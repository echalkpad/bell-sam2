package com.vennetics.bell.sam.admin.service.repositories;

import com.vennetics.bell.sam.model.admin.service.entities.cassandra.CassandraAllowedService;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

/**
 * Repository interface for {@link CassandraAllowedService}.
 * Standard CRUD operations are provided out-of-the-box by spring-data-rest.
 */
@RepositoryRestResource(collectionResourceRel = CassandraAllowedService.TABLE_NAME, path = CassandraAllowedService.TABLE_NAME)
public interface AllowedServicesRepository extends CrudRepository<CassandraAllowedService, String> {
}
