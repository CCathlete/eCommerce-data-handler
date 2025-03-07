package org.webcat.ecommerce.datahandler.infrastructure.database;

import org.webcat.ecommerce.datahandler.domain.model.entities.ProcessedData;
import org.webcat.ecommerce.datahandler.infrastructure.repository.ProcessedDataRepository;

public class MySQLProcessedDataRepository implements ProcessedDataRepository {
  @Override
  public ProcessedData findById(Long id) {
    return null; // TODO: implement.
  }
}
