package com.vennetics.bell.sam.apigateway.utils;

import com.vennetics.bell.sam.model.admin.service.AccountState;
import com.vennetics.bell.sam.model.admin.service.entities.cassandra.CassandraApplication;
import com.vennetics.bell.sam.model.admin.service.entities.cassandra.CassandraClient;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Test Utils Class
 */
public final class TestUtils {

    private TestUtils() {
        // Checkstyle prevent instantiation of util class
    }

    // IDs
    public static final UUID TEST_APPLICATION_ID = UUID.randomUUID();
    public static final String TEST_CLIENT_ID = "testClientId";
    public static final String TEST_NAME = "testName";
    public static final String TEST_EMAIL = "testEmail";
    public static final String TEST_SECRET = "testSecret";
    public static final String TEST_ACCOUNT_STATE = AccountState.ACTIVE.toString();
    public static final Date TEST_CREATION_DATE = new Date();
    public static final Date TEST_MODIFIED_DATE = new Date();

    // Sets
    public static final Set<String> TEST_ROLES =  Collections.unmodifiableSet(new HashSet<String>(Arrays.asList("admin")));
    public static final Set<String> TEST_POLICIES =  Collections.unmodifiableSet(new HashSet<String>(Arrays.asList(
                    "testPolicyId1",
                    "testPolicyId2")));
    public static final Set<String> TEST_GW_POLICIES =  Collections.unmodifiableSet(new HashSet<String>(Arrays.asList(
                    "testGwPolicyId1",
                    "testGwPolicyId2")));
    public static final Set<String> TEST_SERVICES =  Collections.unmodifiableSet(new HashSet<String>(Arrays.asList(
                    "testServiceId1",
                    "testServiceId2")));


    public static CassandraApplication createDummyApplication() {
        CassandraApplication cassandraApplication = new CassandraApplication();
        cassandraApplication.setApplicationId(TEST_APPLICATION_ID);
        cassandraApplication.setClientId(TEST_CLIENT_ID);
        cassandraApplication.setRoleIds(TEST_ROLES);
        cassandraApplication.setPolicies(TEST_POLICIES);
        cassandraApplication.setGatewayPolicies(TEST_GW_POLICIES);
        cassandraApplication.setAllowedServiceIds(TEST_SERVICES);
        cassandraApplication.setName(TEST_NAME);
        cassandraApplication.setEmail(TEST_EMAIL);
        cassandraApplication.setSecret(TEST_SECRET);
        cassandraApplication.setAccountState(TEST_ACCOUNT_STATE);
        cassandraApplication.setCreatedTimestamp(TEST_CREATION_DATE);
        cassandraApplication.setLastModifiedTimestamp(TEST_MODIFIED_DATE);
        return cassandraApplication;
    }

    public static CassandraClient createDummyClient() {
        CassandraClient cassandraClient = new CassandraClient();
        cassandraClient.setClientId(TEST_CLIENT_ID);
        cassandraClient.setApplicationId(TEST_APPLICATION_ID);
        cassandraClient.setCreatedTimestamp(TEST_CREATION_DATE);
        cassandraClient.setLastModifiedTimestamp(TEST_MODIFIED_DATE);
        return cassandraClient;
    }
}
