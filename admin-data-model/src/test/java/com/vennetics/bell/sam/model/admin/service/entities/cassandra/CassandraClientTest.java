package com.vennetics.bell.sam.model.admin.service.entities.cassandra;

import com.vennetics.bell.sam.model.admin.service.TestUtils;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * CassandraClient Entity Test
 */
public class CassandraClientTest {

    private CassandraClient cassandraClient;

    @Before
    public void setUp() {
        // Create test data
        cassandraClient = new CassandraClient();
        cassandraClient.setApplicationId(TestUtils.TEST_APPLICATION_ID_1);
        cassandraClient.setClientId(TestUtils.TEST_CLIENT_ID_1);
        cassandraClient.setCreatedTimestamp(TestUtils.TEST_CREATION_DATE);
        cassandraClient.setLastModifiedTimestamp(TestUtils.TEST_MODIFIED_DATE);
    }

    @Test
    public void testClient() {

        assertEquals(cassandraClient.getApplicationId(), TestUtils.TEST_APPLICATION_ID_1);
        assertEquals(cassandraClient.getClientId(), TestUtils.TEST_CLIENT_ID_1);
        assertEquals(cassandraClient.getCreatedTimestamp(), TestUtils.TEST_CREATION_DATE);
        assertEquals(cassandraClient.getLastModifiedTimestamp(), TestUtils.TEST_MODIFIED_DATE);
    }

    @Test
    public void testEqualsRules() {
        EqualsVerifier.forClass(CassandraClient.class).suppress(Warning.NONFINAL_FIELDS).verify();
    }
}
