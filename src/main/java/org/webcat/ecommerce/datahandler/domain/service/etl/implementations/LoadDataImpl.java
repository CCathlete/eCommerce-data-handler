package org.webcat.ecommerce.datahandler.domain.service.etl.implementations;

import org.webcat.ecommerce.datahandler.domain.model.entities.ProcessedData;
import org.webcat.ecommerce.datahandler.domain.service.etl.interfaces.LoadData;
import org.webcat.ecommerce.datahandler.infrastructure.repository.ProcessedDataRepository;

public class LoadDataImpl
    implements LoadData
{
  ProcessedDataRepository repo;

  public LoadDataImpl(
      ProcessedDataRepository repo)
  {
    this.repo = repo;
  }

  @Override
  public Boolean save(
      ProcessedData processedData)
  {
    return this.repo
        .save(processedData);
  }
}
