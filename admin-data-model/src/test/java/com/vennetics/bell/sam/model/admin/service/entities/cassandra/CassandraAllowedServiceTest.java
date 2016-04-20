package com.vennetics.bell.sam.model.admin.service.entities.cassandra;

import com.vennetics.bell.sam.model.admin.service.TestUtils;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * CassandraAllowedService Entity Test
 */
public class CassandraAllowedServiceTest {

    private CassandraAllowedService cassandraAllowedService;

    @Before
    public void setUp() {
        // Create test data
        cassandraAllowedService = new CassandraAllowedService();
        cassandraAllowedService.setAllowedServiceId(TestUtils.TEST_SERVICE_TYPE_ID_1);
        cassandraAllowedService.setApplicationIds(TestUtils.TEST_APPLICATIONS);
        cassandraAllowedService.setCreatedTimestamp(TestUtils.TEST_CREATION_DATE);
        cassandraAllowedService.setLastModifiedTimestamp(TestUtils.TEST_MODIFIED_DATE);
    }

    @Test
    public void testAllowedService() {
        assertEquals(TestUtils.TEST_SERVICE_TYPE_ID_1, cassandraAllowedService.getAllowedServiceId());
        assertEquals(TestUtils.TEST_APPLICATIONS, cassandraAllowedService.getApplicationIds());
        assertEquals(TestUtils.TEST_CREATION_DATE, cassandraAllowedService.getCreatedTimestamp());
        assertEquals(TestUtils.TEST_MODIFIED_DATE, cassandraAllowedService.getLastModifiedTimestamp());
    }

    @Test
    public void testEqualsRules() {
        EqualsVerifier.forClass(CassandraAllowedService.class).suppress(Warning.NONFINAL_FIELDS).verify();
    }
}
