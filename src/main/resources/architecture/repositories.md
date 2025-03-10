For a **realistic but local** setup, you can:  

### 1️⃣ **Use MinIO for a Local Data Lake**  
MinIO is an S3-compatible object store that runs locally. It mimics AWS S3.  
- Store raw data as **JSON, CSV, or Parquet** files in MinIO.  
- The `RawDataRepository` will interact with MinIO instead of MySQL.  

### 2️⃣ **Keep MySQL as the Data Warehouse**  
- Processed data should be stored in **MySQL**, simulating a data warehouse.  
- `ProcessedDataRepository` stays in MySQL.  

---

### **Implementation Plan**  

✅ **Step 1: Run MinIO locally (Docker)**  
Add this to `docker-compose.yaml`:  

```yaml
version: '3.8'
services:
  minio:
    image: quay.io/minio/minio
    container_name: minio
    ports:
      - "9000:9000"
      - "9001:9001"
    environment:
      MINIO_ROOT_USER: minioadmin
      MINIO_ROOT_PASSWORD: minioadmin
    command: server /data --console-address ":9001"
```
Access MinIO UI at `http://localhost:9001` (user: `minioadmin`, password: `minioadmin`).  

✅ **Step 2: Add MinIO Client SDK (Spring Boot Example)**  
```xml
<dependency>
    <groupId>io.minio</groupId>
    <artifactId>minio</artifactId>
    <version>8.5.7</version>
</dependency>
```

✅ **Step 3: Implement `RawDataRepository` (MinIO)**  
```java
import io.minio.*;
import io.minio.errors.*;
import java.io.InputStream;

public class MinIORawDataRepository implements RawDataRepository {
    private final MinioClient minioClient;
    private final String bucketName = "raw-data";

    public MinIORawDataRepository() {
        this.minioClient = MinioClient.builder()
            .endpoint("http://localhost:9000")
            .credentials("minioadmin", "minioadmin")
            .build();
    }

    public void saveRawData(String objectName, InputStream data) throws Exception {
        minioClient.putObject(
            PutObjectArgs.builder()
                .bucket(bucketName)
                .object(objectName)
                .stream(data, -1, 10485760)
                .contentType("application/json")
                .build()
        );
    }

    public InputStream getRawData(String objectName) throws Exception {
        return minioClient.getObject(
            GetObjectArgs.builder().bucket(bucketName).object(objectName).build()
        );
    }
}
```
This stores raw data **as JSON in MinIO**, instead of MySQL.  

✅ **Step 4: ProcessedDataRepository (MySQL stays the same)**  
- The **ProcessedDataRepository** continues using MySQL for structured, optimized data.  
- The **Transform service** loads from `RawDataRepository`, processes it, and saves to `ProcessedDataRepository`.  

---

### **Summary: Realistic Local Setup**  
✅ **MinIO** → Raw Data (like a Data Lake)  
✅ **MySQL** → Processed Data (like a Data Warehouse)  
✅ **ETL Flow**: **Extract (MinIO) → Transform → Load (MySQL)**  

This keeps your setup **realistic** while still being **fully local**. 🚀  
Would you like to extend this with specific file formats like Parquet?

Got it! We'll start with **ID-encoded object names** in MinIO while keeping the code modular so you can later switch to an SQL mapping.  

<h2>Encoding the id in the object name</h2>

### **Plan**  
1. **Define a method to generate object names** → `data_{id}.json`  
2. **Implement MinIO file storage & retrieval**  
3. **Use an interface** to keep it modular for future SQL integration  

---

### **1. Define the Object Naming Strategy**  
We'll use a function to generate object names from IDs:  

```java
public class ObjectNameGenerator {
    public static String generateObjectName(String id) {
        return "raw-data/" + id + ".json"; 
    }
}
```

This ensures **all IDs map consistently** to object names.

---

### **2. Implement MinIO Storage & Retrieval**  
Now, we add MinIO methods using this naming convention.  
You can convert an `InputStream` to a `RawData` entity using **Jackson** (`ObjectMapper`) if `RawData` is a POJO. Here's how:  

### **1. Convert `InputStream` to `RawData`**
If `RawData` is stored as JSON in MinIO, use Jackson to deserialize it:  

```java
import com.fasterxml.jackson.databind.ObjectMapper;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import org.webcat.ecommerce.datahandler.domain.model.entities.RawData;
import java.io.InputStream;

public class MinioRawDataRepository implements RawDataRepository {
    private final MinioClient minioClient;
    private final String bucketName = "raw-data-bucket";
    private final ObjectMapper objectMapper = new ObjectMapper(); // Jackson object mapper

    public MinioRawDataRepository(String endpoint, String accessKey, String secretKey) {
        this.minioClient = MinioClient.builder()
            .endpoint(endpoint)
            .credentials(accessKey, secretKey)
            .build();
    }

    @Override
    public RawData findById(Long id) {
        try {
            String objectName = ObjectNameGenerator.generate(id);
            InputStream inputStream = minioClient.getObject(
                GetObjectArgs.builder().bucket(bucketName).object(objectName).build()
            );

            // Convert InputStream to RawData
            return objectMapper.readValue(inputStream, RawData.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch raw data for ID: " + id, e);
        }
    }

    @Override
    public Boolean save(RawData rawData) {
        try {
            String objectName = ObjectNameGenerator.generate(rawData.getId());
            String json = objectMapper.writeValueAsString(rawData);
            InputStream dataStream = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8));

            minioClient.putObject(
                io.minio.PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .stream(dataStream, json.length(), -1)
                    .contentType("application/json")
                    .build()
            );

            return true;
        } catch (Exception e) {
            throw new RuntimeException("Failed to save raw data", e);
        }
    }

    private static class ObjectNameGenerator {
        static String generate(Long id) {
            return "raw-data/" + id + ".json";
        }
    }
}
```

