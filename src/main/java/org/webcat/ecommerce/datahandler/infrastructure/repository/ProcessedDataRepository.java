package org.webcat.ecommerce.datahandler.infrastructure.repository;

import org.webcat.ecommerce.datahandler.domain.model.entities.ProcessedData;

public interface ProcessedDataRepository {
  ProcessedData findById(Long id);
}