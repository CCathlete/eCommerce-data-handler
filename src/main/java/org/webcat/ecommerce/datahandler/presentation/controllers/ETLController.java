package org.webcat.ecommerce.datahandler.presentation.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.webcat.ecommerce.datahandler.Application;
import org.webcat.ecommerce.datahandler.application.dtos.ETLRequestDTO;
import org.webcat.ecommerce.datahandler.application.dtos.ETLResponseDTO;
import org.webcat.ecommerce.datahandler.application.use_cases.implementations.ETLMinImp;
import org.webcat.ecommerce.datahandler.application.use_cases.interfaces.ETL;
import org.webcat.ecommerce.datahandler.infrastructure.repository.JPAProcessedDataRepo;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/etl")
public class ETLController
{

  private final ETLMinImp ETLMinImp;

  private final ETL etlUseCase;
  private final ObjectMapper objectMApper;

  public ETLController(ETL etlUseCase,
      ObjectMapper objectMApper,
      Application application,
      ETLMinImp ETLMinImp)
  {
    this.etlUseCase = etlUseCase;
    this.objectMApper = objectMApper;
    this.ETLMinImp = ETLMinImp;
  }

  // Handler for starting the etl process.
  @PostMapping("/run")
  public ResponseEntity<ETLResponseDTO> sendToETL(
      @RequestBody ETLRequestDTO request)
  {

    ETLResponseDTO response =
        this.etlUseCase.runETL(request);

    if (response == null)
    {
      return ResponseEntity.badRequest()
          .build();
    }

    return ResponseEntity.ok(response);
  }

  // Handler for checking the status of the etl process.
  @GetMapping("/status/{processID}")
  public ResponseEntity<ETLResponseDTO> checkETLStatus(
      @RequestParam String processID)
  {

    return ResponseEntity.ok(etlUseCase
        .checkETLStatus(processID));
  }

  @PostMapping("/webhook")
  public ResponseEntity<ETLResponseDTO> handleMinioEvent(
      @RequestBody String eventData)
  {
    try
    {
      // Parsing the event's data as json.
      JsonNode eventJson =
          this.objectMApper
              .readTree(eventData);

      // Extracting relevant details from the json.
      String eventName = eventJson
          .path("Records").get(0)
          .path("eventName").asText();

      String fileName = eventJson
          .path("Records").get(0)
          .path("s3").path("object")
          .path("key").asText();

      // Logging the event and the extracted file name.
      System.out.printf(
          "Received event: %s\nFile name: %s\n",
          eventName, fileName);

      // Calling the ETL use case to process the file.
      ETLResponseDTO response =
          this.etlUseCase
              .handleMinioEvent(
                  eventName, fileName);

      if (response == null)
      {
        return ResponseEntity
            .badRequest().build();
      }

      return ResponseEntity
          .ok(response);
    } catch (Exception e)
    {
      e.printStackTrace();
      // See why I can't return a body.
      return ResponseEntity.status(500)
          .build();
    }

  }


}
