package org.webcat.ecommerce.datahandler.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.webcat.ecommerce.datahandler.domain.model.entities.FileMapping;

public interface JPAFileMappingRepo
    extends
    JpaRepository<FileMapping, Long>
{
  // Hibernate will generate this in addition to the default CRUD.
  FileMapping findByOriginalName(
      String originalName);
}
