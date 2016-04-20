package com.vennetics.bell.sam.model.admin.service.pojo;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

/**
 * Policy Test
 */
public class PolicyTest {


    public static final String POLICY_NAME = "policyName";

    public static final Set<String> POLICY_VALUES = new HashSet<>(Arrays.asList("Value1",
                                                                                "Value2"));

    public static final Map<String, Set<String>> POLICY_MAP = new HashMap<>();

    static {
        POLICY_MAP.put(POLICY_NAME, POLICY_VALUES);
    }

    private static final Policy POLICY = new Policy("policyId", POLICY_MAP, "serviceTypeId");

    @Test
    public void shouldSupportGetters() {
        assertThat("policyId", equalTo(POLICY.getPolicyId()));
        assertThat(POLICY_MAP, equalTo(POLICY.getValues()));
        assertThat("serviceTypeId", equalTo(POLICY.getServiceTypeId()));
    }


    @Test
    public void shouldSupportToString() {

        assertThat(POLICY.toString(), containsString("policyId=policyId"));
        assertThat(POLICY.toString(), containsString("serviceTypeId=serviceTypeId"));

        assertThat(POLICY.toString(), containsString("values="));
    }

    @Test
    public void testEqualsRules() {
        EqualsVerifier.forClass(Policy.class).verify();
    }
}
