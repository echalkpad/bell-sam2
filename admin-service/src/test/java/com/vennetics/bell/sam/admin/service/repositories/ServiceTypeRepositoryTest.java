package com.vennetics.bell.sam.admin.service.repositories;

import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.core.querybuilder.Select;
import com.vennetics.bell.sam.admin.service.AdminServiceCassandraTestConfig;
import com.vennetics.bell.sam.admin.service.utils.TestUtils;
import com.vennetics.bell.sam.core.cassandra.helpers.EmbeddedCassandraTestHelper;
import com.vennetics.bell.sam.model.admin.service.entities.cassandra.CassandraServiceType;
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * CassandraServiceType repo test
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = AdminServiceCassandraTestConfig.class)
public class ServiceTypeRepositoryTest {


    private static final Logger logger = LoggerFactory.getLogger(ServiceTypeRepositoryTest.class);
    public static final Set<String> UPDATED_POLICY_TYPE_IDS = new HashSet<String>(
                    Arrays.asList("PolicyType1", "PolicyType2"));

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
                                                          TestUtils.CREATE_SERVICE_TYPE_TABLE);
    }

    @Before
    public void createTable() {
        cassandraAdminTemplate.createTable(true, CqlIdentifier.cqlId(CassandraServiceType.TABLE_NAME),
                                           CassandraServiceType.class, new HashMap<String, Object>());
        logger.debug("CassandraServiceType Table Created");

    }

    @After
    public void dropTable() {
        cassandraAdminTemplate.dropTable(CqlIdentifier.cqlId(CassandraServiceType.TABLE_NAME));
        logger.debug("CassandraServiceType Table Dropped");
    }

    @AfterClass
    public static void stopCassandraEmbedded() throws Exception {
        logger.debug("Cleaning Embedded Cassandra...");
        EmbeddedCassandraServerHelper.cleanEmbeddedCassandra();
        logger.debug("Embedded Cassandra Cleaned");
    }

    @Test
    public void shouldAssertServiceTypeIsInserted() {
        logger.debug("Creating CassandraServiceType...");
        final CassandraServiceType cassandraServiceType = TestUtils.createDummyCassServiceType();
        final String serviceTypeId = cassandraServiceType.getServiceTypeId();
        logger.debug("Created CassandraServiceType with id: [{}]", serviceTypeId);

        cassandraTemplate.insert(cassandraServiceType);
        logger.debug("CassandraServiceType with id [{}] inserted into Cassandra", serviceTypeId);

        final Select select = QueryBuilder.select().from(CassandraServiceType.TABLE_NAME).where(
                        QueryBuilder.eq(CassandraServiceType.SERVICE_TYPE_ID, serviceTypeId)).limit(10);

        final CassandraServiceType result = cassandraTemplate.selectOne(select, CassandraServiceType.class);

        assertEquals(serviceTypeId, result.getServiceTypeId());

    }

    @Test
    public void shouldAssertServiceTypeIsInsertedAndUpdated() {
        final CassandraServiceType cassandraServiceType = insertServiceType();

        cassandraServiceType.setPolicyTypeIds(UPDATED_POLICY_TYPE_IDS);

        cassandraTemplate.update(cassandraServiceType);

        final Select select = QueryBuilder.select().from(CassandraServiceType.TABLE_NAME)
                                          .where(QueryBuilder.eq(
                                              CassandraServiceType.SERVICE_TYPE_ID,
                                              cassandraServiceType.getServiceTypeId())).limit(10);

        CassandraServiceType updatedResult = cassandraTemplate.selectOne(select, CassandraServiceType.class);

        assertEquals(UPDATED_POLICY_TYPE_IDS, updatedResult.getPolicyTypeIds());

    }

    @Test
    public void shouldAssertServiceTypeIsDeleted() {
        final CassandraServiceType cassandraServiceType = insertServiceType();

        cassandraTemplate.delete(cassandraServiceType);

        final Select select = QueryBuilder.select().from(CassandraServiceType.TABLE_NAME)
                                          .where(QueryBuilder.eq(CassandraServiceType.SERVICE_TYPE_ID, cassandraServiceType.getServiceTypeId())).limit(10);

        CassandraServiceType updatedResult = cassandraTemplate.selectOne(select, CassandraServiceType.class);
        assertNull(updatedResult);

    }

    private CassandraServiceType insertServiceType() {
        logger.debug("Creating CassandraServiceType...");
        final CassandraServiceType cassandraServiceType = TestUtils.createDummyCassServiceType();
        final String serviceTypeId = cassandraServiceType.getServiceTypeId();
        logger.debug("Created CassandraServiceType with serviceTypeId: [{}]", serviceTypeId);

        cassandraTemplate.insert(cassandraServiceType);
        logger.debug("CassandraServiceType with serviceTypeId [{}] inserted into Cassandra", serviceTypeId);

        final Select select = QueryBuilder.select().from(CassandraServiceType.TABLE_NAME)
                                          .where(QueryBuilder.eq(CassandraServiceType.SERVICE_TYPE_ID, serviceTypeId)).limit(10);

        final CassandraServiceType result = cassandraTemplate.selectOne(select, CassandraServiceType.class);

        assertEquals(serviceTypeId, result.getServiceTypeId());

        return cassandraServiceType;
    }
}
