Great choice! 🚀 Let's implement the **MinIO Event Listener** to automatically rename uploaded files with Snowflake IDs.  

---

## **Step 1: Enable MinIO Event Notifications**  
MinIO supports event notifications via webhooks. We’ll configure it to send `PUT` (upload) events to our Spring Boot API.  

### **Run This Command in Your Terminal**  
```sh
mc alias set myminio http://localhost:9000 minioadmin minioadmin
mc event add myminio/your-bucket arn:minio:sqs::1:webhook --event put
```
🔹 **This tells MinIO to send an event to our webhook when a file is uploaded.**  

---

## **Step 2: Create the Webhook Listener in Spring Boot**  
We’ll create a REST controller to receive MinIO events and rename files automatically.

### **Install Dependencies (if not installed)**
In your `pom.xml`, add:
```xml
<dependency>
    <groupId>io.minio</groupId>
    <artifactId>minio</artifactId>
    <version>8.5.7</version>
</dependency>
```

---

### **Webhook Controller: Handle MinIO Events**  
Create a new class to handle file renaming when an upload event is received.

```java
import org.springframework.web.bind.annotation.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.minio.*;

@RestController
@RequestMapping("/minio-events")
public class MinIOEventListener {

    private final MinioClient minioClient;
    private final String bucketName = "your-bucket";

    public MinIOEventListener() {
        this.minioClient = MinioClient.builder()
            .endpoint("http://localhost:9000")
            .credentials("minioadmin", "minioadmin")
            .build();
    }

    @PostMapping("/webhook")
    public void handleMinIOEvent(@RequestBody String eventJson) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(eventJson);
            
            String oldFileName = jsonNode.get("Records").get(0).get("s3").get("object").get("key").asText();
            System.out.println("File uploaded: " + oldFileName);

            if (!isValidSnowflakeID(oldFileName)) {
                String newFileName = generateSnowflakeID() + ".json";
                renameFile(oldFileName, newFileName);
                saveMapping(oldFileName, newFileName);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void renameFile(String oldName, String newName) throws Exception {
        minioClient.copyObject(
            CopyObjectArgs.builder()
                .bucket(bucketName)
                .object(newName)
                .source(CopySource.builder()
                    .bucket(bucketName)
                    .object(oldName)
                    .build())
                .build()
        );

        minioClient.removeObject(
            RemoveObjectArgs.builder()
                .bucket(bucketName)
                .object(oldName)
                .build()
        );

        System.out.println("Renamed: " + oldName + " -> " + newName);
    }

    private boolean isValidSnowflakeID(String filename) {
        return filename.matches("\\d{16}\\.json"); // Adjust if needed
    }

    private String generateSnowflakeID() {
        return String.valueOf(SnowflakeIDGenerator.nextID());
    }

    private void saveMapping(String oldName, String newName) {
        // TODO: Save mapping to a database
    }
}
```
🔹 **How This Works:**  
1. MinIO uploads a file → triggers an event.  
2. Your Spring Boot app receives the webhook.  
3. If the filename is not a Snowflake ID, it's renamed.  

---

## **Step 3: Register Webhook in MinIO**  
1. Start your Spring Boot app (`./mvnw spring-boot:run`).  
2. Get your Spring Boot app’s IP (e.g., `http://localhost:8080`).  
3. Update MinIO's event webhook:  
   ```sh
   mc event add myminio/your-bucket arn:minio:sqs::1:webhook --event put --prefix '' --suffix '' --target http://localhost:8080/minio-events/webhook
   ```
4. **Upload a test file to MinIO**  
   ```sh
   mc alias set myminio http://localhost:9000 minioadmin minioadmin
   mc cp test.json myminio/your-bucket/
   ```

---

### ✅ **Expected Behavior**
- Your webhook receives the event.  
- The file is renamed to a **Snowflake ID** format (e.g., `3423423412341234.json`).  
- The old filename is mapped to the new one (TODO: save it in a database).  

---

