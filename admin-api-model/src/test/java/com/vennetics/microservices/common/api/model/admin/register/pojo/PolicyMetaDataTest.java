package com.vennetics.microservices.common.api.model.admin.register.pojo;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PolicyMetaDataTest {

    private static final String POLICY_DESCRIPTION = "policy_description";
    private static final String POLICY_NAME = "policyName";
    private static final String POLICY_KEY_NAME = "policy_key_name";
    private static final String POLICY_KEY_DESCRIPTION = "policy_key_description";
    private final Set<PolicyKeyMetaData> policyKeys = new HashSet<PolicyKeyMetaData>();
    private PolicyKeyMetaData policyKeyMetaData;

    @Before
    public void setUp() {
        policyKeyMetaData = new PolicyKeyMetaData(POLICY_KEY_NAME,
                                                  POLICY_KEY_DESCRIPTION);
        policyKeys.add(policyKeyMetaData);
    }

    @Test
    public void testContructor() {
        final PolicyMetaData policyMetaData = new PolicyMetaData(POLICY_NAME,
                                                                 POLICY_DESCRIPTION,
                                                                 policyKeys);

        assertEquals(POLICY_NAME, policyMetaData.getPolicyId());
        assertEquals(POLICY_DESCRIPTION, policyMetaData.getPolicyDescription());
        assertEquals(policyKeys, policyMetaData.getPolicyKeys());
    }

    @Test
    public void testGetPolicyMandatoryKeys() {
        final PolicyMetaData policyMetaData = new PolicyMetaData(POLICY_NAME,
                                                                 POLICY_DESCRIPTION,
                                                                 policyKeys);

        final Set<String> expectedKeys = new HashSet<>();
        expectedKeys.add(policyKeyMetaData.getKey());

        assertEquals(expectedKeys, policyMetaData.getPolicyMandatoryKeys());
    }

    @Test
    public void equalsTest() {
        EqualsVerifier.forClass(PolicyMetaData.class).verify();
    }

    @Test
    public void toStringTest() {
        final PolicyMetaData policyMetaData = new PolicyMetaData(POLICY_NAME,
                                                                 POLICY_DESCRIPTION,
                                                                 policyKeys);

        assertTrue(policyMetaData.toString().contains(POLICY_NAME));
        assertTrue(policyMetaData.toString().contains(POLICY_DESCRIPTION));
        assertTrue(policyMetaData.toString().contains(policyKeys.toString()));
    }

}
