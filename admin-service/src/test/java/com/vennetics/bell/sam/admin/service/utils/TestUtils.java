package com.vennetics.bell.sam.admin.service.utils;

import com.datastax.driver.core.utils.UUIDs;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.vennetics.bell.sam.model.admin.service.entities.cassandra.CassandraAllowedService;
import com.vennetics.bell.sam.model.admin.service.entities.cassandra.CassandraApplication;
import com.vennetics.bell.sam.model.admin.service.entities.cassandra.CassandraClient;
import com.vennetics.bell.sam.model.admin.service.entities.cassandra.CassandraPolicyType;
import com.vennetics.bell.sam.model.admin.service.entities.cassandra.CassandraProtocolType;
import com.vennetics.bell.sam.model.admin.service.entities.cassandra.CassandraRole;
import com.vennetics.bell.sam.model.admin.service.entities.cassandra.CassandraServiceType;
import com.vennetics.bell.sam.model.admin.service.pojo.ITypeMapper;
import com.vennetics.bell.sam.model.admin.service.pojo.Policy;
import com.vennetics.bell.sam.model.admin.service.pojo.TypeMapper;
import com.vennetics.bell.sam.rest.config.converters.JsonConverter;
import com.vennetics.microservices.common.api.model.admin.register.pojo.PolicyKeyMetaData;
import com.vennetics.microservices.common.api.model.admin.register.pojo.PolicyMetaData;
import com.vennetics.microservices.common.api.model.admin.register.pojo.ServiceMetaData;
import com.vennetics.microservices.common.api.model.admin.v1.application.ApplicationAllowedService;
import com.vennetics.microservices.common.api.model.admin.v1.application.ApplicationPolicy;
import com.vennetics.microservices.common.api.model.admin.v1.application.ApplicationUserDetails;
import com.vennetics.microservices.common.api.model.admin.v1.application.PolicyType;
import com.vennetics.microservices.common.api.model.admin.v1.application.PolicyTypeList;
import com.vennetics.microservices.common.api.model.admin.v1.application.ProtocolType;
import com.vennetics.microservices.common.api.model.admin.v1.application.Role;
import com.vennetics.microservices.common.api.model.admin.v1.application.ServiceType;

import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Test Utils Class
 */
public final class TestUtils {

    private static ITypeMapper typeMapper = new TypeMapper();

    private TestUtils() {
        // Checkstyle prevent instantiation of util class
    }

    public static final String ACCOUNT_STATE = "ACTIVE";
    public static final String SECRET = "secretPassword";
    public static final String CLIENT_ID_1 = "clientId1";
    public static final String CLIENT_ID_2 = "clientId2";
    public static final String SERVICE_ID = "serviceId";
    public static final String POLICY_ID = "policyId";
    public static final String POLICY_ID_2 = "policyId2";
    public static final String POLICY_NAME = "policyName";
    public static final String NAME = "name";
    public static final String UPDATED_NAME = "updatedName";
    public static final String EMAIL = "email@test.com";
    public static final UUID APPLICATION_ID_1 = UUIDs.timeBased();
    public static final UUID APPLICATION_ID_2 = UUIDs.timeBased();
    public static final String PROTOCOL_TYPE_ID = "testProtocolId";
    public static final String PROTOCOL_TYPE_ID_2 = "testProtocolId2";
    public static final Integer POLICY_ORDER = 1;
    public static final String DESCRIPTION = "testDescription";
    public static final String UPDATED_DESCRIPTION = "updateTestDescription";
    public static final Date CREATION_DATE = new Date();
    public static final Date MODIFIED_DATE = new Date();
    public static final String ROLE_ADMIN = "ROLE_ADMIN";
    public static final String ROLE_USER = "ROLE_USER";
    public static final String KEY = "key";

    public static final Set<String> POLICY_VALUES = ImmutableSet.of("Value1", "Value2");

    public static final Set<String> POLICY_VALUES_2 = ImmutableSet.of("Value3", "Value4");

    public static final Set<String> ROLES = ImmutableSet.of(ROLE_ADMIN, ROLE_USER);

    public static final Map<String, Set<String>> POLICY_MAP = ImmutableMap.of(POLICY_NAME,
                                                                              POLICY_VALUES);

    public static final Map<String, Set<String>> POLICY_MAP_2 = ImmutableMap.of(KEY,
                                                                              POLICY_VALUES_2);

    public static final ApplicationPolicy APPLICATION_POLICY = new ApplicationPolicy(POLICY_ID,
                                                                                     POLICY_MAP);

    public static final ApplicationPolicy APPLICATION_POLICY_2 = new ApplicationPolicy(POLICY_ID_2,
                                                                                     POLICY_MAP_2);

    public static final Set<ApplicationPolicy> APPLICATION_POLICIES = ImmutableSet.of(APPLICATION_POLICY);

    public static final Policy POLICY = new Policy(POLICY_ID, POLICY_MAP, null);

