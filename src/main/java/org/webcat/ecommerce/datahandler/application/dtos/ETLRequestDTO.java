package org.webcat.ecommerce.datahandler.application.dtos;

import java.util.List;

public class ETLRequestDTO
{
  // Currently I assume they're all from the same bucket.
  private List<String> fileNames;


  public ETLRequestDTO()
  {}

  public ETLRequestDTO(
      List<String> fileNames)
  {
    this.fileNames = fileNames;
  }

  public List<String> getFileNames()
  {
    return this.fileNames;
  }

  public void setFileNames(
      List<String> newFileNames)
  {
    this.fileNames = newFileNames;
  }
}
