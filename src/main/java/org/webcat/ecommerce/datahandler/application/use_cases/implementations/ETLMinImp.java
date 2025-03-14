package org.webcat.ecommerce.datahandler.application.use_cases.implementations;

import org.webcat.ecommerce.datahandler.application.use_cases.interfaces.ETL;
import org.webcat.ecommerce.datahandler.domain.model.entities.ProcessedData;
import org.webcat.ecommerce.datahandler.domain.model.entities.RawData;
import org.webcat.ecommerce.datahandler.domain.model.valueobjects.ETLStatus;
import org.webcat.ecommerce.datahandler.domain.service.datavalidation.interfaces.DataValidationService;
import org.webcat.ecommerce.datahandler.domain.service.etl.interfaces.ExtractData;
import org.webcat.ecommerce.datahandler.domain.service.etl.interfaces.LoadData;
import org.webcat.ecommerce.datahandler.domain.service.etl.interfaces.TransformData;
import org.springframework.stereotype.Component;
import org.webcat.ecommerce.datahandler.application.dtos.ETLRequestDTO;
import org.webcat.ecommerce.datahandler.application.dtos.ETLResponseDTO;

@Component
public class ETLMinImp implements ETL
{

  private final ExtractData extractionService;
  private final TransformData transformationService;
  private final LoadData loadingService;
  private final DataValidationService dataValidationService;

  // Constructor.
  public ETLMinImp(
      ExtractData extractionService,
      TransformData transformationService,
      LoadData loadingService,
      DataValidationService dataValidationService)
  {

    this.extractionService =
        extractionService;
    this.transformationService =
        transformationService;
    this.loadingService =
        loadingService;
    this.dataValidationService =
        dataValidationService;

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

  @Override
  public ETLResponseDTO handleMinioEvent(
      String eventName, String fileName)
  {
    // Only process "create:Put" (upload) events, ignore "delete" events
    if (eventName.startsWith(
        "s3:ObjectCreated:Put"))
    {
      Long sfID =
          this.dataValidationService
              .normaliseNameID(
                  fileName);

      if (sfID == null)
      {
        return new ETLResponseDTO(null,
            ETLStatus.FAILED);
      }


      System.out.print("\n");
      System.out.printf(
          "Processing file %s, event %s, with id %s\n",
          fileName, eventName, sfID);
      System.out.print("\n");

      // In the case of an upload, if the renaming was successful,
      // we return a normalised status..
      return new ETLResponseDTO(null,
          ETLStatus.DATANORMALISED)
              .setSuccess(true);
    }

    // In the case of a delete, we do nothing and return a ready
    // status.
    return new ETLResponseDTO(null,
        ETLStatus.READY)
            .setSuccess(true);
  }


}
