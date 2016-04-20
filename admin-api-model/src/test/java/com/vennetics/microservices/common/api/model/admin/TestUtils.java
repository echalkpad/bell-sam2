package com.vennetics.microservices.common.api.model.admin;

import com.vennetics.microservices.common.api.model.admin.v1.application.ApplicationAllowedService;
import com.vennetics.microservices.common.api.model.admin.v1.application.ApplicationPolicy;
import com.vennetics.microservices.common.api.model.admin.v1.application.ApplicationUserDetails;
import com.vennetics.microservices.common.api.model.admin.v1.application.PolicyType;
import com.vennetics.microservices.common.api.model.admin.v1.application.ProtocolType;
import com.vennetics.microservices.common.api.model.admin.v1.application.Role;
import com.vennetics.microservices.common.api.model.admin.v1.application.ServiceType;

import java.util.Arrays;
import java.util.Collections;
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

    public static final Set<ApplicationPolicy> TEST_APPLICATION_POLICIES =
        Collections.unmodifiableSet(new HashSet<ApplicationPolicy>(Arrays.asList(
            new ApplicationPolicy("testPolicyId1", null),
            new ApplicationPolicy("testPolicyId2", null))));
    public static final Set<ApplicationPolicy> TEST_GW_APPLICATION_POLICIES =
        Collections.unmodifiableSet(new HashSet<ApplicationPolicy>(Arrays.asList(
            new ApplicationPolicy("testGwPolicyId1", null),
            new ApplicationPolicy("testGwPolicyId2", null))));
    public static final Set<ApplicationAllowedService> TEST_APPLICATION_ALLOWED_SERVICES =
        Collections.unmodifiableSet(new HashSet<ApplicationAllowedService>(Arrays.asList(
            new ApplicationAllowedService("testServiceId1", null),
            new ApplicationAllowedService("testServiceId1", null))));


    public static final Set<UUID> TEST_APPLICATIONS =  Collections.unmodifiableSet(new HashSet<UUID>(
                    Arrays.asList(UUID.randomUUID(), UUID.randomUUID())));
    public static final Set<String> TEST_SERVICE_TYPE_IDS =
        Collections.unmodifiableSet(new HashSet<String>(Arrays.asList("testServiceTypeId1", "testServiceTypeId2")));

    public static ApplicationUserDetails createDummyApplicationFull() {
        return new ApplicationUserDetails(TestUtils.TEST_CLIENT_ID_1,
                                          TestUtils.TEST_NAME,
                                          TestUtils.TEST_EMAIL,
                                          TestUtils.TEST_ACCOUNT_STATE,
                                          TestUtils.TEST_APPLICATION_ID_1,
                                          TestUtils.TEST_ROLES,
                                          TestUtils.TEST_GW_APPLICATION_POLICIES,
                                          TestUtils.TEST_APPLICATION_ALLOWED_SERVICES);
    }

    public static Role createDummyRole() {
        return new  Role(TestUtils.TEST_ROLE_ID_1,
                         TestUtils.TEST_DESCRIPTION,
                         TestUtils.TEST_SERVICES,
                         TestUtils.TEST_APPLICATIONS);
    }

    public static ServiceType createDummyServiceType() {
        return new ServiceType(TestUtils.TEST_SERVICE_TYPE_ID_1,
                               TestUtils.TEST_NAME,
                               TestUtils.TEST_POLICIES,
                               TestUtils.TEST_PROTOCOL_TYPE_ID);

    }

    public static PolicyType createDummyPolicyType() {
        return new PolicyType(TestUtils.TEST_POLICY_TYPE_ID,
                              TestUtils.TEST_DESCRIPTION,
                              TestUtils.TEST_POLICY_ORDER,
                              TestUtils.TEST_SERVICE_TYPE_IDS);

    }

    public static ProtocolType createDummyProtocolType() {
        return new ProtocolType(TestUtils.TEST_POLICY_TYPE_ID,
                              TestUtils.TEST_DESCRIPTION,
                              TestUtils.TEST_SERVICE_TYPE_IDS);

    }
}