### **Why Keep the Interface?**
- **No need to change `RawDataRepository`** – you just handle deserialization in the implementation.  
- **Jackson converts `InputStream` → `RawData`** transparently.  
- **Keeps the API clean** while allowing efficient MinIO integration.  

Would you like a different serialization format (e.g., XML or binary)? 🚀


<h1>Data warehouse (processed data repo) with Hibernate</h1>

To use Hibernate, update your **ProcessedDataRepository** and **MySQLProcessedDataRepository** like this:  

If you want to keep the flexibility to have multiple implementations for `ProcessedDataRepository`, **do not extend `JpaRepository` directly in your interface**. Instead, create a separate implementation that does.  

1. **Define a generic repository interface:**
   ```java
   public interface ProcessedDataRepository {
       ProcessedData findById(Long id);
       Boolean save(ProcessedData processedData);
   }
   ```
   
2. **Create a JPA-based implementation:**
   ```java
   import org.springframework.data.jpa.repository.JpaRepository;
   import org.springframework.stereotype.Repository;

   @Repository
   public interface JpaProcessedDataRepository extends JpaRepository<ProcessedData, Long> {
   }
   ```

3. **Implement `ProcessedDataRepository` using JPA:**
   ```java
   import org.springframework.beans.factory.annotation.Autowired;
   import org.springframework.stereotype.Repository;

   @Repository
   public class MySQLProcessedDataRepository implements ProcessedDataRepository {

       private final JpaProcessedDataRepository jpaRepo;

       @Autowired
       public MySQLProcessedDataRepository(JpaProcessedDataRepository jpaRepo) {
           this.jpaRepo = jpaRepo;
       }

       @Override
       public ProcessedData findById(Long id) {
           return jpaRepo.findById(id).orElse(null);
       }

       @Override
       public Boolean save(ProcessedData processedData) {
           jpaRepo.save(processedData);
           return true;
       }
   }
   ```

### **Why This Approach?**
- **You can add different implementations** (e.g., a NoSQL repository, an in-memory implementation).
- **Spring Data JPA works automatically** with `JpaRepository`, but your application still depends only on `ProcessedDataRepository`, making it easy to swap implementations.  
- **Keeps your code decoupled** and follows **Dependency Inversion**.

Would you like to discuss alternative approaches or potential refinements?

3️⃣ Make ProcessedData an Entity
Ensure it's a Hibernate entity:
```java
package org.webcat.ecommerce.datahandler.domain.model.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "processed_data")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProcessedData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String content;  // Adjust fields as needed
}
Now Spring Data JPA will handle everything! Let me know if you need custom queries. 🚀
```
✅ No need for `@Repository` because Spring Data JPA detects it.

---

### **2. Inject it into the "Load" Domain Service**
```java
@Service
public class LoadService {
    private final ProcessedDataRepository processedDataRepository;

    public LoadService(ProcessedDataRepository processedDataRepository) {
        this.processedDataRepository = processedDataRepository;
    }

    public void saveProcessedData(ProcessedData data) {
        processedDataRepository.save(data);
    }
}
```
✅ The **Load service** handles storing the transformed data using the repository.

---

### **3. Use the "Load" Service in Your ETL Use Case**
```java
@Component
public class ETLUseCase {
    private final ExtractService extractService;
    private final TransformService transformService;
    private final LoadService loadService;

    public ETLUseCase(ExtractService extractService, 
                      TransformService transformService, 
                      LoadService loadService) {
        this.extractService = extractService;
        this.transformService = transformService;
        this.loadService = loadService;
    }

    public void executeETL() {
        RawData rawData = extractService.extract();
        ProcessedData processedData = transformService.transform(rawData);
        loadService.saveProcessedData(processedData);
    }
}
```
✅ The **ETL use case orchestrates** the Extract, Transform, and Load process.

---


<h1>Creating the entities to fit the tables</h1>

```java
@Entity
@Table(name = "processed_data")
public class ProcessedData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "raw_data_key", nullable = false, unique = true)
    private String rawDataKey;  // Stores the MinIO object key instead of a DB foreign key

    @Column(nullable = false, columnDefinition = "JSON")
    private String transformedData;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @Column(name = "processed_at", updatable = false, insertable = false)
    private Timestamp processedAt;

    public enum Status {
        PENDING, SUCCESS, FAILED
    }

    // Getters & setters
}
```
