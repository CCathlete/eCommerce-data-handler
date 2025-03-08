package org.webcat.ecommerce.datahandler.infrastructure.repository;

import org.webcat.ecommerce.datahandler.domain.model.entities.ProcessedData;

/**
 * {@link ProcessedDataRepository}
 * <p>
 * Repository for processed data that implements CRUD operations.
 * </p>
 * <h4>Methods:</h4>
 * <li>{@method ProcessedData findById(Long id)}</li>
 * <li>{@method Boolean save(ProcessedData processedData)}</li>
 */
public interface ProcessedDataRepository
{
  ProcessedData findById(Long id);

  Boolean save(
      ProcessedData processedData);
}
