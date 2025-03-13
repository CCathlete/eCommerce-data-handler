package org.webcat.ecommerce.datahandler.domain.service.datavalidation.interfaces;


public interface DataValidationService
{
  void validateData();

  Long normaliseNameID(String fileName);
}
