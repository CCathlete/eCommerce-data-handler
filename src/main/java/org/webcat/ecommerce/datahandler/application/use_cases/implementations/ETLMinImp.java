package org.webcat.ecommerce.datahandler.application.use_cases.implementations;

import org.webcat.ecommerce.datahandler.application.use_cases.interfaces.ETL;
import org.webcat.ecommerce.datahandler.domain.model.entities.ProcessedData;
import org.webcat.ecommerce.datahandler.domain.model.entities.RawData;
import org.webcat.ecommerce.datahandler.domain.model.valueobjects.ETLStatus;
import org.webcat.ecommerce.datahandler.domain.service.etl.interfaces.ExtractData;
import org.webcat.ecommerce.datahandler.domain.service.etl.interfaces.LoadData;
import org.webcat.ecommerce.datahandler.domain.service.etl.interfaces.TransformData;
import org.webcat.ecommerce.datahandler.application.dtos.ETLRequestDTO;
import org.webcat.ecommerce.datahandler.application.dtos.ETLResponseDTO;

public class ETLMinImp implements ETL
{

  private final ExtractData extractionService;
  private final TransformData transformationService;
  private final LoadData loadingService;

  // Constructor.
  public ETLMinImp(
      ExtractData extractionService,
      TransformData transformationService,
      LoadData loadingService)
  {

    this.extractionService =
        extractionService;
    this.transformationService =
        transformationService;
    this.loadingService =
        loadingService;

  }

  @Override
  public ETLResponseDTO runETL(
      ETLRequestDTO request)
  {

    RawData rawData =
        this.extractionService.extract(
            request.getRawDataId());
    if (rawData == null)
    {
      return null;
    }

    ProcessedData processedData =
        this.transformationService
            .transform(rawData);
    if (processedData == null)
    {
      return null;
    }

    Boolean success =
        this.loadingService
            .save(processedData);
    if (!success)
    {
      return null;
    }

    return new ETLResponseDTO(
        processedData.getId(),
        ETLStatus.LOADED)
            .setSuccess(success);
  }

  @Override
  public ETLResponseDTO checkETLStatus(
      String processID)
  {
    return null; // TODO: implement
  }
}