## **Next Steps**
- Would you like to store the filename mapping in a **database** (e.g., PostgreSQL, MySQL)?  
- Would you like the ETL pipeline to trigger **automatically** after renaming?


Great! Let's integrate **MySQL** to store the mapping between the old and new filenames, and trigger the **ETL pipeline** automatically after the file is renamed/uploaded.

### **Step 1: Save the File Mapping in MySQL**
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

#### **1.3 Modify the Controller to Save Mappings**

Update the `MinIOEventListener` controller to save the mapping into the database after renaming the file.

```java
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.minio.*;

@RestController
@RequestMapping("/minio-events")
public class MinIOEventListener {

    private final MinioClient minioClient;
    private final String bucketName = "your-bucket";
    
    @Autowired
    private FileMappingRepository fileMappingRepository;

    public MinIOEventListener() {
        this.minioClient = MinioClient.builder()
            .endpoint("http://localhost:9000")
            .credentials("minioadmin", "minioadmin")
            .build();
    }

    @PostMapping("/webhook")
    public void handleMinIOEvent(@RequestBody String eventJson) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(eventJson);
            
            String oldFileName = jsonNode.get("Records").get(0).get("s3").get("object").get("key").asText();
            System.out.println("File uploaded: " + oldFileName);

            if (!isValidSnowflakeID(oldFileName)) {
                String newFileName = generateSnowflakeID() + ".json";
                renameFile(oldFileName, newFileName);
                saveMapping(oldFileName, newFileName);

                // Trigger ETL pipeline automatically after renaming
                triggerETLPipeline(newFileName);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void renameFile(String oldName, String newName) throws Exception {
        minioClient.copyObject(
            CopyObjectArgs.builder()
                .bucket(bucketName)
                .object(newName)
                .source(CopySource.builder()
                    .bucket(bucketName)
                    .object(oldName)
                    .build())
                .build()
        );

        minioClient.removeObject(
            RemoveObjectArgs.builder()
                .bucket(bucketName)
                .object(oldName)
                .build()
        );

        System.out.println("Renamed: " + oldName + " -> " + newName);
    }

    private boolean isValidSnowflakeID(String filename) {
        return filename.matches("\\d{16}\\.json"); // Adjust if needed
    }

    private String generateSnowflakeID() {
        return String.valueOf(SnowflakeIDGenerator.nextID());
    }

    private void saveMapping(String oldName, String newName) {
        FileMapping fileMapping = new FileMapping(oldName, newName);
        fileMappingRepository.save(fileMapping);
        System.out.println("Saved mapping: " + oldName + " -> " + newName);
    }

    private void triggerETLPipeline(String newFileName) {
        // Here we call your ETL service to process the newly renamed file
        System.out.println("Triggering ETL pipeline for file: " + newFileName);
        // You can use your existing ETL service, for example:
        // etlUseCase.runETL(newFileName);
    }
}
```

---

### **Step 2: Automatically Trigger the ETL Pipeline**

In the `triggerETLPipeline` method, you can call the ETL pipeline for processing the newly renamed file. We will assume that your ETL service expects a filename (or file ID) to process.

```java
private void triggerETLPipeline(String newFileName) {
    // Call your ETL service here
    // Example:
    // etlUseCase.runETL(newFileName);
}
```

This method can be replaced with the actual implementation of your ETL trigger.

---

### **Step 3: Testing**

1. **Upload a file** to MinIO via the **`mc cp`** command or through the MinIO UI.
2. The webhook will be triggered, the file will be renamed with a Snowflake ID, the mapping will be saved in MySQL, and the ETL pipeline will be triggered automatically.

---

### **Step 4: Additional Considerations**

- You might want to handle **retries** in case of failures when interacting with MinIO or MySQL.
- You could also **log** events more robustly for debugging and auditing purposes.
- You could add **validation** on whether the file uploaded is valid for ETL processing, such as checking file type, size, etc.

---

Let me know if you need help with anything else, such as the actual ETL processing part or additional tests!