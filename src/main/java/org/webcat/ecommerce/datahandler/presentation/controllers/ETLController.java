package org.webcat.ecommerce.datahandler.presentation.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.webcat.ecommerce.datahandler.application.dtos.ETLRequestDTO;
import org.webcat.ecommerce.datahandler.application.dtos.ETLResponseDTO;
import org.webcat.ecommerce.datahandler.application.use_cases.interfaces.ETL;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/etl")
public class ETLController {

  private final ETL etlUseCase;

  public ETLController(ETL etlUseCase) {
    this.etlUseCase = etlUseCase;
  }

  // Handler for starting the etl process.
  @PostMapping("path")
  public ResponseEntity<ETLResponseDTO> processData(
      @RequestBody ETLRequestDTO request) {

    ETLResponseDTO response = this.etlUseCase.runETL(request);
    if (response == null) {
      return ResponseEntity.badRequest().build();
    }

    return ResponseEntity.ok(response);
  }

  //

}
