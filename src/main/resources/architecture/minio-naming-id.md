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

#### ✅ **2. Generate Snowflake ID in the MinIO event handler**  
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
  public ResponseEntity<String> handleMinioEvent(@RequestBody String eventData)
  {
    try {
      // Parse the event data as JSON
      JsonNode eventJson = objectMapper.readTree(eventData);

      // Extract relevant details from the event
      String eventName = eventJson.path("Records").get(0).path("eventName").asText();
      String fileName = eventJson.path("Records").get(0).path("s3").path("object").path("key").asText();

      // Log the event and extracted file name
      System.out.printf("Received event: %s, File: %s\n", eventName, fileName);

      // Pass the file name (and event if needed) to your use case or service layer
      // For example, you can trigger a specific method in your use case to handle the event
      etlUseCase.handleMinioEvent(eventName, fileName);

      return ResponseEntity.ok("Event processed");
    } catch (Exception e) {
      // Handle any parsing or processing errors
      e.printStackTrace();
      return ResponseEntity.status(500).body("Error processing event");
    }
  }
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