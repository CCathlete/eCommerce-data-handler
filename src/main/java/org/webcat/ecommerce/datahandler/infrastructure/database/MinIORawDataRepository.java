package org.webcat.ecommerce.datahandler.infrastructure.database;

import org.springframework.stereotype.Repository;
import org.webcat.ecommerce.datahandler.domain.model.entities.RawData;
import org.webcat.ecommerce.datahandler.infrastructure.repository.RawDataRepository;

import io.minio.MinioClient;
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

  Dotenv env = Dotenv.load();
  private final MinioClient lakeClient;
  private final String bucketName =
      "raw-data";

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

  @Override
  public RawData findById(Long id)
  {
    return null; // TODO: implement.
  }
}
