package org.webcat.ecommerce.datahandler.infrastructure.database;

import org.springframework.stereotype.Repository;
import org.webcat.ecommerce.datahandler.domain.model.entities.ProcessedData;
import org.webcat.ecommerce.datahandler.infrastructure.repository.ProcessedDataRepository;

@Repository
public class MySQLProcessedDataRepository
    implements ProcessedDataRepository
{
  @Override
  public ProcessedData findById(Long id)
  {
    return null; // TODO: implement.
  }

  public Boolean save(
      ProcessedData processedData)
  {
    return true; // TODO implement.
  }
}
