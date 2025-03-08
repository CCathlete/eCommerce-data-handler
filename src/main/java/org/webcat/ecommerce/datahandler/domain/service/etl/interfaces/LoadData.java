package org.webcat.ecommerce.datahandler.domain.service.etl.interfaces;

import org.webcat.ecommerce.datahandler.domain.model.entities.ProcessedData;

/**
 *
 * {@link LoadData}
 * <p>
 * A domain service that calls the processed data repository to save data.
 * </p>
 * <h4>Methods:</h4> {@method Boolean save(ProcessedData processedData)}
 */
public interface LoadData
{
  Boolean save(
      ProcessedData processedData);
}
