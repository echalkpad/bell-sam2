package com.vennetics.bell.sam.model.admin.service.pojo.util;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.vennetics.bell.sam.model.admin.service.pojo.Policy;

import java.util.Map;
import java.util.Set;

public final class AdminDataModelTestData {

    public static final String SERVICE_TYPE_ID1 = "serviceTypeId1";

    public static final String SERVICE_TYPE_ID2 = "serviceTypeId2";

    public static final Set<String> SERVICE_TYPE_IDS = ImmutableSet.of(SERVICE_TYPE_ID1,
                                                                       SERVICE_TYPE_ID2);

    public static final String SERVICE_POLICY_ID1 = "servicePolicyId";
    public static final String SERVICE_POLICY_ID2 = "servicePolicyId";

    public static final Set<String> SERVICE_POLICY_IDS = ImmutableSet.of(SERVICE_POLICY_ID1,
                                                                         SERVICE_POLICY_ID2);

    public static final String POLICY_NAME = "policyName";

    public static final Set<String> POLICY_VALUES = ImmutableSet.of("Value1", "Value2");

    public static final String POLICY_NAME2 = "policyName2";

    public static final Set<String> POLICY_VALUES2 = ImmutableSet.of("Value1", "Value2");

    public static final Map<String, Set<String>> POLICY_MAP1 = ImmutableMap.of(POLICY_NAME,
                                                                               POLICY_VALUES,
                                                                               POLICY_NAME2,
                                                                               POLICY_VALUES2);

    public static final Map<String, Set<String>> POLICY_MAP2 = ImmutableMap.of(POLICY_NAME,
                                                                              POLICY_VALUES,
                                                                              POLICY_NAME2,
                                                                              POLICY_VALUES2);

    public static final Policy SERVICE_POLICY1 = new Policy(SERVICE_POLICY_ID1,
                                                            POLICY_MAP1,
                                                            SERVICE_TYPE_ID1);

    public static final Policy SERVICE_POLICY2 = new Policy(SERVICE_POLICY_ID2,
                                                            POLICY_MAP1,
                                                            SERVICE_TYPE_ID2);

    // Same ID different values as 2
    public static final Policy SERVICE_POLICY3 = new Policy(SERVICE_POLICY_ID2,
                                                            POLICY_MAP2,
                                                            SERVICE_TYPE_ID2);

    public static final Policy APPLICATION_POLICY1 = new Policy("appPolicyId1", POLICY_MAP1, null);
    public static final Policy APPLICATION_POLICY2 = new Policy("appPolicyId2", POLICY_MAP1, null);


    public static final Set<Policy> APP_POLICY_SET = ImmutableSet.of(APPLICATION_POLICY1,
                                                                     APPLICATION_POLICY2);

    public static final Set<Policy> SERVICE_POLICY_SET = ImmutableSet.of(SERVICE_POLICY1,
                                                                         SERVICE_POLICY2,
                                                                         SERVICE_POLICY3);

    private AdminDataModelTestData() {

    }
}
