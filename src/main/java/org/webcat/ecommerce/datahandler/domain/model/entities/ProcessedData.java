package org.webcat.ecommerce.datahandler.domain.model.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
  @Id
  @GeneratedValue(
      strategy = jakarta.persistence.GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String content;

}
