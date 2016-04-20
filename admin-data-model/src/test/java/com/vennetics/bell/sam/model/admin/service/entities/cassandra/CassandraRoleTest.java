package com.vennetics.bell.sam.model.admin.service.entities.cassandra;

import com.vennetics.bell.sam.model.admin.service.TestUtils;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * CassandraRole Entity Test
 */
public class CassandraRoleTest {

    private CassandraRole cassandraRole;

    @Before
    public void setUp() {
        // Create test data
        cassandraRole = new CassandraRole();
        cassandraRole.setRoleId(TestUtils.TEST_ROLE_ID_1);
        cassandraRole.setDescription(TestUtils.TEST_DESCRIPTION);
        cassandraRole.setServiceTypeIds(TestUtils.TEST_SERVICES);
        cassandraRole.setApplicationIds(TestUtils.TEST_APPLICATIONS);
    }

    @Test
    public void testRole() {

        assertEquals(cassandraRole.getRoleId(), TestUtils.TEST_ROLE_ID_1);
        assertEquals(cassandraRole.getDescription(), TestUtils.TEST_DESCRIPTION);
        assertEquals(cassandraRole.getServiceTypeIds(), TestUtils.TEST_SERVICES);
        assertEquals(cassandraRole.getApplicationIds(), TestUtils.TEST_APPLICATIONS);
    }

    @Test
    public void testEqualsRules() {
        EqualsVerifier.forClass(CassandraRole.class).suppress(Warning.NONFINAL_FIELDS).verify();
    }
}
