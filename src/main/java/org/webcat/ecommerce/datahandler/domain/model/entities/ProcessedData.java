package org.webcat.ecommerce.datahandler.domain.model.entities;

public class ProcessedData
{
  private Long id;
  private String content;

  public ProcessedData(Long id,
      String content)
  {
    this.id = id;
    this.content = content;
  }

  public Long getID()
  {
    return this.id;
  }

  public String getContent()
  {
    return this.content;
  }
}
