package org.webcat.ecommerce.datahandler.infrastructure.database;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.springframework.stereotype.Repository;
import org.webcat.ecommerce.datahandler.domain.model.entities.RawData;
import org.webcat.ecommerce.datahandler.infrastructure.repository.RawDataRepository;
import org.webcat.ecommerce.datahandler.presentation.controllers.ETLController;
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

  private final ETLController ETLController;

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
      return "raw-data_" + id + ".json";
    }
  }

  public String generateObjectName(
      Long id)
  {
    return ObjectNameGenerator
        .generateObjectName(id);
  }

  // Constructor.
  public MinIORawDataRepository(
      ETLController ETLController)
  {
    // System.out.println("MINIO_HOST: "
    // + env.get("MINIO_HOST"));
    // System.out.println("MINIO_PORT: "
    // + env.get("MINIO_PORT"));
    // System.out.println(
    // "MINIO_ACCESS_KEY: " + env
    // .get("MINIO_ACCESS_KEY"));
    // System.out.println(
    // "MINIO_SECRET_KEY: " + env
    // .get("MINIO_SECRET_KEY"));

    // Instantiating minio client.
    this.lakeClient = MinioClient
        .builder()
        .endpoint("http://"
            + env.get("MINIO_HOST")
            + ":"
            + env.get("MINIO_API_PORT"))
        .credentials(
            env.get("MINIO_ACCESS_KEY"),
            env.get("MINIO_SECRET_KEY"))
        .build();
    // System.out.println("MINIO_HOST: "
    // + env.get("MINIO_HOST"));
    // System.out.println("MINIO_PORT: "
    // + env.get("MINIO_PORT"));
    // System.out.println(
    // "MINIO_ACCESS_KEY: " + env
    // .get("MINIO_ACCESS_KEY"));
    // System.out.println(
    // "MINIO_SECRET_KEY: " + env
    // .get("MINIO_SECRET_KEY"));

    // Instantiating minio client.
    this.ETLController = ETLController;
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
