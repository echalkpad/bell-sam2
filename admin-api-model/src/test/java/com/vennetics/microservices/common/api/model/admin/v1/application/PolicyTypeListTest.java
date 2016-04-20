package com.vennetics.microservices.common.api.model.admin.v1.application;

import com.vennetics.microservices.common.api.model.admin.TestUtils;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

/**
 * PolicyTypeList Test
 */
public class PolicyTypeListTest {

    private PolicyTypeList policyTypeList;

    @Before
    public void setUp() {

        final PolicyType policyType1 = TestUtils.createDummyPolicyType();
        final PolicyType policyType2 = TestUtils.createDummyPolicyType();
        final ArrayList<PolicyType> policyTypes = new ArrayList<>();
        policyTypes.add(policyType1);
        policyTypes.add(policyType2);
        policyTypeList = new PolicyTypeList(policyTypes);
    }

    @Test
    public void testGetPolicyTypes() throws Exception {
        assertEquals(2, policyTypeList.getPolicyTypes().size());
    }

    @Test
    public void testToString() throws Exception {
        assertEquals("PolicyTypeList{policyTypes=[PolicyType{policyTypeId='testPolicyId', "
                         + "description='testDescription', policyOrder=1, "
                         + "serviceTypeIds=[testServiceTypeId2, testServiceTypeId1]}, "
                         + "PolicyType{policyTypeId='testPolicyId', description='testDescription', "
                         + "policyOrder=1, "
                         + "serviceTypeIds=[testServiceTypeId2, testServiceTypeId1]}]}",
                     policyTypeList.toString());
    }

    @Test
    public void testEqualsRules() {
        EqualsVerifier.forClass(PolicyTypeList.class).suppress(Warning.NONFINAL_FIELDS).verify();
    }
}
