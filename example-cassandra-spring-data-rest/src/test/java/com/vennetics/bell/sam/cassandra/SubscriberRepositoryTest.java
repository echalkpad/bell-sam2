package com.vennetics.bell.sam.cassandra;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.core.querybuilder.Select;
import com.vennetics.bell.sam.cassandra.utils.CassandraTestUtils;
import org.cassandraunit.utils.EmbeddedCassandraServerHelper;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cassandra.core.cql.CqlIdentifier;
import org.springframework.data.cassandra.core.CassandraAdminTemplate;
import org.springframework.data.cassandra.core.CassandraOperations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Ensures a {@link Subscriber} entity is persisted to local Cassandra backend.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = CassandraConfig.class)
public class SubscriberRepositoryTest {

    private static final Logger logger = LoggerFactory.getLogger(SubscriberRepositoryTest.class);
    private static final String UPDATED_SURNAME = "Updated Surname";

    @Autowired
    private CassandraAdminTemplate cassandraAdminTemplate;

    @Autowired
    private CassandraOperations cassandraTemplate;

    @BeforeClass
    public static void startCassandraEmbedded() throws Exception {
        EmbeddedCassandraServerHelper.startEmbeddedCassandra(EmbeddedCassandraServerHelper.CASSANDRA_RNDPORT_YML_FILE,
                90000L);
        final Cluster cluster = Cluster.builder().addContactPoint(CassandraTestUtils.CASSANDRA_HOST)
                .withPort(EmbeddedCassandraServerHelper.getNativeTransportPort()).build();
        logger.debug("Test Server Started at {}:{}", CassandraTestUtils.CASSANDRA_HOST,
                EmbeddedCassandraServerHelper.getNativeTransportPort());
        final Session session = cluster.connect();
        session.execute(CassandraTestUtils.KEYSPACE_CREATE);
        logger.debug("Keyspace created...");
    }

    @Before
    public void createTable() {
        cassandraAdminTemplate.createTable(true, CqlIdentifier.cqlId(CassandraTestUtils.CASS_TABLE_NAME),
                Subscriber.class, new HashMap<String, Object>());
        logger.debug("Subscriber Table Created");

    }

    @After
    public void dropTable() {
        cassandraAdminTemplate.dropTable(CqlIdentifier.cqlId(CassandraTestUtils.CASS_TABLE_NAME));
        logger.debug("Subscriber Table Dropped");
    }

    @AfterClass
    public static void stopCassandraEmbedded() throws Exception {
        logger.debug("Cleaning Embedded Cassandra...");
        EmbeddedCassandraServerHelper.cleanEmbeddedCassandra();
        logger.debug("Embedded Cassandra Cleaned");
    }

    @Test
    public void shouldAssertSubscriberIsInserted() {
        logger.debug("Creating Subscriber...");
        final Subscriber subscriber = CassandraTestUtils.createDummySubscriber();
        final String id = subscriber.getId();
        logger.debug("Created Subscriber with id: [{}]", id);

        cassandraTemplate.insert(subscriber);
        logger.debug("Subscriber with id [{}] inserted into Cassandra", id);

        final Select select = QueryBuilder.select().from(Subscriber.SUBSCRIBERS)
                .where(QueryBuilder.eq("id", id)).limit(10);

        final Subscriber result = cassandraTemplate.selectOne(select, Subscriber.class);

        assertEquals(id, result.getId());

    }

    @Test
    public void shouldAssertSubscriberIsInsertedAndUpdated() {
        final Subscriber subscriber = insertSubscriber();

        subscriber.setSurName(UPDATED_SURNAME);

        cassandraTemplate.update(subscriber);

        final Select surnameSelect = QueryBuilder.select().from(Subscriber.SUBSCRIBERS)
                .where(QueryBuilder.eq("id", subscriber.getId())).limit(10);

        Subscriber updatedResult = cassandraTemplate.selectOne(surnameSelect, Subscriber.class);

        assertEquals(UPDATED_SURNAME, updatedResult.getSurName());

    }

    @Test
    public void shouldAssertSubscriberIsDeleted() {
        final Subscriber subscriber = insertSubscriber();

        cassandraTemplate.delete(subscriber);

        final Select surnameSelect = QueryBuilder.select().from(Subscriber.SUBSCRIBERS)
                .where(QueryBuilder.eq("id", subscriber.getId())).limit(10);

        Subscriber updatedResult = cassandraTemplate.selectOne(surnameSelect, Subscriber.class);
        assertNull(updatedResult);

    }

    private Subscriber insertSubscriber() {
        logger.debug("Creating Subscriber...");
        final Subscriber subscriber = CassandraTestUtils.createDummySubscriber();
        final String id = subscriber.getId();
        logger.debug("Created Subscriber with id: [{}]", id);

        cassandraTemplate.insert(subscriber);
        logger.debug("Subscriber with id [{}] inserted into Cassandra", id);

        final Select idSelect = QueryBuilder.select().from(Subscriber.SUBSCRIBERS)
                .where(QueryBuilder.eq("id", id)).limit(10);

        final Subscriber result = cassandraTemplate.selectOne(idSelect, Subscriber.class);

        assertEquals(id, result.getId());

        return subscriber;
    }

}
