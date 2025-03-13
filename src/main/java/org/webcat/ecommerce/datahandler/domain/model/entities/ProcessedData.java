package org.webcat.ecommerce.datahandler.domain.model.entities;

import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * A domain entity that represents processed data.
 * <h4>Fields:</h4>
 * <li>{@field Long id} (not passed to the constructor)</li>
 * <li>{@field Long rawDataId}</li>
 * <li>{@field String transformedData}</li>
 * <li>{@field Status status}</li>
 * <li>{@field Timestamp processedAt}</li>
 */
@Entity
@Table(name = "processed_data")
@Getter
@Setter
@NoArgsConstructor
public class ProcessedData
{

  public enum Status
  {
    PENDING, SUCCESS, FAILED
  }

  @Id
  @GeneratedValue(
      strategy = jakarta.persistence.GenerationType.IDENTITY)
  private Long id;

  // The id of the corresponding raw data entry in minio (it's
  // saved as an object name there).
  @Column(name = "raw_data_id",
      nullable = false, unique = true)
  private Long rawDataId;

  // The content after transformation (processing) in a json
  // string format.
  @Column(name = "transformed_data",
      nullable = false,
      columnDefinition = "JSON")
  private String transformedData;

  // The status of the processed data.
  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Status status;

  // The timestamp when the data was processed.
  @Column(nullable = false,
      updatable = false,
      insertable = false)
  private Timestamp processedAt;

  // Since some of the variables are @GeneratedValue, Lombok's automatic @AllArgeConstructor doesn't
  // work.
  public ProcessedData(Long rawId,
      String transformedData,
      Status status,
      Timestamp processedAt)
  {

    this.rawDataId = rawId;
    this.transformedData =
        transformedData;
    this.status = status;
    this.processedAt = processedAt;
  }

}