    public static final Set<Policy> POLICIES = ImmutableSet.of(POLICY);

    public static final Set<String> POLICY_IDS = ImmutableSet.of(POLICY_ID);

    public static final Set<String> POLICIES_STRING = typeMapper.toPoliciesAsString(POLICIES);

    public static final Set<String> ROLE_IDS = ImmutableSet.of(ROLE_ADMIN, ROLE_USER);

    public static final Set<String> SERVICE_IDS = ImmutableSet.of(SERVICE_ID);

    public static final Set<UUID> APPLICATION_IDS = ImmutableSet.of(APPLICATION_ID_1, APPLICATION_ID_2);

    public static final Set<UUID> UPDATED_TEST_APPLICATIONS = ImmutableSet.of(UUIDs.timeBased(), UUIDs.timeBased());

    public static final Policy SERVICE_POLICY = new Policy(POLICY_ID, POLICY_MAP, SERVICE_ID);

    public static final Set<Policy> SERVICE_POLICIES = ImmutableSet.of(SERVICE_POLICY);

    public static final Set<String> SERVICE_POLICIES_STRING = typeMapper.toPoliciesAsString(SERVICE_POLICIES);

    public static final PolicyKeyMetaData POLICY_KEY = new PolicyKeyMetaData(KEY, DESCRIPTION);
    public static final Set<PolicyKeyMetaData> POLICY_KEYS = ImmutableSet.of(POLICY_KEY);

    public static final PolicyMetaData POLICY_META_DATA = new PolicyMetaData(POLICY_ID, DESCRIPTION, POLICY_KEYS);
    public static final Set<PolicyMetaData> POLICY_META_DATAS = ImmutableSet.of(POLICY_META_DATA);

    public static final PolicyType POLICY_TYPE = new PolicyType(POLICY_ID, NAME, POLICY_ORDER, SERVICE_IDS);
    public static final PolicyTypeList POLICY_TYPES = new PolicyTypeList(Collections.singletonList(POLICY_TYPE));


    public static final ServiceMetaData SERVICE_TYPE = new ServiceMetaData(SERVICE_ID, DESCRIPTION, POLICY_META_DATAS);
    public static final Set<ServiceMetaData> SERVICE_TYPES = ImmutableSet.of(SERVICE_TYPE);

    public static final ApplicationAllowedService ALLOWED_SERVICE = new ApplicationAllowedService(SERVICE_ID,
                                                                                                  APPLICATION_POLICIES);

    public static final Set<ApplicationAllowedService> ALLOWED_SERVICES = ImmutableSet.of(ALLOWED_SERVICE);

    public static final ApplicationUserDetails FULL_APPLICATION_USER_DETAILS =
        new ApplicationUserDetails(CLIENT_ID_1,
                                   SECRET,
                                   ACCOUNT_STATE,
                                   NAME,
                                   EMAIL,
                                   ROLES,
                                   ALLOWED_SERVICES,
                                   APPLICATION_POLICIES,
                                   APPLICATION_ID_1);

    public static final ApplicationUserDetails RETURNED_APPLICATION_USER_DETAILS =
        new ApplicationUserDetails(CLIENT_ID_1,
                                   NAME,
                                   EMAIL,
                                   ACCOUNT_STATE,
                                   APPLICATION_ID_1,
                                   ROLES,
                                   APPLICATION_POLICIES,
                                   ALLOWED_SERVICES);

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

    public static CassandraAllowedService createDummyCassAllowedService() {
        CassandraAllowedService cassandraAllowedService = new CassandraAllowedService();
        cassandraAllowedService.setAllowedServiceId(SERVICE_ID);
        cassandraAllowedService.setApplicationIds(APPLICATION_IDS);
        cassandraAllowedService.setCreatedTimestamp(CREATION_DATE);
        cassandraAllowedService.setLastModifiedTimestamp(MODIFIED_DATE);
        return cassandraAllowedService;
    }

    public static CassandraApplication createDummyCassApplication() {
        CassandraApplication cassandraApplication = new CassandraApplication();
        cassandraApplication.setApplicationId(APPLICATION_ID_1);
        cassandraApplication.setClientId(CLIENT_ID_1);
        cassandraApplication.setRoleIds(ROLE_IDS);
        cassandraApplication.setPolicies(POLICIES_STRING);
        cassandraApplication.setGatewayPolicies(SERVICE_POLICIES_STRING);
        cassandraApplication.setAllowedServiceIds(SERVICE_IDS);
        cassandraApplication.setName(NAME);
        cassandraApplication.setEmail(EMAIL);
        cassandraApplication.setSecret(SECRET);
        cassandraApplication.setAccountState(ACCOUNT_STATE);
        cassandraApplication.setCreatedTimestamp(CREATION_DATE);
        cassandraApplication.setLastModifiedTimestamp(MODIFIED_DATE);
        return cassandraApplication;
    }

