package com.vennetics.bell.sam.admin.service.repositories;

import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.core.querybuilder.Select;
import com.datastax.driver.core.utils.UUIDs;
import com.vennetics.bell.sam.admin.service.AdminServiceCassandraTestConfig;
import com.vennetics.bell.sam.admin.service.utils.TestUtils;
import com.vennetics.bell.sam.core.cassandra.helpers.EmbeddedCassandraTestHelper;
import com.vennetics.bell.sam.model.admin.service.entities.cassandra.CassandraClient;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cassandra.core.cql.CqlIdentifier;
import org.springframework.data.cassandra.core.CassandraAdminTemplate;
import org.springframework.data.cassandra.core.CassandraOperations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * CassandraClient repo test
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = AdminServiceCassandraTestConfig.class)
public class ClientRepositoryTest {


    private static final Logger logger = LoggerFactory.getLogger(ClientRepositoryTest.class);
    public static final UUID UPDATED_APPLICATION_ID = UUIDs.timeBased();

    @Autowired
    private CassandraAdminTemplate cassandraAdminTemplate;
    @Autowired
    private CassandraOperations cassandraTemplate;
    @Value("${local.server.port}")
    private static int port;

    @BeforeClass
    public static void startCassandraEmbedded() throws Exception {
        EmbeddedCassandraTestHelper.startCassandraCluster(EmbeddedCassandraServerHelper.CASSANDRA_RNDPORT_YML_FILE,
                                                          port,
                                                          TestUtils.CASSANDRA_HOST,
                                                          TestUtils.KEYSPACE_CREATE,
                                                          TestUtils.CREATE_CLIENT_TABLE);
    }

    @Before
    public void createTable() {
        cassandraAdminTemplate.createTable(true, CqlIdentifier.cqlId(CassandraClient.TABLE_NAME),
                                           CassandraClient.class, new HashMap<String, Object>());
        logger.debug("CassandraClient Table Created");

    }

    @After
    public void dropTable() {
        cassandraAdminTemplate.dropTable(CqlIdentifier.cqlId(CassandraClient.TABLE_NAME));
        logger.debug("CassandraClient Table Dropped");
    }

    @AfterClass
    public static void stopCassandraEmbedded() throws Exception {
        logger.debug("Cleaning Embedded Cassandra...");
        EmbeddedCassandraServerHelper.cleanEmbeddedCassandra();
        logger.debug("Embedded Cassandra Cleaned");
    }

    @Test
    public void shouldAssertClientIsInserted() {
        logger.debug("Creating CassandraClient...");
        final CassandraClient cassandraClient = TestUtils.createDummyCassClient();
        final String clientId = cassandraClient.getClientId();
        logger.debug("Created CassandraClient with id: [{}]", clientId);

        cassandraTemplate.insert(cassandraClient);
        logger.debug("CassandraClient with id [{}] inserted into Cassandra", clientId);

        final Select select = QueryBuilder.select().from(CassandraClient.TABLE_NAME).where(
                        QueryBuilder.eq(CassandraClient.CLIENT_ID, clientId)).limit(10);

        final CassandraClient result = cassandraTemplate.selectOne(select, CassandraClient.class);

        assertEquals(clientId, result.getClientId());

    }

    @Test
    public void shouldAssertClientIsInsertedAndUpdated() {
        final CassandraClient cassandraClient = insertClient();

        cassandraClient.setApplicationId(UPDATED_APPLICATION_ID);

        cassandraTemplate.update(cassandraClient);

        final Select select = QueryBuilder.select().from(CassandraClient.TABLE_NAME)
                                          .where(QueryBuilder.eq(
                                              CassandraClient.CLIENT_ID,
                                              cassandraClient.getClientId())).limit(10);

        CassandraClient updatedResult = cassandraTemplate.selectOne(select, CassandraClient.class);

        assertEquals(UPDATED_APPLICATION_ID, updatedResult.getApplicationId());

    }

    @Test
    public void shouldAssertClientIsDeleted() {
        final CassandraClient cassandraClient = insertClient();

        cassandraTemplate.delete(cassandraClient);

        final Select select = QueryBuilder.select().from(CassandraClient.TABLE_NAME)
                                          .where(QueryBuilder.eq(CassandraClient.CLIENT_ID, cassandraClient.getClientId())).limit(10);

        CassandraClient updatedResult = cassandraTemplate.selectOne(select, CassandraClient.class);
        assertNull(updatedResult);

    }

    private CassandraClient insertClient() {
        logger.debug("Creating CassandraClient...");
        final CassandraClient cassandraClient = TestUtils.createDummyCassClient();
        final String clientId = cassandraClient.getClientId();
        logger.debug("Created CassandraClient with clientId: [{}]", clientId);

        cassandraTemplate.insert(cassandraClient);
        logger.debug("CassandraClient with clientId [{}] inserted into Cassandra", clientId);

        final Select select = QueryBuilder.select().from(CassandraClient.TABLE_NAME)
                                          .where(QueryBuilder.eq(CassandraClient.CLIENT_ID, clientId)).limit(10);

        final CassandraClient result = cassandraTemplate.selectOne(select, CassandraClient.class);

        assertEquals(clientId, result.getClientId());

        return cassandraClient;
    }
}
