package org.webcat.ecommerce.datahandler.domain.service.etl.interfaces;

import org.webcat.ecommerce.datahandler.domain.model.entities.ProcessedData;

public interface LoadData
{
  Boolean save(
      ProcessedData processedData);
}
