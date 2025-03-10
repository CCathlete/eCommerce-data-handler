package org.webcat.ecommerce.datahandler.domain.model.entities;

import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "processed_data")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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
  @Column(nullable = false,
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

}
