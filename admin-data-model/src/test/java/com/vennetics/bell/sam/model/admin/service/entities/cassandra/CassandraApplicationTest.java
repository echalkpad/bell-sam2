package com.vennetics.bell.sam.model.admin.service.entities.cassandra;

import com.vennetics.bell.sam.model.admin.service.TestUtils;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * CassandraApplication Entity Test
 */
public final class CassandraApplicationTest {

    private CassandraApplication cassandraApplication;
    public static final BCryptPasswordEncoder PASSWORD_ENCODER = new BCryptPasswordEncoder();

    @Before
    public void setUp() {
        // Create test data
        cassandraApplication = new CassandraApplication();
        cassandraApplication.setApplicationId(TestUtils.TEST_APPLICATION_ID_1);
        cassandraApplication.setClientId(TestUtils.TEST_CLIENT_ID_1);
        cassandraApplication.setRoleIds(TestUtils.TEST_ROLES);
        cassandraApplication.setPolicies(TestUtils.TEST_POLICIES);
        cassandraApplication.setGatewayPolicies(TestUtils.TEST_GW_POLICIES);
        cassandraApplication.setAllowedServiceIds(TestUtils.TEST_SERVICES);
        cassandraApplication.setName(TestUtils.TEST_NAME);
        cassandraApplication.setEmail(TestUtils.TEST_EMAIL);
        cassandraApplication.setSecret(TestUtils.TEST_SECRET);
        cassandraApplication.setAccountState(TestUtils.TEST_ACCOUNT_STATE);
        cassandraApplication.setCreatedTimestamp(TestUtils.TEST_CREATION_DATE);
        cassandraApplication.setLastModifiedTimestamp(TestUtils.TEST_MODIFIED_DATE);
    }

    @Test
    public void testApplication() {

        assertEquals(cassandraApplication.getApplicationId(), TestUtils.TEST_APPLICATION_ID_1);
        assertEquals(cassandraApplication.getClientId(), TestUtils.TEST_CLIENT_ID_1);
        assertEquals(cassandraApplication.getRoleIds(), TestUtils.TEST_ROLES);
        assertEquals(cassandraApplication.getPolicies(), TestUtils.TEST_POLICIES);
        assertEquals(cassandraApplication.getGatewayPolicies(), TestUtils.TEST_GW_POLICIES);
        assertEquals(cassandraApplication.getAllowedServiceIds(), TestUtils.TEST_SERVICES);
        assertEquals(cassandraApplication.getName(), TestUtils.TEST_NAME);
        assertEquals(cassandraApplication.getEmail(), TestUtils.TEST_EMAIL);
        assertEquals(cassandraApplication.getAccountState(), TestUtils.TEST_ACCOUNT_STATE.toString());
        assertEquals(cassandraApplication.getCreatedTimestamp(), TestUtils.TEST_CREATION_DATE);
        assertEquals(cassandraApplication.getLastModifiedTimestamp(), TestUtils.TEST_MODIFIED_DATE);
        assertTrue(PASSWORD_ENCODER.matches(TestUtils.TEST_SECRET, cassandraApplication.getSecret()));

        cassandraApplication.setSecret(PASSWORD_ENCODER.encode("encodedSecret"));
        assertTrue(PASSWORD_ENCODER.matches("encodedSecret", cassandraApplication.getSecret()));

    }

    @Test
    public void testEqualsRules() {
        EqualsVerifier.forClass(CassandraApplication.class).suppress(Warning.NONFINAL_FIELDS).verify();
    }
}
