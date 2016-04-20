package com.vennetics.bell.sam.admin.service.repositories;

import com.vennetics.bell.sam.model.admin.service.entities.cassandra.CassandraRole;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

/**
 * Repository interface for {@link CassandraRole}.
 * Standard CRUD operations are provided out-of-the-box by spring-data-rest.
 */
@RepositoryRestResource(collectionResourceRel = CassandraRole.TABLE_NAME, path = CassandraRole.TABLE_NAME)
public interface RoleRepository extends CrudRepository<CassandraRole, String> {
}
