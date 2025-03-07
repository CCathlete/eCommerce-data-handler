package org.webcat.ecommerce.datahandler.application.dtos;

public class ETLResponseDTO {
  private Long processedDataId;
  private String status;

  public ETLResponseDTO() {
  }

  public ETLResponseDTO(Long processedDataId, String status) {
    this.processedDataId = processedDataId;
    this.status = status;
  }

  public Long getProcessedDataId() {
    return this.processedDataId;
  }

  public void setProcessedDataId(Long processedDataId) {
    this.processedDataId = processedDataId;
  }

  public String getStatus() {
    return this.status;
  }

  public void setStatus(String status) {
    this.status = status;
  }
}
