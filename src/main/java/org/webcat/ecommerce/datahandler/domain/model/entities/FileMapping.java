package org.webcat.ecommerce.datahandler.domain.model.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "file_mapping")
public class FileMapping
{

  @Id
  @GeneratedValue(
      strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "original_name",
      nullable = false)
  private String originalName;

  @Column(name = "new_name",
      nullable = false)
  private String newName;

  // Constructor.
  public FileMapping(
      String originalName,
      String newName)
  {
    this.originalName = originalName;
    this.newName = newName;
  }

}
