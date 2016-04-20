package com.vennetics.bell.sam.admin.service.repositories;

import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.core.querybuilder.Select;
import com.datastax.driver.core.utils.UUIDs;
import com.vennetics.bell.sam.admin.service.AdminServiceCassandraTestConfig;
import com.vennetics.bell.sam.admin.service.utils.TestUtils;
import com.vennetics.bell.sam.core.cassandra.helpers.EmbeddedCassandraTestHelper;
import com.vennetics.bell.sam.model.admin.service.entities.cassandra.CassandraAllowedService;
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

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * CassandraAllowedService repo test
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = AdminServiceCassandraTestConfig.class)
public class AllowedServiceRepositoryTest {

    private static final Logger logger = LoggerFactory.getLogger(AllowedServiceRepositoryTest.class);

    public static final Set<UUID> UPDATED_APPLICATIONS = new HashSet<UUID>(Arrays.asList(UUIDs.timeBased(),
                                                                                         UUIDs.timeBased()));
    @Autowired
    private CassandraAdminTemplate cassandraAdminTemplate;
    @Autowired
    private CassandraOperations cassandraTemplate;
    @Value("${local.server.port}")
    private static int port;

    @BeforeClass
    public static void setUp() throws Exception {
        EmbeddedCassandraTestHelper.startCassandraCluster(EmbeddedCassandraServerHelper.CASSANDRA_RNDPORT_YML_FILE,
                                                          port,
                                                          TestUtils.CASSANDRA_HOST,
                                                          TestUtils.KEYSPACE_CREATE,
                                                          TestUtils.CREATE_APPLICATION_TABLE,
                                                          TestUtils.CREATE_CLIENT_TABLE,
                                                          TestUtils.CREATE_ROLE_TABLE);
    }

    @AfterClass
    public static void stopCassandraEmbedded() throws Exception {
        logger.debug("Cleaning Embedded Cassandra...");
        EmbeddedCassandraServerHelper.cleanEmbeddedCassandra();
        logger.debug("Embedded Cassandra Cleaned");
    }

    @Before
    public void createTable() {
        cassandraAdminTemplate.createTable(true,
                                           CqlIdentifier.cqlId(CassandraAllowedService.TABLE_NAME),
                                           CassandraAllowedService.class,
                                           new HashMap<String, Object>());
        logger.debug("allowed_services Table Created");

    }

    @After
    public void dropTable() {
        cassandraAdminTemplate.dropTable(CqlIdentifier.cqlId(CassandraAllowedService.TABLE_NAME));
        logger.debug("allowed_services Table Dropped");
    }

    @Test
    public void shouldAssertAllowedServiceIsInserted() {
        logger.debug("Creating CassandraAllowedService...");
        final CassandraAllowedService cassandraAllowedService = TestUtils.createDummyCassAllowedService();
        final String allowedServiceId = cassandraAllowedService.getAllowedServiceId();
        logger.debug("Created CassandraAllowedService with id: [{}]", allowedServiceId);

        cassandraTemplate.insert(cassandraAllowedService);
        logger.debug("CassandraAllowedService with id [{}] inserted into Cassandra", allowedServiceId);

        final Select select = QueryBuilder.select()
                                          .from(CassandraAllowedService.TABLE_NAME)
                                          .where(QueryBuilder.eq(CassandraAllowedService.ALLOWED_SERVICE_ID, allowedServiceId)).limit(10);

        final CassandraAllowedService result = cassandraTemplate.selectOne(select, CassandraAllowedService.class);

        assertEquals(allowedServiceId, result.getAllowedServiceId());
    }

    @Test public void shouldAssertAllowedServiceIsInsertedAndUpdated() {
        final CassandraAllowedService cassandraAllowedService = insertAllowedService();

        cassandraAllowedService.setApplicationIds(UPDATED_APPLICATIONS);

        cassandraTemplate.update(cassandraAllowedService);

        final Select select = QueryBuilder.select()
                                          .from(CassandraAllowedService.TABLE_NAME)
                                          .where(QueryBuilder.eq(CassandraAllowedService.ALLOWED_SERVICE_ID, cassandraAllowedService.getAllowedServiceId()))
                                          .limit(10);

        CassandraAllowedService updatedResult = cassandraTemplate.selectOne(select, CassandraAllowedService.class);

        assertEquals(UPDATED_APPLICATIONS, updatedResult.getApplicationIds());
    }

    @Test public void shouldAssertAllowedServiceIsDeleted() {
        final CassandraAllowedService cassandraAllowedService = insertAllowedService();

        cassandraTemplate.delete(cassandraAllowedService);

        final Select select = QueryBuilder.select()
                                          .from(CassandraAllowedService.TABLE_NAME)
                                          .where(QueryBuilder.eq(CassandraAllowedService.ALLOWED_SERVICE_ID, cassandraAllowedService.getAllowedServiceId()))
                                          .limit(10);

        CassandraAllowedService updatedResult = cassandraTemplate.selectOne(select, CassandraAllowedService.class);
        assertNull(updatedResult);
    }

    private CassandraAllowedService insertAllowedService() {
        logger.debug("Creating CassandraAllowedService...");
        final CassandraAllowedService cassandraAllowedService = TestUtils.createDummyCassAllowedService();
        final String allowedServiceId = cassandraAllowedService.getAllowedServiceId();
        logger.debug("Created CassandraAllowedService with allowedServiceId: [{}]", allowedServiceId);

        cassandraTemplate.insert(cassandraAllowedService);
        logger.debug("CassandraAllowedService with allowedServiceId [{}] inserted into Cassandra", allowedServiceId);

        final Select select = QueryBuilder.select()
                                          .from(CassandraAllowedService.TABLE_NAME)
                                          .where(QueryBuilder.eq(CassandraAllowedService.ALLOWED_SERVICE_ID, allowedServiceId)).limit(10);

        final CassandraAllowedService result = cassandraTemplate.selectOne(select, CassandraAllowedService.class);

        assertEquals(allowedServiceId, result.getAllowedServiceId());

        return cassandraAllowedService;
    }
}
