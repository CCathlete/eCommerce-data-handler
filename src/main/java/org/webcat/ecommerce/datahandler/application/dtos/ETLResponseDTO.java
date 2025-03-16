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
  private Long[] processedFilesIDs;
  private ETLStatus status;
  private Boolean success;

  public ETLResponseDTO()
  {}

  /**
   * 
   * @param processedFilesIDs
   * @param status
   */
  public ETLResponseDTO(
      Long[] processedFilesIDs,
      ETLStatus status)
  {
    this.processedFilesIDs =
        processedFilesIDs;
    this.status = status;
    this.success = false;
  }

  public Long[] getProcessedFilesIDs()
  {
    return this.processedFilesIDs;
  }

  public void setProcessedFilesIDs(
      Long[] processedFilesIDs)
  {
    this.processedFilesIDs =
        processedFilesIDs;
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
