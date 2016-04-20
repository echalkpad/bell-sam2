package com.vennetics.bell.sam.admin.service.repositories;

import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.core.querybuilder.Select;
import com.vennetics.bell.sam.admin.service.AdminServiceCassandraTestConfig;
import com.vennetics.bell.sam.admin.service.utils.TestUtils;
import com.vennetics.bell.sam.core.cassandra.helpers.EmbeddedCassandraTestHelper;
import com.vennetics.bell.sam.model.admin.service.entities.cassandra.CassandraPolicyType;
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
 * CassandraPolicyType repo test
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = AdminServiceCassandraTestConfig.class)
public class PolicyTypeRepositoryTest {


    private static final Logger logger = LoggerFactory.getLogger(PolicyTypeRepositoryTest.class);
    public static final Set<String> UPDATED_SERVICE_TYPE_IDS = new HashSet<String>(
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
                                                          TestUtils.CREATE_POLICY_TYPE_TABLE);
    }

    @Before
    public void createTable() {
        cassandraAdminTemplate.createTable(true, CqlIdentifier.cqlId(CassandraPolicyType.TABLE_NAME),
                                           CassandraPolicyType.class, new HashMap<String, Object>());
        logger.debug("CassandraPolicyType Table Created");

    }

    @After
    public void dropTable() {
        cassandraAdminTemplate.dropTable(CqlIdentifier.cqlId(CassandraPolicyType.TABLE_NAME));
        logger.debug("CassandraPolicyType Table Dropped");
    }

    @AfterClass
    public static void stopCassandraEmbedded() throws Exception {
        logger.debug("Cleaning Embedded Cassandra...");
        EmbeddedCassandraServerHelper.cleanEmbeddedCassandra();
        logger.debug("Embedded Cassandra Cleaned");
    }

    @Test
    public void shouldAssertPolicyTypeIsInserted() {
        logger.debug("Creating CassandraPolicyType...");
        final CassandraPolicyType cassandraPolicyType = TestUtils.createDummyCassPolicyType();
        final String policyTypeId = cassandraPolicyType.getPolicyTypeId();
        logger.debug("Created CassandraPolicyType with id: [{}]", policyTypeId);

        cassandraTemplate.insert(cassandraPolicyType);
        logger.debug("CassandraPolicyType with id [{}] inserted into Cassandra", policyTypeId);

        final Select select = QueryBuilder.select().from(CassandraPolicyType.TABLE_NAME).where(
                        QueryBuilder.eq(CassandraPolicyType.POLICY_TYPE_ID, policyTypeId)).limit(10);

        final CassandraPolicyType result = cassandraTemplate.selectOne(select, CassandraPolicyType.class);

        assertEquals(policyTypeId, result.getPolicyTypeId());

    }

    @Test
    public void shouldAssertPolicyTypeIsInsertedAndUpdated() {
        final CassandraPolicyType cassandraPolicyType = insertPolicyType();

        cassandraPolicyType.setServiceTypeIds(UPDATED_SERVICE_TYPE_IDS);

        cassandraTemplate.update(cassandraPolicyType);

        final Select select = QueryBuilder.select().from(CassandraPolicyType.TABLE_NAME)
                                          .where(QueryBuilder.eq(
                                              CassandraPolicyType.POLICY_TYPE_ID,
                                              cassandraPolicyType.getPolicyTypeId())).limit(10);

        CassandraPolicyType updatedResult = cassandraTemplate.selectOne(select, CassandraPolicyType.class);

        assertEquals(UPDATED_SERVICE_TYPE_IDS, updatedResult.getServiceTypeIds());

    }

    @Test
    public void shouldAssertPolicyTypeIsDeleted() {
        final CassandraPolicyType cassandraPolicyType = insertPolicyType();

        cassandraTemplate.delete(cassandraPolicyType);

        final Select select = QueryBuilder.select().from(CassandraPolicyType.TABLE_NAME)
                                          .where(QueryBuilder.eq(CassandraPolicyType.POLICY_TYPE_ID, cassandraPolicyType.getPolicyTypeId())).limit(10);

        CassandraPolicyType updatedResult = cassandraTemplate.selectOne(select, CassandraPolicyType.class);
        assertNull(updatedResult);

    }

    private CassandraPolicyType insertPolicyType() {
        logger.debug("Creating CassandraPolicyType...");
        final CassandraPolicyType cassandraPolicyType = TestUtils.createDummyCassPolicyType();
        final String policyTypeId = cassandraPolicyType.getPolicyTypeId();
        logger.debug("Created CassandraPolicyType with policyTypeId: [{}]", policyTypeId);

        cassandraTemplate.insert(cassandraPolicyType);
        logger.debug("CassandraPolicyType with policyTypeId [{}] inserted into Cassandra", policyTypeId);

        final Select select = QueryBuilder.select().from(CassandraPolicyType.TABLE_NAME)
                                          .where(QueryBuilder.eq(CassandraPolicyType.POLICY_TYPE_ID, policyTypeId)).limit(10);

        final CassandraPolicyType result = cassandraTemplate.selectOne(select, CassandraPolicyType.class);

        assertEquals(policyTypeId, result.getPolicyTypeId());

        return cassandraPolicyType;
    }
}
