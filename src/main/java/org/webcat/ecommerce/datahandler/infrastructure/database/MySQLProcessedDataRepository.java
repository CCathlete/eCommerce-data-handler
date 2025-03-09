package org.webcat.ecommerce.datahandler.infrastructure.database;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.webcat.ecommerce.datahandler.domain.model.entities.ProcessedData;
import org.webcat.ecommerce.datahandler.infrastructure.repository.JPAProcessedDataRepo;
import org.webcat.ecommerce.datahandler.infrastructure.repository.ProcessedDataRepository;

@Repository
public class MySQLProcessedDataRepository
    implements ProcessedDataRepository
{

  private final JPAProcessedDataRepo jpaRepo;

  // This is autowired, e.g. spring creates a singleton for the
  // implementation of the jpa repo interface and passes the
  // singleton here.
  public MySQLProcessedDataRepository(
      JPAProcessedDataRepo jpaRepo)
  {
    this.jpaRepo = jpaRepo;
  }

  @Override
  public ProcessedData findById(Long id)
  {
    return this.jpaRepo.findById(id)
        .orElse(null);
  }

  public Boolean save(
      ProcessedData processedData)
  {
    return this.jpaRepo
        .save(processedData) != null;
  }
}
