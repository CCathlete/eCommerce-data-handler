package org.webcat.ecommerce.datahandler.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.webcat.ecommerce.datahandler.domain.model.entities.ProcessedData;

/**
 * {@link JPAProcessedDataRepo}
 * <p>
 * A JPA repository for processed data.
 * </p>
 * <p>
 * <li>This will be embedded in the SQL implementation to allow spring to</li>
 * <li>implement CRUD operations.</li>
 * </p>
 */
public interface JPAProcessedDataRepo
    extends
    JpaRepository<ProcessedData, Long>
{

}