    public static CassandraApplication createDummyCassApplication2() {
        CassandraApplication cassandraApplication = new CassandraApplication();
        cassandraApplication.setApplicationId(APPLICATION_ID_2);
        cassandraApplication.setClientId(CLIENT_ID_2);
        cassandraApplication.setRoleIds(ROLES);
        cassandraApplication.setPolicies(POLICIES_STRING);
        cassandraApplication.setGatewayPolicies(SERVICE_POLICIES_STRING);
        cassandraApplication.setAllowedServiceIds(SERVICE_IDS);
        cassandraApplication.setName(NAME);
        cassandraApplication.setEmail(EMAIL);
        cassandraApplication.setSecret(SECRET);
        cassandraApplication.setAccountState(ACCOUNT_STATE);
        cassandraApplication.setCreatedTimestamp(CREATION_DATE);
        cassandraApplication.setLastModifiedTimestamp(MODIFIED_DATE);
        return cassandraApplication;
    }

    public static CassandraClient createDummyCassClient() {
        CassandraClient cassandraClient = new CassandraClient();
        cassandraClient.setClientId(CLIENT_ID_1);
        cassandraClient.setApplicationId(APPLICATION_ID_1);
        cassandraClient.setCreatedTimestamp(CREATION_DATE);
        cassandraClient.setLastModifiedTimestamp(MODIFIED_DATE);
        return cassandraClient;
    }

    public static CassandraClient createDummyCassClient2() {
        CassandraClient cassandraClient = new CassandraClient();
        cassandraClient.setClientId(CLIENT_ID_2);
        cassandraClient.setApplicationId(APPLICATION_ID_2);
        cassandraClient.setCreatedTimestamp(CREATION_DATE);
        cassandraClient.setLastModifiedTimestamp(MODIFIED_DATE);
        return cassandraClient;
    }

    public static CassandraPolicyType createDummyCassPolicyType() {
        CassandraPolicyType cassandraPolicyType = new CassandraPolicyType();
        cassandraPolicyType.setPolicyTypeId(POLICY_ID);
        cassandraPolicyType.setName(NAME);
        cassandraPolicyType.setPolicyOrder(POLICY_ORDER);
        cassandraPolicyType.setServiceTypeIds(SERVICE_IDS);
        return cassandraPolicyType;
    }

    public static CassandraProtocolType createDummyCassProtocolType() {
        CassandraProtocolType cassandraProtocolType = new CassandraProtocolType();
        cassandraProtocolType.setProtocolTypeId(PROTOCOL_TYPE_ID);
        cassandraProtocolType.setDescription(DESCRIPTION);
        cassandraProtocolType.setServiceTypeIds(SERVICE_IDS);
        return cassandraProtocolType;
    }

    public static CassandraRole createDummyCassRole() {
        CassandraRole cassandraRole = new CassandraRole();
        cassandraRole.setRoleId(ROLE_ADMIN);
        cassandraRole.setDescription(DESCRIPTION);
        cassandraRole.setServiceTypeIds(SERVICE_IDS);
        cassandraRole.setApplicationIds(APPLICATION_IDS);
        return cassandraRole;
    }

    public static CassandraServiceType createDummyCassServiceType() {
        CassandraServiceType cassandraServiceType = new CassandraServiceType();
        cassandraServiceType.setServiceTypeId(SERVICE_ID);
        cassandraServiceType.setName(NAME);
        cassandraServiceType.setPolicyTypeIds(POLICY_IDS);
        cassandraServiceType.setProtocolTypeId(PROTOCOL_TYPE_ID);
        return cassandraServiceType;
    }


    public static Role createDummyRole() {
        return new  Role(ROLE_ADMIN,
                             DESCRIPTION,
                             SERVICE_IDS,
                             APPLICATION_IDS);
    }

    public static ServiceType createDummyServiceType() {
        return new ServiceType(SERVICE_ID,
                               DESCRIPTION,
                               POLICY_IDS,
                               PROTOCOL_TYPE_ID);

    }

    public static ProtocolType createDummyProtocolType() {
        return new ProtocolType(PROTOCOL_TYPE_ID,
                               DESCRIPTION,
                               SERVICE_IDS);

    }

    public static ProtocolType createDummyProtocolType2() {
        return new ProtocolType(PROTOCOL_TYPE_ID_2,
                               DESCRIPTION,
                               SERVICE_IDS);

    }

    public static PolicyType createDummyPolicyType() {
        return new PolicyType(POLICY_ID,
                                DESCRIPTION,
                                POLICY_ORDER,
                                SERVICE_IDS);

    }


    public static byte[] convertObjectToJsonBytes(final Object object) throws IOException {
        final ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return mapper.writeValueAsBytes(object);
    }

    public static String convertObjectToJsonString(final Object object) throws IOException {
        return new JsonConverter().jsonObjectMapper().writeValueAsString(object);
    }
}
