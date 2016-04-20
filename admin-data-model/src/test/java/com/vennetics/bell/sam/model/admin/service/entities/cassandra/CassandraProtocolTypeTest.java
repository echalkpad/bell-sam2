package com.vennetics.bell.sam.model.admin.service.entities.cassandra;

import com.vennetics.bell.sam.model.admin.service.TestUtils;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Protocol Type Entity Test
 */
public class CassandraProtocolTypeTest {

    private CassandraProtocolType cassandraProtocolType;

    @Before
    public void setUp() {
        // Create test data
        cassandraProtocolType = new CassandraProtocolType();
        cassandraProtocolType.setProtocolTypeId(TestUtils.TEST_PROTOCOL_TYPE_ID);
        cassandraProtocolType.setDescription(TestUtils.TEST_DESCRIPTION);
        cassandraProtocolType.setServiceTypeIds(TestUtils.TEST_SERVICES);
    }

    @Test
    public void testProtocolType() {

        assertEquals(cassandraProtocolType.getProtocolTypeId(), TestUtils.TEST_PROTOCOL_TYPE_ID);
        assertEquals(cassandraProtocolType.getDescription(), TestUtils.TEST_DESCRIPTION);
        assertEquals(cassandraProtocolType.getServiceTypeIds(), TestUtils.TEST_SERVICES);
    }

    @Test
    public void testEqualsRules() {
        EqualsVerifier.forClass(CassandraProtocolType.class).suppress(Warning.NONFINAL_FIELDS).verify();
    }
}
