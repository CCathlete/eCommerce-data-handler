package org.webcat.ecommerce.datahandler.application.dtos;

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
  private String status;
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
      String status)
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

  public String getStatus()
  {
    return this.status;
  }

  public ETLResponseDTO setStatus(
      String status)
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
