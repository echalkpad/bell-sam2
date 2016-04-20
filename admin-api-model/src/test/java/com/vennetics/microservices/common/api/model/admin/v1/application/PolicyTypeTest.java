package com.vennetics.microservices.common.api.model.admin.v1.application;

import com.vennetics.microservices.common.api.model.admin.TestUtils;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;

/**
 * PolicyType Test
 */
public class PolicyTypeTest {

    private static Validator validator;
    private static PolicyType testPolicyType;
    @BeforeClass
    public static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        testPolicyType = new PolicyType(TestUtils.TEST_POLICY_TYPE_ID,
                                        TestUtils.TEST_DESCRIPTION,
                                        TestUtils.TEST_POLICY_ORDER,
                                        TestUtils.TEST_SERVICE_TYPE_IDS);
    }

    @Test
    public void testPolicyTypeValidation() throws Exception {
        PolicyType policyType = new PolicyType(null, null, 0, null);

        Set<ConstraintViolation<PolicyType>> constraintViolations =
            validator.validate(policyType);

        assertEquals(3, constraintViolations.size());
        assertEquals("may not be empty", constraintViolations.iterator().next().getMessage());

        final Set<String> serviceTypeIds = new HashSet<>();
        policyType = new PolicyType("", "", 0, serviceTypeIds);
        constraintViolations =
            validator.validate(policyType);

        assertEquals(3, constraintViolations.size());
        assertEquals("may not be empty", constraintViolations.iterator().next().getMessage());

        serviceTypeIds.add("testString");
        policyType = new PolicyType("id", "name", null, serviceTypeIds);
        constraintViolations =
            validator.validate(policyType);
        assertEquals(1, constraintViolations.size());
        assertEquals("may not be null", constraintViolations.iterator().next().getMessage());

        constraintViolations =
            validator.validate(testPolicyType);

        assertEquals(0, constraintViolations.size());
    }

    @Test
    public void testGetPolicyTypeId() throws Exception {
        assertEquals(TestUtils.TEST_POLICY_TYPE_ID, testPolicyType.getPolicyTypeId());
    }

    @Test
    public void testGetDescription() throws Exception {
        assertEquals(TestUtils.TEST_DESCRIPTION, testPolicyType.getDescription());
    }

    @Test
    public void testGetPolicyOrder() throws Exception {
        assertEquals(TestUtils.TEST_POLICY_ORDER, testPolicyType.getPolicyOrder());
    }

    @Test
    public void testGetServiceTypeIds() throws Exception {
        assertEquals(TestUtils.TEST_SERVICE_TYPE_IDS, testPolicyType.getServiceTypeIds());
    }

    @Test
    public void testEqualsRules() {
        EqualsVerifier.forClass(PolicyType.class).suppress(Warning.NONFINAL_FIELDS).verify();
    }

    @Test
    public void testToString() throws Exception {
        assertEquals("PolicyType{policyTypeId='testPolicyId', "
                         + "description='testDescription', policyOrder=1, "
                         + "serviceTypeIds=[testServiceTypeId2, testServiceTypeId1]}",
                     testPolicyType.toString());
    }
}
