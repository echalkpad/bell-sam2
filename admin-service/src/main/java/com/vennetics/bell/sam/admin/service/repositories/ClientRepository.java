package com.vennetics.bell.sam.admin.service.repositories;

import com.vennetics.bell.sam.model.admin.service.entities.cassandra.CassandraClient;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

/**
 * Repository interface for {@link CassandraClient}.
 * Standard CRUD operations are provided out-of-the-box by spring-data-rest.
 */
@RepositoryRestResource(collectionResourceRel = CassandraClient.TABLE_NAME, path = CassandraClient.TABLE_NAME)
public interface ClientRepository extends CrudRepository<CassandraClient, String> {
}
