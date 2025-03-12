### **Configuring a MinIO Webhook (Using cURL, No `mc`)**  

MinIO supports **event notifications** via webhooks. You'll configure MinIO to send an HTTP POST request to your API whenever a file is uploaded.

---

### **Step 1: Start MinIO Event Listener**
First, ensure your MinIO instance is **running inside the container**.

---

### **Step 2: Configure the Webhook Notification Target**
Use **cURL** to set up a webhook that notifies your API when a new file is uploaded.

```sh
. .env
curl -X POST "http://<MINIO_HOST>:9000/minio/admin/v3/add/webhook" \
  -H "Content-Type: application/json" \
  --user "$MINIO_ACCESS_KEY:$MINIO_SECRET_KEY" \
  -d '{
    "config": {
      "endpoint": "http://<YOUR_APP_HOST>:<YOUR_PORT>/etl/webhook",
      "queueDir": "/tmp/minio-events",
      "queueLimit": 100000
    },
    "comment": "Send events to ETL service"
  }'

```

#### **Replace:**
- `<MINIO_HOST>` → MinIO container's hostname (e.g., `localhost` or `minio`)
- `<ACCESS_TOKEN>` → MinIO admin token (if required)
- `<YOUR_APP_HOST>` → Your Spring Boot app's host (`host.docker.internal` if running outside Docker)
- `<YOUR_PORT>` → Your Spring Boot app's port (e.g., `8080`)

---

### **Step 3: Set Up Event Triggers for a Bucket**
Now, tell MinIO to **send events** when files are uploaded to a specific bucket.

```sh
curl -X PUT "http://<MINIO_HOST>:9000/minio/admin/v3/set/webhook/notifications" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <ACCESS_TOKEN>" \
  -d '{
    "bucket": "<YOUR_BUCKET>",
    "events": ["s3:ObjectCreated:*"],
    "configId": "etl-webhook",
    "destination": "http://<YOUR_APP_HOST>:<YOUR_PORT>/etl/webhook"
  }'
```

#### **Replace:**
- `<YOUR_BUCKET>` → Your MinIO bucket name  
- `"s3:ObjectCreated:*"` → Triggers webhook when a file is uploaded  

---

### **Step 4: Restart MinIO to Apply Changes**
```sh
docker restart <minio-container-name>
```

---

### **Step 5: Verify Webhook Events**
After uploading a file to MinIO:
```sh
curl -X PUT --upload-file test.json http://<MINIO_HOST>:9000/<YOUR_BUCKET>/test.json
```
You should see a **POST request** hitting your Spring Boot `/etl/webhook` endpoint.

---

### **Now, your ETL pipeline triggers automatically on file uploads! 🚀**


Yes, you should **generate the Snowflake ID when handling the MinIO event**, not in the `RawData` constructor.

### **Why?**  
1. **Ensures MinIO and MySQL stay in sync** → You need the same Snowflake ID in both places.  
2. **Prevents duplicate IDs** → If `RawData` generates its own ID, it might not match the file name in MinIO.  
3. **Allows manual uploads** → If someone uploads manually, your app will still rename the file and save the mapping properly.  

---

### **What to Change?**
#### ✅ **1. Remove ID generation from `RawData`**  
```java
public class RawData {
  private Long id;
  private String rawContent;

  public RawData(Long id, String rawContent) {
    this.id = id;  // Now it accepts an ID instead of generating it
    this.rawContent = rawContent;
  }

  public Long getID() { return this.id; }
  public String getRawContent() { return this.rawContent; }
}
```

---

#### ✅ **2. Generate Snowflake ID in the MinIO event handler**  
Inside `ETLController`:
```java
@PostMapping("/webhook")
public ResponseEntity<String> handleMinioEvent(@RequestBody String eventData) {
    System.out.println("Received MinIO event: " + eventData);
    
    // Extract file name
    String rawFileName = extractFileName(eventData);
    
    // Generate a Snowflake ID
    Long snowflakeID = SnowflakeIDGenerator.generateId();
    String newFileName = snowflakeID + ".json";
    
    // Rename file in MinIO
    renameFileInMinio(rawFileName, newFileName);
    
    // Save mapping in MySQL
    saveFileMapping(rawFileName, newFileName);
    
    // Trigger ETL with the correct ID
    RawData rawData = new RawData(snowflakeID, newFileName);
    ETLRequestDTO request = new ETLRequestDTO(rawData);
    etlUseCase.runETL(request);
    
    return ResponseEntity.ok("ETL started for: " + newFileName);
}
```

---

Now, **every uploaded file gets a Snowflake ID immediately** and is **correctly mapped in MySQL and MinIO**. 🚀


### **Save the File Mapping in MySQL**
We'll create a new **FileMapping** entity that will store the original file name and the new Snowflake ID name in the MySQL database.

---

#### **1.1 Create the FileMapping Entity**

Create a new entity `FileMapping.java` to store the file name mappings.

```java
package org.webcat.ecommerce.datahandler.domain.model.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "file_mapping")
public class FileMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "original_name", nullable = false)
    private String originalName;

    @Column(name = "new_name", nullable = false)
    private String newName;

    // Default constructor, getters, and setters
    public FileMapping() {}

    public FileMapping(String originalName, String newName) {
        this.originalName = originalName;
        this.newName = newName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOriginalName() {
        return originalName;
    }

    public void setOriginalName(String originalName) {
        this.originalName = originalName;
    }

    public String getNewName() {
        return newName;
    }

    public void setNewName(String newName) {
        this.newName = newName;
    }
}
```

#### **1.2 Create the Repository Interface**

Create a repository interface for `FileMapping`:

```java
package org.webcat.ecommerce.datahandler.domain.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.webcat.ecommerce.datahandler.domain.model.entities.FileMapping;

public interface FileMappingRepository extends JpaRepository<FileMapping, Long> {
    FileMapping findByOriginalName(String originalName);
}
```