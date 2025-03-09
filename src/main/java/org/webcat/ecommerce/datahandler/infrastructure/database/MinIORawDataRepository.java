package org.webcat.ecommerce.datahandler.infrastructure.database;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.springframework.stereotype.Repository;
import org.webcat.ecommerce.datahandler.domain.model.entities.RawData;
import org.webcat.ecommerce.datahandler.infrastructure.repository.RawDataRepository;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.github.cdimascio.dotenv.Dotenv;


/**
 *
 * {@link MinIORawDataRepository}
 * <p>
 * An S3 compatible object store that runs locally. It mimics AWS S3. Serves as a data lake.
 * </p>
 */
@Repository
public class MinIORawDataRepository
    implements RawDataRepository
{

  // Loading environment variables.
  Dotenv env = Dotenv.load();
  private final MinioClient lakeClient;
  private final String bucketName =
      "raw-data";
  // Jackson object mapper.
  private final ObjectMapper objectMapper =
      new ObjectMapper();

  // Generating object names from ids.
  private static class ObjectNameGenerator
  {
    // TODO check.
    // The class is private so its methods are also private.
    static String generateObjectName(
        Long id)
    {
      return "raw-data/" + id + ".json";
    }
  }

  // Constructor.
  public MinIORawDataRepository()
  {
    this.lakeClient = MinioClient
        .builder()
        .endpoint(env.get("MINIO_URL"))
        .credentials(
            env.get("MINIO_USER"),
            env.get("MINIO_PASS"))
        .build();
  }

  // Getting data by ID.
  @Override
  public RawData findById(Long id)
  {
    try
    {
      String objectName =
          ObjectNameGenerator
              .generateObjectName(id);
      InputStream inputStream =
          this.lakeClient.getObject(
              GetObjectArgs.builder()
                  .bucket(bucketName)
                  .object(objectName)
                  .build());

      // Reads data form input stream into RawData object.
      return objectMapper.readValue(
          inputStream, RawData.class);
    } catch (Exception e)
    {
      throw new RuntimeException(
          "Failed to fetch raw data with id: "
              + id,
          e);
    }
  }

  /**
   * Saves raw data in the minio repository.
   */
  @Override
  public Boolean save(RawData rawData)
  {
    try
    {
      String objectName =
          ObjectNameGenerator
              .generateObjectName(
                  rawData.getID());
      String json = this.objectMapper
          .writeValueAsString(rawData);
      // Turning the data into a json string and then turning it into
      // a byte array input stream (sends one byte after the other).
      InputStream inputStream =
          new ByteArrayInputStream(
              json.getBytes(
                  StandardCharsets.UTF_8));

      this.lakeClient.putObject(
          PutObjectArgs.builder()
              .bucket(this.bucketName)
              .object(objectName)
              .stream(inputStream,
                  json.length(), -1)
              .contentType(
                  "application/json")
              .build());
      return true;
    } catch (Exception e)
    {
      throw new RuntimeException(
          "Failed to save raw data", e);
    }
  }
}
