package com.vennetics.bell.sam.cassandra;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

/**
 * Repository interface for {@link Subscriber}.
 * Standard CRUD operations are provided out-of-the-box by spring-data-rest.
 */
@RepositoryRestResource(collectionResourceRel = Subscriber.SUBSCRIBERS, path = Subscriber.SUBSCRIBERS)
public interface SubscriberRepository extends CrudRepository<Subscriber, String> {

}
