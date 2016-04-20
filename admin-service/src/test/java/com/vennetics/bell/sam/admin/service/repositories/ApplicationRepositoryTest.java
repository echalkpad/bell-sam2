package com.vennetics.bell.sam.admin.service.repositories;

import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.core.querybuilder.Select;
import com.vennetics.bell.sam.admin.service.AdminServiceCassandraTestConfig;
import com.vennetics.bell.sam.admin.service.utils.TestUtils;
import com.vennetics.bell.sam.core.cassandra.helpers.EmbeddedCassandraTestHelper;
import com.vennetics.bell.sam.model.admin.service.entities.cassandra.CassandraApplication;
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
 * CassandraApplication repo test
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = AdminServiceCassandraTestConfig.class)
public class ApplicationRepositoryTest {


    private static final Logger logger = LoggerFactory.getLogger(ApplicationRepositoryTest.class);
    public static final Set<String> UPDATED_ALLOWED_SERVICE_TYPE_IDS = new HashSet<String>(
                    Arrays.asList("ServiceType1", "ServiceType2"));
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
                                                          TestUtils.CREATE_APPLICATION_TABLE);
    }

    @Before
    public void createTable() {
        cassandraAdminTemplate.createTable(true, CqlIdentifier.cqlId(CassandraApplication.TABLE_NAME),
                                           CassandraApplication.class, new HashMap<String, Object>());
        logger.debug("CassandraApplication Table Created");

    }

    @After
    public void dropTable() {
        cassandraAdminTemplate.dropTable(CqlIdentifier.cqlId(CassandraApplication.TABLE_NAME));
        logger.debug("CassandraApplication Table Dropped");
    }

    @AfterClass
    public static void stopCassandraEmbedded() throws Exception {
        logger.debug("Cleaning Embedded Cassandra...");
        EmbeddedCassandraServerHelper.cleanEmbeddedCassandra();
        logger.debug("Embedded Cassandra Cleaned");
    }

    @Test
    public void shouldAssertApplicationIsInserted() {
        logger.debug("Creating CassandraApplication...");
        final CassandraApplication cassandraApplication = TestUtils.createDummyCassApplication();
        final UUID applicationId = cassandraApplication.getApplicationId();
        logger.debug("Created CassandraApplication with id: [{}]", applicationId);

        cassandraTemplate.insert(cassandraApplication);
        logger.debug("CassandraApplication with id [{}] inserted into Cassandra", applicationId);

        final Select select = QueryBuilder.select().from(CassandraApplication.TABLE_NAME).where(
                        QueryBuilder.eq(CassandraApplication.APPLICATION_ID, applicationId)).limit(10);

        final CassandraApplication result = cassandraTemplate.selectOne(select, CassandraApplication.class);

        assertEquals(applicationId, result.getApplicationId());

    }

    @Test
    public void shouldAssertApplicationIsInsertedAndUpdated() {
        final CassandraApplication cassandraApplication = insertApplication();

        cassandraApplication.setAllowedServiceIds(UPDATED_ALLOWED_SERVICE_TYPE_IDS);

        cassandraTemplate.update(cassandraApplication);

        final Select select = QueryBuilder.select().from(CassandraApplication.TABLE_NAME)
                                          .where(QueryBuilder.eq(
                                              CassandraApplication.APPLICATION_ID,
                                              cassandraApplication.getApplicationId())).limit(10);

        CassandraApplication updatedResult = cassandraTemplate.selectOne(select, CassandraApplication.class);

        assertEquals(UPDATED_ALLOWED_SERVICE_TYPE_IDS, updatedResult.getAllowedServiceIds());

    }

    @Test
    public void shouldAssertApplicationIsDeleted() {
        final CassandraApplication cassandraApplication = insertApplication();

        cassandraTemplate.delete(cassandraApplication);

        final Select select = QueryBuilder.select().from(CassandraApplication.TABLE_NAME)
                                          .where(QueryBuilder.eq(CassandraApplication.APPLICATION_ID, cassandraApplication.getApplicationId())).limit(10);

        CassandraApplication updatedResult = cassandraTemplate.selectOne(select, CassandraApplication.class);
        assertNull(updatedResult);

    }

    private CassandraApplication insertApplication() {
        logger.debug("Creating CassandraApplication...");
        final CassandraApplication cassandraApplication = TestUtils.createDummyCassApplication();
        final UUID applicationId = cassandraApplication.getApplicationId();
        logger.debug("Created CassandraApplication with applicationId: [{}]", applicationId);

        cassandraTemplate.insert(cassandraApplication);
        logger.debug("CassandraApplication with applicationId [{}] inserted into Cassandra", applicationId);

        final Select select = QueryBuilder.select().from(CassandraApplication.TABLE_NAME)
                                          .where(QueryBuilder.eq(CassandraApplication.APPLICATION_ID, applicationId)).limit(10);

        final CassandraApplication result = cassandraTemplate.selectOne(select, CassandraApplication.class);

        assertEquals(applicationId, result.getApplicationId());

        return cassandraApplication;
    }
}
