package org.webcat.ecommerce.datahandler.domain.model.valueobjects;

public enum ETLStatus
{
  EXTRACTED("Extracted"), TRANSFORMED(
      "Transformed"), LOADED(
          "Loaded"), FAILED(
              "Failed"), CANCELLED(
                  "Cancelled");

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
