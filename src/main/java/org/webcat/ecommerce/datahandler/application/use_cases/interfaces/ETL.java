package org.webcat.ecommerce.datahandler.application.use_cases.interfaces;

import org.webcat.ecommerce.datahandler.application.dtos.ETLRequestDTO;
import org.webcat.ecommerce.datahandler.application.dtos.ETLResponseDTO;

public interface ETL
{
  ETLResponseDTO runETL(
      ETLRequestDTO request);

  ETLResponseDTO checkETLStatus(
      String processID);

  ETLResponseDTO handleMinioEvent(
      String eventName,
      String fileName);
}
