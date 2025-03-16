package org.webcat.ecommerce.datahandler.infrastructure.repository;

import org.webcat.ecommerce.datahandler.domain.model.entities.RawData;

public interface RawDataRepository
{
  RawData findById(Long id);

  RawData findByFileName(
      String fileName);

  Boolean save(RawData rawData);
}
