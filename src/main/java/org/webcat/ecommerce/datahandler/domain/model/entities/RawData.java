package org.webcat.ecommerce.datahandler.domain.model.entities;

import org.webcat.ecommerce.datahandler.shared.helpers.SnowflakeIDGenerator;

/**
 *
 * {@link RawData}
 * <p>
 * A domain entity that represents raw data.
 * </p>
 * <h4>Fields:</h4>
 * <li>{@field Long id}</li>
 * <li>{@field String rawContent}</li>
 * <p>
 * Setting the content and id is done by the raw data repo.
 * </p>
 */
public class RawData
{
  private Long id;
  private String rawContent;
  private static final SnowflakeIDGenerator idGenerator =
      new SnowflakeIDGenerator(1, 1);

  public RawData(String rawContent)
  {
    // Generates a unique id at time of creation.
    this.id = RawData.idGenerator
        .generateId();
    this.rawContent = rawContent;
  }

  public Long getID()
  {
    return this.id;
  }

  public String getRawContent()
  {
    return this.rawContent;
  }
}
