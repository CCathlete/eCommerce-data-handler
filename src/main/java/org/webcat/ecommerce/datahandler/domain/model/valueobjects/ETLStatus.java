package org.webcat.ecommerce.datahandler.domain.model.valueobjects;

public enum ETLStatus
{
  READY("Ready"), READYWITHWARNING(
      "Ready with Warning"), PENDING(
          "Pending"), RUNNING(
              "Running"), EXTRACTED(
                  "Extracted"), TRANSFORMED(
                      "Transformed"), LOADED(
                          "Loaded"), FAILED(
                              "Failed"), CANCELLED(
                                  "Cancelled"), DATANORMALISED(
                                      "Data Normalised");

  private final String status;

  ETLStatus(String status)
  {
    this.status = status;
  }

  public String getStatus()
  {
    return this.status;
  }
}
