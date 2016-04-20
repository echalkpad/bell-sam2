package com.vennetics.microservices.common.api.model.admin.register.pojo;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PolicyKeyMetaDataTest {

    private static final String POLICY_DESCRIPTION = "policy_description";
    private static final String POLICY_KEY = "policy_key";

    @Test
    public void testConstructor() {
        final PolicyKeyMetaData policyKeyMetaData = new PolicyKeyMetaData(POLICY_KEY,
                                                                          POLICY_DESCRIPTION);
        assertEquals(POLICY_KEY, policyKeyMetaData.getKey());
        assertEquals(POLICY_DESCRIPTION, policyKeyMetaData.getDescription());
    }

    @Test
    public void testEquals() {
        EqualsVerifier.forClass(PolicyKeyMetaData.class).verify();
    }

    @Test
    public void testToString() {
        final PolicyKeyMetaData policyKeyMetaData = new PolicyKeyMetaData(POLICY_KEY,
                                                                          POLICY_DESCRIPTION);

        assertTrue(policyKeyMetaData.toString().contains(POLICY_KEY));
        assertTrue(policyKeyMetaData.toString().contains(POLICY_DESCRIPTION));
    }

}
