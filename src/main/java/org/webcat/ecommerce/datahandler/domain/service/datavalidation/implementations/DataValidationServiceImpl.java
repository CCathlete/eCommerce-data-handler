package org.webcat.ecommerce.datahandler.domain.service.datavalidation.implementations;

import org.springframework.stereotype.Service;
import org.webcat.ecommerce.datahandler.domain.model.entities.FileMapping;
import org.webcat.ecommerce.datahandler.domain.service.datavalidation.interfaces.DataValidationService;
import org.webcat.ecommerce.datahandler.infrastructure.database.MinIORawDataRepository;
import org.webcat.ecommerce.datahandler.infrastructure.repository.JPAFileMappingRepo;
import org.webcat.ecommerce.datahandler.shared.helpers.SnowflakeIDGenerator;

@Service
public class DataValidationServiceImpl
    implements DataValidationService
{

  private final SnowflakeIDGenerator snowflakeIDGenerator;
  private final JPAFileMappingRepo fileMappingRepo;
  private final MinIORawDataRepository minioRepo;

  public DataValidationServiceImpl(
      SnowflakeIDGenerator snowflakeIDGenerator,
      JPAFileMappingRepo fileMappingRepo,
      MinIORawDataRepository minioRepo)
  {
    this.snowflakeIDGenerator =
        snowflakeIDGenerator;
    this.fileMappingRepo =
        fileMappingRepo;
    this.minioRepo = minioRepo;
  }

  @Override
  public void validateData()
  {}

  @Override
  public Long normaliseNameID(
      String fileName)
  {
    Long sfID =
        this.snowflakeIDGenerator
            .generateId();

    // Generating a new name for the file.
    String newName = this.minioRepo
        .generateObjectName(sfID);

    if (newName == null)
    {
      return null;
    }

    FileMapping fMap = new FileMapping(
        fileName, newName);

    // Saving the new and old names mapping in the DB.
    if (this.fileMappingRepo
        .save(fMap) == null)
    {
      return null;
    }

    // Updating the file name in minio.
    if (!this.minioRepo.renameObject(
        fileName, newName))
    {
      return null;
    }

    return sfID;
  }

  @Override
  public Boolean deleteNameMapping(
      String newName)
  {
    return this.fileMappingRepo
        .deleteByNewName(newName) > 0;
  }

}
