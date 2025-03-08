package org.webcat.ecommerce.datahandler.domain.service.etl.implementations;

import org.webcat.ecommerce.datahandler.domain.model.entities.ProcessedData;
import org.webcat.ecommerce.datahandler.domain.model.entities.RawData;
import org.webcat.ecommerce.datahandler.domain.service.etl.interfaces.TransformData;

public class TransformDataImpl
    implements TransformData
{
  @Override
  public ProcessedData transform(
      RawData rawData)
  {
    String processedContent = rawData
        .getRawContent().toUpperCase();
    // The ID is given during loading to a repo.
    return new ProcessedData(null,
        processedContent);
  }
}
