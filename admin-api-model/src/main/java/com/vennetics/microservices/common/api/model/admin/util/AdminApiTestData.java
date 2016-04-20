package com.vennetics.microservices.common.api.model.admin.util;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.vennetics.microservices.common.api.model.admin.v1.application.ApplicationAllowedService;
import com.vennetics.microservices.common.api.model.admin.v1.application.ApplicationPolicy;
import com.vennetics.microservices.common.api.model.admin.v1.application.ApplicationUserDetails;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Reusable unit test data
 */
public final class AdminApiTestData {

    public static final String ACCOUNT_STATE = "ACTIVE";
    public static final String SECRET = "secretPassword";
    public static final String CLIENT_ID = "clientId";
    public static final String SERVICE_TYPE_ID = "serviceTypeId";
    public static final String POLICY_ID = "policyId";
    public static final String POLICY_NAME = "policyName";
    public static final String NAME = "name";
    public static final String EMAIL = "email@test.com";
    public static final UUID APPLICATION_ID = UUID.randomUUID();

    public static final Set<String> POLICY_VALUES = ImmutableSet.of("Value1", "Value2");
    public static final String ROLE_ADMIN = "ROLE_ADMIN";
    public static final String ROLE_USER = "ROLE_USER";

    public static final Set<String> ROLES = ImmutableSet.of(ROLE_ADMIN, ROLE_USER);

    public static final Map<String, Set<String>> POLICY_MAP = ImmutableMap.of(POLICY_NAME,
                                                                              POLICY_VALUES);

    public static final ApplicationPolicy APPLICATION_POLICY = new ApplicationPolicy(POLICY_ID,
                                                                                     POLICY_MAP);

    public static final Set<ApplicationPolicy> POLICIES = ImmutableSet.of(APPLICATION_POLICY);

    public static final ApplicationAllowedService ALLOWED_SERVICE = new ApplicationAllowedService(SERVICE_TYPE_ID,
                                                                                                  POLICIES);

    public static final Set<ApplicationAllowedService> ALLOWED_SERVICES = ImmutableSet.of(ALLOWED_SERVICE);

    public static final ApplicationUserDetails APPLICATION_USER_DETAILS = new ApplicationUserDetails(CLIENT_ID,
                                                                                                     SECRET,
                                                                                                     ACCOUNT_STATE,
                                                                                                     ROLES,
                                                                                                     ALLOWED_SERVICES,
                                                                                                     POLICIES);

    public static final ApplicationUserDetails FULL_APPLICATION_USER_DETAILS =
        new ApplicationUserDetails(CLIENT_ID,
                                   SECRET,
                                   ACCOUNT_STATE,
                                   NAME,
                                   EMAIL,
                                   ROLES,
                                   ALLOWED_SERVICES,
                                   POLICIES,
                                   APPLICATION_ID);


    private AdminApiTestData() {

    }
}
