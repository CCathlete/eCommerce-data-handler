### **Configuring a MinIO Webhook (Using the UI)**  
Minio would register the webhook only if the spring app is online because Minio checks the endpoint it sends the events to.


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