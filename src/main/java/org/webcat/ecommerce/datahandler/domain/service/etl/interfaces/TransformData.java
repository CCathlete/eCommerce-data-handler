package org.webcat.ecommerce.datahandler.domain.service.etl.interfaces;

import org.webcat.ecommerce.datahandler.domain.model.entities.ProcessedData;
import org.webcat.ecommerce.datahandler.domain.model.entities.RawData;

/**
 *
 * {@link TransformData}
 * <p>
 * A domain service that transforms raw data into processed data.
 * </p>
 * <h4>Methods:</h4>
 * <li>{@method ProcessedData transform(RawData rawData)}</li>
 *
 */
public interface TransformData
{
  ProcessedData transform(
      RawData rawData);
}
