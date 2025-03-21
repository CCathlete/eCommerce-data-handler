package org.webcat.ecommerce.datahandler.domain.service.etl.implementations;

import java.sql.Timestamp;

import org.springframework.stereotype.Service;
import org.webcat.ecommerce.datahandler.domain.model.entities.ProcessedData;
import org.webcat.ecommerce.datahandler.domain.model.entities.RawData;
import org.webcat.ecommerce.datahandler.domain.service.etl.interfaces.TransformData;

@Service
public class TransformDataImpl
    implements TransformData
{
  /**
   * Transforms raw data into processed data by converting its content to uppercase. The resulting
   * processed data does not include an ID, which is assigned during the loading phase into a
   * repository.
   *
   * @param rawData The raw data to be transformed.
   * @return A ProcessedData object with the transformed content.
   */
  @Override
  public ProcessedData transform(
      RawData rawData)
  {
    // The processed data ID is generated by hibernate during loading to a repo.
    String processedContent = rawData
        .getRawContent().toUpperCase();

    Long rDataID = rawData.getID();

    ProcessedData.Status status =
        ProcessedData.Status.SUCCESS;

    Timestamp processedAt =
        new Timestamp(
            System.currentTimeMillis());

    return new ProcessedData(rDataID,
        processedContent, status,
        processedAt);
  }
}
