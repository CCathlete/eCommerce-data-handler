package org.webcat.ecommerce.datahandler.infrastructure.database;

import org.webcat.ecommerce.datahandler.domain.model.entities.RawData;
import org.webcat.ecommerce.datahandler.infrastructure.repository.RawDataRepository;

public class MySQLRawDataRepository implements RawDataRepository {
  @Override
  public RawData findById(Long id) {
    return null; // TODO: implement.
  }
}