package org.webcat.ecommerce.datahandler.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import org.webcat.ecommerce.datahandler.domain.model.entities.FileMapping;

public interface JPAFileMappingRepo
    extends
    JpaRepository<FileMapping, Long>
{
  // Hibernate will generate this in addition to the default CRUD.
  FileMapping findByOriginalName(
      String originalName);

  FileMapping findByNewName(
      String newName);

  // Hibernate can return void, int, long from a deleteby... method.
  // The return value is the number of rows deleted.
  // We need to annotate as transactional since JPA methods are read only by default.
  @Transactional
  Integer deleteByNewName(
      String newName);
}
