package com.vennetics.bell.sam.admin.service.repositories;

import com.vennetics.bell.sam.model.admin.service.entities.cassandra.CassandraApplication;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.UUID;

/**
 * Repository interface for {@link CassandraApplication}.
 * Standard CRUD operations are provided out-of-the-box by spring-data-rest.
 */
@RepositoryRestResource(collectionResourceRel = CassandraApplication.TABLE_NAME, path = CassandraApplication.TABLE_NAME)
public interface ApplicationRepository extends CrudRepository<CassandraApplication, UUID> {
}
