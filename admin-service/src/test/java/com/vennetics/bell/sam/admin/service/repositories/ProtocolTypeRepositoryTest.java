package com.vennetics.bell.sam.admin.service.repositories;

import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.core.querybuilder.Select;
import com.vennetics.bell.sam.admin.service.AdminServiceCassandraTestConfig;
import com.vennetics.bell.sam.admin.service.utils.TestUtils;
import com.vennetics.bell.sam.core.cassandra.helpers.EmbeddedCassandraTestHelper;
import com.vennetics.bell.sam.model.admin.service.entities.cassandra.CassandraProtocolType;
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
 * CassandraProtocolType repo test
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = AdminServiceCassandraTestConfig.class)
public class ProtocolTypeRepositoryTest {


    private static final Logger logger = LoggerFactory.getLogger(ProtocolTypeRepositoryTest.class);
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
                                                          TestUtils.CREATE_PROTOCOL_TYPE_TABLE);
    }

    @Before
    public void createTable() {
        cassandraAdminTemplate.createTable(true, CqlIdentifier.cqlId(CassandraProtocolType.TABLE_NAME),
                                           CassandraProtocolType.class, new HashMap<String, Object>());
        logger.debug("CassandraProtocolType Table Created");

    }

    @After
    public void dropTable() {
        cassandraAdminTemplate.dropTable(CqlIdentifier.cqlId(CassandraProtocolType.TABLE_NAME));
        logger.debug("CassandraProtocolType Table Dropped");
    }

    @AfterClass
    public static void stopCassandraEmbedded() throws Exception {
        logger.debug("Cleaning Embedded Cassandra...");
        EmbeddedCassandraServerHelper.cleanEmbeddedCassandra();
        logger.debug("Embedded Cassandra Cleaned");
    }

    @Test
    public void shouldAssertProtocolTypeIsInserted() {
        logger.debug("Creating CassandraProtocolType...");
        final CassandraProtocolType cassandraProtocolType = TestUtils.createDummyCassProtocolType();
        final String protocolTypeId = cassandraProtocolType.getProtocolTypeId();
        logger.debug("Created CassandraProtocolType with id: [{}]", protocolTypeId);

        cassandraTemplate.insert(cassandraProtocolType);
        logger.debug("CassandraProtocolType with id [{}] inserted into Cassandra", protocolTypeId);

        final Select select = QueryBuilder.select().from(CassandraProtocolType.TABLE_NAME).where(
                        QueryBuilder.eq(CassandraProtocolType.PROTOCOL_TYPE_ID, protocolTypeId)).limit(10);

        final CassandraProtocolType result = cassandraTemplate.selectOne(select, CassandraProtocolType.class);

        assertEquals(protocolTypeId, result.getProtocolTypeId());

    }

    @Test
    public void shouldAssertProtocolTypeIsInsertedAndUpdated() {
        final CassandraProtocolType cassandraProtocolType = insertProtocolType();

        cassandraProtocolType.setServiceTypeIds(UPDATED_SERVICE_TYPE_IDS);

        cassandraTemplate.update(cassandraProtocolType);

        final Select select = QueryBuilder.select().from(CassandraProtocolType.TABLE_NAME)
                                          .where(QueryBuilder.eq(
                                              CassandraProtocolType.PROTOCOL_TYPE_ID,
                                              cassandraProtocolType.getProtocolTypeId())).limit(10);

        CassandraProtocolType updatedResult = cassandraTemplate.selectOne(select, CassandraProtocolType.class);

        assertEquals(UPDATED_SERVICE_TYPE_IDS, updatedResult.getServiceTypeIds());

    }

    @Test
    public void shouldAssertProtocolTypeIsDeleted() {
        final CassandraProtocolType cassandraProtocolType = insertProtocolType();

        cassandraTemplate.delete(cassandraProtocolType);

        final Select select = QueryBuilder.select().from(CassandraProtocolType.TABLE_NAME)
                                          .where(QueryBuilder.eq(CassandraProtocolType.PROTOCOL_TYPE_ID, cassandraProtocolType.getProtocolTypeId())).limit(10);

        CassandraProtocolType updatedResult = cassandraTemplate.selectOne(select, CassandraProtocolType.class);
        assertNull(updatedResult);

    }

    private CassandraProtocolType insertProtocolType() {
        logger.debug("Creating CassandraProtocolType...");
        final CassandraProtocolType cassandraProtocolType = TestUtils.createDummyCassProtocolType();
        final String protocolTypeId = cassandraProtocolType.getProtocolTypeId();
        logger.debug("Created CassandraProtocolType with protocolTypeId: [{}]", protocolTypeId);

        cassandraTemplate.insert(cassandraProtocolType);
        logger.debug("CassandraProtocolType with protocolTypeId [{}] inserted into Cassandra", protocolTypeId);

        final Select select = QueryBuilder.select().from(CassandraProtocolType.TABLE_NAME)
                                          .where(QueryBuilder.eq(CassandraProtocolType.PROTOCOL_TYPE_ID, protocolTypeId)).limit(10);

        final CassandraProtocolType result = cassandraTemplate.selectOne(select, CassandraProtocolType.class);

        assertEquals(protocolTypeId, result.getProtocolTypeId());

        return cassandraProtocolType;
    }
}
