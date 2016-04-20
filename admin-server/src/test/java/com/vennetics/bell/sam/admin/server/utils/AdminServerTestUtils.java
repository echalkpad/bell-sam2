package com.vennetics.bell.sam.admin.server.utils;

import com.datastax.driver.core.utils.UUIDs;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vennetics.bell.sam.model.admin.service.AccountState;
import com.vennetics.bell.sam.model.admin.service.entities.cassandra.CassandraApplication;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Admin Server Test Utils Class
 */
public final class AdminServerTestUtils {

    private AdminServerTestUtils() {
        // Checkstyle prevent instantiation of util class
    }

    // IDs
    public static final UUID TEST_APPLICATION_ID = UUIDs.timeBased();
    public static final String TEST_CLIENT_ID = "testClientId";

    public static final String TEST_NAME = "testName";
    public static final String TEST_EMAIL = "testEmail";
    public static final String TEST_SECRET = "testSecret";
    public static final String TEST_ACCOUNT_STATE = AccountState.ACTIVE.toString();
    public static final Date TEST_CREATION_DATE = new Date();
    public static final Date TEST_MODIFIED_DATE = new Date();

    // Sets
    public static final Set<String> TEST_ROLES =  Collections.unmodifiableSet(new HashSet<String>(Arrays.asList("testRoleId1",
                                                                                   "testRoleId2")));
    public static final Set<String> TEST_POLICIES =  Collections.unmodifiableSet(new HashSet<String>(Arrays.asList(
                    "testPolicyId1",
                    "testPolicyId2")));
    public static final Set<String> TEST_GW_POLICIES =  Collections.unmodifiableSet(new HashSet<String>(Arrays.asList(
                    "testGwPolicyId1",
                    "testGwPolicyId2")));
    public static final Set<String> TEST_SERVICES =  Collections.unmodifiableSet(new HashSet<String>(Arrays.asList(
                    "testServiceId1",
                    "testServiceId2")));

    public static final String UPDATED_TEST_NAME = "updatesTestName";

    // DB Stuff
    public static final String KEYSPACE_CREATE = "CREATE KEYSPACE IF NOT EXISTS bell_sam_test "
                    + "WITH replication = {'class': 'SimpleStrategy', 'replication_factor': '1'} "
                    + "AND durable_writes = true;";
    public static final String CREATE_ALLOWED_SERVICES_TABLE = "CREATE TABLE bell_sam_test"
                    + ".allowed_services(allowed_service_id text PRIMARY KEY, "
                    + "application_ids set<uuid>, created_timestamp timestamp, "
                    + "last_modified_timestamp timestamp);";
    public static final String CREATE_APPLICATION_TABLE = "CREATE TABLE bell_sam_test.applications"
                    + "(application_id uuid PRIMARY KEY, client_id text, role_ids "
                    + "set<text>, policies set<text>, gateway_policies set<text>, "
                    + "allowed_service_ids set<text>, name text, email text, secret text, "
                    + "account_state text, created_timestamp timestamp, last_modified_timestamp "
                    + "timestamp);";
    public static final String CREATE_CLIENT_TABLE = "CREATE TABLE bell_sam_test.clients(client_id "
                    + "text PRIMARY KEY, application_id uuid, created_timestamp timestamp, "
                    + "last_modified_timestamp timestamp);";
    public static final String CREATE_POLICY_TYPE_TABLE = "CREATE TABLE bell_sam_test.policy_types"
                    + "(policy_type_id text PRIMARY KEY, name text, policy_order int, service_type_ids "
                    + "set<text>);";
    public static final String CREATE_PROTOCOL_TYPE_TABLE = "CREATE TABLE bell_sam_test.protocol_types"
                    + "(protocol_type_Id text PRIMARY KEY, description text, service_type_ids "
                    + "set<text>);";
    public static final String CREATE_ROLE_TABLE = "CREATE TABLE bell_sam_test.roles"
                    + "(role_id text PRIMARY KEY, description text, service_type_ids "
                    + "set<text>, application_ids set<uuid>);";
    public static final String CREATE_SERVICE_TYPE_TABLE = "CREATE TABLE bell_sam_test.service_types"
                    + "(service_type_id text PRIMARY KEY, name text, policy_type_ids set<text>, "
                    + "protocol_type_id text);";

    public static final String KEYSPACE_NAME = "bell_sam_test";
    public static final String CASSANDRA_HOST = "localhost";

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

    public static byte[] convertObjectToJsonBytes(final Object object) throws IOException {
        final ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return mapper.writeValueAsBytes(object);
    }
}
