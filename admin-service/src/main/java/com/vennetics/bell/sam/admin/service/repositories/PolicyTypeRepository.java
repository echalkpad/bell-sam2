package com.vennetics.bell.sam.admin.service.repositories;

import com.vennetics.bell.sam.model.admin.service.entities.cassandra.CassandraPolicyType;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

/**
 * Repository interface for {@link CassandraPolicyType}.
 * Standard CRUD operations are provided out-of-the-box by spring-data-rest.
 */
@RepositoryRestResource(collectionResourceRel = CassandraPolicyType.TABLE_NAME, path = CassandraPolicyType.TABLE_NAME)
public interface PolicyTypeRepository extends CrudRepository<CassandraPolicyType, String> {
}
