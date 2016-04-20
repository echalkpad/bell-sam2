package com.vennetics.bell.sam.admin.service.repositories;

import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.core.querybuilder.Select;
import com.vennetics.bell.sam.admin.service.AdminServiceCassandraTestConfig;
import com.vennetics.bell.sam.admin.service.utils.TestUtils;
import com.vennetics.bell.sam.core.cassandra.helpers.EmbeddedCassandraTestHelper;
import com.vennetics.bell.sam.model.admin.service.entities.cassandra.CassandraRole;
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
 * CassandraRole repo test
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = AdminServiceCassandraTestConfig.class)
public class RoleRepositoryTest {


    private static final Logger logger = LoggerFactory.getLogger(RoleRepositoryTest.class);
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
                                                          TestUtils.CREATE_ROLE_TABLE);
    }

    @Before
    public void createTable() {
        cassandraAdminTemplate.createTable(true, CqlIdentifier.cqlId(CassandraRole.TABLE_NAME),
                                           CassandraRole.class, new HashMap<String, Object>());
        logger.debug("CassandraRole Table Created");

    }

    @After
    public void dropTable() {
        cassandraAdminTemplate.dropTable(CqlIdentifier.cqlId(CassandraRole.TABLE_NAME));
        logger.debug("CassandraRole Table Dropped");
    }

    @AfterClass
    public static void stopCassandraEmbedded() throws Exception {
        logger.debug("Cleaning Embedded Cassandra...");
        EmbeddedCassandraServerHelper.cleanEmbeddedCassandra();
        logger.debug("Embedded Cassandra Cleaned");
    }

    @Test
    public void shouldAssertRoleIsInserted() {
        logger.debug("Creating CassandraRole...");
        final CassandraRole cassandraRole = TestUtils.createDummyCassRole();
        final String roleTypeId = cassandraRole.getRoleId();
        logger.debug("Created CassandraRole with id: [{}]", roleTypeId);

        cassandraTemplate.insert(cassandraRole);
        logger.debug("CassandraRole with id [{}] inserted into Cassandra", roleTypeId);

        final Select select = QueryBuilder.select().from(CassandraRole.TABLE_NAME).where(
                        QueryBuilder.eq(CassandraRole.ROLE_ID, roleTypeId)).limit(10);

        final CassandraRole result = cassandraTemplate.selectOne(select, CassandraRole.class);

        assertEquals(roleTypeId, result.getRoleId());

    }

    @Test
    public void shouldAssertRoleIsInsertedAndUpdated() {
        final CassandraRole cassandraRole = insertRole();

        cassandraRole.setServiceTypeIds(UPDATED_SERVICE_TYPE_IDS);

        cassandraTemplate.update(cassandraRole);

        final Select select = QueryBuilder.select().from(CassandraRole.TABLE_NAME)
                                          .where(QueryBuilder.eq(
                                              CassandraRole.ROLE_ID,
                                              cassandraRole.getRoleId())).limit(10);

        CassandraRole updatedResult = cassandraTemplate.selectOne(select, CassandraRole.class);

        assertEquals(UPDATED_SERVICE_TYPE_IDS, updatedResult.getServiceTypeIds());

    }

    @Test
    public void shouldAssertRoleIsDeleted() {
        final CassandraRole cassandraRole = insertRole();

        cassandraTemplate.delete(cassandraRole);

        final Select select = QueryBuilder.select().from(CassandraRole.TABLE_NAME)
                                          .where(QueryBuilder.eq(CassandraRole.ROLE_ID, cassandraRole.getRoleId())).limit(10);

        CassandraRole updatedResult = cassandraTemplate.selectOne(select, CassandraRole.class);
        assertNull(updatedResult);

    }

    private CassandraRole insertRole() {
        logger.debug("Creating CassandraRole...");
        final CassandraRole cassandraRole = TestUtils.createDummyCassRole();
        final String roleTypeId = cassandraRole.getRoleId();
        logger.debug("Created CassandraRole with roleTypeId: [{}]", roleTypeId);

        cassandraTemplate.insert(cassandraRole);
        logger.debug("CassandraRole with roleTypeId [{}] inserted into Cassandra", roleTypeId);

        final Select select = QueryBuilder.select().from(CassandraRole.TABLE_NAME)
                                          .where(QueryBuilder.eq(CassandraRole.ROLE_ID, roleTypeId)).limit(10);

        final CassandraRole result = cassandraTemplate.selectOne(select, CassandraRole.class);

        assertEquals(roleTypeId, result.getRoleId());

        return cassandraRole;
    }
}
