### **Configuring a MinIO Webhook (Using the UI)**  
Minio would register the webhook only if the spring app is online because Minio checks the endpoint it sends the events to.


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

#### ✅ **2. Call the handleMinioEvent method of etlUseCase**  
Inside `ETLController`:
```java
package org.webcat.ecommerce.datahandler.presentation.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.webcat.ecommerce.datahandler.application.dtos.ETLRequestDTO;
import org.webcat.ecommerce.datahandler.application.dtos.ETLResponseDTO;
import org.webcat.ecommerce.datahandler.application.use_cases.interfaces.ETL;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/etl")
public class ETLController
{
  private final ETL etlUseCase;
  private final ObjectMapper objectMapper; // For parsing JSON

  public ETLController(ETL etlUseCase, ObjectMapper objectMapper)
  {
    this.etlUseCase = etlUseCase;
    this.objectMapper = objectMapper;
  }

  // Handler for starting the ETL process.
  @PostMapping("/run")
  public ResponseEntity<ETLResponseDTO> sendToETL(
      @RequestBody ETLRequestDTO request)
  {
    ETLResponseDTO response = this.etlUseCase.runETL(request);

    if (response == null)
    {
      return ResponseEntity.badRequest().build();
    }

    return ResponseEntity.ok(response);
  }

  // Handler for checking the status of the ETL process.
  @GetMapping("/status/{processID}")
  public ResponseEntity<ETLResponseDTO> checkETLStatus(
      @RequestParam String processID)
  {
    return ResponseEntity.ok(etlUseCase.checkETLStatus(processID));
  }

  // Handler for receiving MinIO event notifications.
  @PostMapping("/webhook")
  public ResponseEntity<ETLResponseDTO> handleMinioEvent(
      @RequestBody String eventData)
  {
    try
    {
      // Parsing the event's data as json.
      JsonNode eventJson =
          this.objectMApper
              .readTree(eventData);

      // Checking if the event is valid.
      if (!eventJson.has("Records")
          || eventJson.get("Records")
              .isEmpty())
      {
        return ResponseEntity
            .badRequest().build();
      }

      // Extracting relevant details from the json.
      String eventName = eventJson
          .path("Records").get(0)
          .path("eventName").asText();

      String fileName = eventJson
          .path("Records").get(0)
          .path("s3").path("object")
          .path("key").asText();

      // Logging the event and the extracted file name.
      System.out.printf(
          "Received event: %s\nFile name: %s\n",
          eventName, fileName);

      // Calling the ETL use case to process the file.
      ETLResponseDTO response =
          this.etlUseCase
              .handleMinioEvent(
                  eventName, fileName);

      if (response == null)
      {
        return ResponseEntity
            .badRequest().build();
      }

      return ResponseEntity
          .ok(response);
    } catch (Exception e)
    {
      e.printStackTrace();
      // See why I can't return a body.
      return ResponseEntity.status(500)
          .build();
    }

  }
}
```

---

Now, **every uploaded file gets a Snowflake ID immediately** and is **correctly mapped in MySQL and MinIO**. 🚀

### **Use case method implementation**

```java
import org.springframework.stereotype.Service;
import org.webcat.ecommerce.datahandler.application.dtos.ETLResponseDTO;
import org.webcat.ecommerce.datahandler.application.use_cases.interfaces.ETL;
import org.webcat.ecommerce.datahandler.infrastructure.utils.SnowflakeGenerator;

@Service
public class ETLMinImp implements ETL {

    private final SnowflakeGenerator snowflakeGenerator;

    public ETLMinImp(SnowflakeGenerator snowflakeGenerator) {
        this.snowflakeGenerator = snowflakeGenerator;
    }

    @Override
    public ETLResponseDTO handleMinioEvent(String eventName, String fileName) {
        long snowflakeId = snowflakeGenerator.generateId();

        System.out.printf("Processing file: %s, Event: %s, Snowflake ID: %d\n", fileName, eventName, snowflakeId);

        return new ETLResponseDTO("Processing started for " + fileName, snowflakeId);
    }
}

```


### **Save the File Mapping in MySQL**
We'll create a new **FileMapping** entity that will store the original file name and the new Snowflake ID name in the MySQL database.

---

#### **1.1 Create the FileMapping Entity**

Create a new entity `FileMapping.java` to store the file name mappings.

```java
package org.webcat.ecommerce.datahandler.domain.model.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "file_mapping")
public class FileMapping
{

  @Id
  @GeneratedValue(
      strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "original_name",
      nullable = false)
  private String originalName;

  @Column(name = "new_name",
      nullable = false)
  private String newName;

  // Constructor.
  public FileMapping(
      String originalName,
      String newName)
  {
    this.originalName = originalName;
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

<h1> Renaming a file in MinIO</h1>

1. Copy the file to a new name.  
2. Delete the original file.  

If `MinIORawDataRepository` is your MinIO integration, add a method like this:  

```java
public boolean renameObject(String oldName, String newName) {
    try {
        // Copy the object to a new name
        minioClient.copyObject(
            CopyObjectArgs.builder()
                .source(CopySource.builder()
                    .bucket(bucketName)
                    .object(oldName)
                    .build())
                .bucket(bucketName)
                .object(newName)
                .build()
        );

        // Delete the old object
        minioClient.removeObject(
            RemoveObjectArgs.builder()
                .bucket(bucketName)
                .object(oldName)
                .build()
        );

        return true;
    } catch (Exception e) {
        e.printStackTrace();
        return false;
    }
}
```

Then, call `renameObject(fileName, newName)` in your `normaliseNameID` method. Let me know if you need adjustments!