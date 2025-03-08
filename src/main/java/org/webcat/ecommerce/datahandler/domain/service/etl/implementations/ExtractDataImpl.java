package org.webcat.ecommerce.datahandler.domain.service.etl.implementations;

import org.webcat.ecommerce.datahandler.domain.model.entities.RawData;
import org.webcat.ecommerce.datahandler.domain.service.etl.interfaces.ExtractData;
import org.webcat.ecommerce.datahandler.infrastructure.repository.RawDataRepository;

/**
 * {@link ExtractDataImpl}
 * <p>
 * A domain service that calls the repository to extract data by ID.
 * </p>
 */
public class ExtractDataImpl
    implements ExtractData
{
  private final RawDataRepository repo;

  ExtractDataImpl(
      RawDataRepository repo)
  {
    this.repo = repo;
  }

  @Override
  public RawData extract(Long id)
  {
    return this.repo.findById(id);
  }
}
