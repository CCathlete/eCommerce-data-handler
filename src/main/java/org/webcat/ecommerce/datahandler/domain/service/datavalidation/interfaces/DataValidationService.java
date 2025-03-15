package org.webcat.ecommerce.datahandler.domain.service.datavalidation.interfaces;


/**
 * A domain service that makes sure the stored raw data is valid. Meaning, that the data is not
 * corrupted, that the name of the file is unique and that the name mapping is removed if the file
 * is deleted.
 */
public interface DataValidationService
{
  void validateData();

  Long normaliseNameID(String fileName);

  Boolean deleteNameMapping(
      String newName);
}
