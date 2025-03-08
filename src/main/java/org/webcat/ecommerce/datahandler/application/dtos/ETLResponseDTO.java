package org.webcat.ecommerce.datahandler.application.dtos;

import org.webcat.ecommerce.datahandler.domain.model.valueobjects.ETLStatus;

/**
 *
 * ETLResponseDTO
 * <p>
 * Long processedDataId
 * <p>
 * String status
 * </p>
 * <p>
 * Boolean success
 * </p>
 * </p>
 */
public class ETLResponseDTO
{
  private Long processedDataId;
  private ETLStatus status;
  private Boolean success;

  public ETLResponseDTO()
  {}

  /**
   * 
   * @param processedDataId
   * @param status
   */
  public ETLResponseDTO(
      Long processedDataId,
      ETLStatus status)
  {
    this.processedDataId =
        processedDataId;
    this.status = status;
    this.success = false;
  }

  public Long getProcessedDataId()
  {
    return this.processedDataId;
  }

  public void setProcessedDataId(
      Long processedDataId)
  {
    this.processedDataId =
        processedDataId;
  }

  public ETLStatus getStatus()
  {
    return this.status;
  }

  public ETLResponseDTO setStatus(
      ETLStatus status)
  {
    this.status = status;
    return this;
  }

  public Boolean getSuccess()
  {
    return this.success;
  }

  public ETLResponseDTO setSuccess(
      Boolean success)
  {
    this.success = success;
    return this;
  }
}
