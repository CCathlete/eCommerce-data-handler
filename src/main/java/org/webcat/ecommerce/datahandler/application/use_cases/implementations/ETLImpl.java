package org.webcat.ecommerce.datahandler.application.use_cases.implementations;

import org.webcat.ecommerce.datahandler.application.use_cases.interfaces.ETL;

import org.webcat.ecommerce.datahandler.application.dtos.ETLRequestDTO;
import org.webcat.ecommerce.datahandler.application.dtos.ETLResponseDTO;

public class ETLImpl implements ETL
{
  @Override
  public ETLResponseDTO runETL(
      ETLRequestDTO request)
  {
    return null; // TODO: implement
  }

  @Override
  public ETLResponseDTO checkETLStatus(
      String processID)
  {
    return null; // TODO: implement
  }

  @Override
  public ETLResponseDTO handleMinioEvent(
      String eventName, String fileName)
  {
    return null; // TODO: implement
  }
}
