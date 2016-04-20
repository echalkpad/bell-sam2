package com.vennetics.bell.sam.model.admin.service;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Test Util Class
 */
public final class TestUtils {

    private TestUtils() {
        // Checkstyle prevent instantiation of util class
    }

    // IDs
    public static final UUID TEST_APPLICATION_ID_1 = UUID.randomUUID();
    public static final String TEST_CLIENT_ID_1 = "testClientId1";
    public static final String TEST_SERVICE_TYPE_ID_1 = "testServiceId1";
    public static final String TEST_POLICY_TYPE_ID = "testPolicyId";
    public static final String TEST_PROTOCOL_TYPE_ID = "testProtocolId";
    public static final String TEST_ROLE_ID_1 = "testRoleId1";
    public static final String TEST_SERVICE_ID = "testServiceId";

    public static final String TEST_NAME = "testName";
    public static final String TEST_EMAIL = "testEmail@test.com";
    public static final String TEST_SECRET = "testSecret";
    public static final Integer TEST_POLICY_ORDER = 1;
    public static final String TEST_ACCOUNT_STATE = "ACTIVE";
    public static final String TEST_DESCRIPTION = "testDescription";
    public static final Date TEST_CREATION_DATE = new Date();
    public static final Date TEST_MODIFIED_DATE = new Date();

    // Sets
    public static final Set<String> TEST_ROLES =  Collections.unmodifiableSet(new HashSet<String>(
                    Arrays.asList("testRoleId1",
                                  "testRole2Id")));
    public static final Set<String> TEST_POLICIES =  Collections.unmodifiableSet(new HashSet<String>(Arrays.asList(
                    "testPolicyId1",
                    "testPolicyId2")));
    public static final Set<String> TEST_GW_POLICIES =  Collections.unmodifiableSet(new HashSet<String>(Arrays.asList(
                    "testGwPolicyId1",
                    "testGwPolicyId2")));
    public static final Set<String> TEST_SERVICES =  Collections.unmodifiableSet(new HashSet<String>(Arrays.asList(
                    "testServiceId1",
                    "testServiceId2")));
    public static final Set<UUID> TEST_APPLICATIONS =  Collections.unmodifiableSet(new HashSet<UUID>(Arrays.asList(UUID.randomUUID(),
                    UUID.randomUUID())));
}
