package com.vennetics.bell.sam.admin.service;

import com.vennetics.bell.sam.model.admin.service.entities.cassandra.CassandraRole;
import org.junit.Test;

import static com.vennetics.bell.sam.admin.service.utils.TestUtils.APPLICATION_IDS;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Admin Service Config test class
 */
public class AdminServiceConfigTest {

    @Test
    public void testIsDefaultUserRequired() {
        CassandraRole cassandraRole = new CassandraRole();

        // CassandraRole is NULL
        assertTrue(AdminServiceConfig.isDefaultUserRequired(cassandraRole));

        // CassandraRole is not NULL
        // CassandraRole is not NULL but does not have applicationIds
        cassandraRole.setRoleId("admin");
        assertTrue(AdminServiceConfig.isDefaultUserRequired(cassandraRole));

        // CassandraRole is not NULL and does have applicationIds
        cassandraRole.setApplicationIds(APPLICATION_IDS);
        assertFalse(AdminServiceConfig.isDefaultUserRequired(cassandraRole));
    }
}
