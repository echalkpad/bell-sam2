package com.vennetics.bell.sam.admin.service.repositories;

import com.vennetics.bell.sam.model.admin.service.entities.cassandra.CassandraProtocolType;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

/**
 * Repository interface for {@link CassandraProtocolType}.
 * Standard CRUD operations are provided out-of-the-box by spring-data-rest.
 */
@RepositoryRestResource(collectionResourceRel = CassandraProtocolType.TABLE_NAME, path = CassandraProtocolType.TABLE_NAME)
public interface ProtocolTypeRepository extends CrudRepository<CassandraProtocolType, String> {
}
