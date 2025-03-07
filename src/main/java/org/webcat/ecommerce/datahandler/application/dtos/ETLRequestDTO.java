package org.webcat.ecommerce.datahandler.application.dtos;

public class ETLRequestDTO {
  private Long rawDataId;

  public ETLRequestDTO() {
  }

  public ETLRequestDTO(Long rawDataId) {
    this.rawDataId = rawDataId;
  }

  public Long getRawDataId() {
    return this.rawDataId;
  }

  public void setRawDataId(Long rawDataId) {
    this.rawDataId = rawDataId;
  }
}
