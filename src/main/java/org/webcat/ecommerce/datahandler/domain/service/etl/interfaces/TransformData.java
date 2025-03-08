package org.webcat.ecommerce.datahandler.domain.service.etl.interfaces;

import org.webcat.ecommerce.datahandler.domain.model.entities.ProcessedData;
import org.webcat.ecommerce.datahandler.domain.model.entities.RawData;

public interface TransformData
{
  ProcessedData transform(
      RawData rawData);
}
