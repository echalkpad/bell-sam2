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
 * ServiceType entity test
 */
public class ServiceTypeTest {

    private static Validator validator;
    private static ServiceType testServiceType;
    @BeforeClass
    public static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        testServiceType = new ServiceType(TestUtils.TEST_SERVICE_TYPE_ID_1,
                                      TestUtils.TEST_DESCRIPTION,
                                      TestUtils.TEST_POLICIES,
                                      TestUtils.TEST_PROTOCOL_TYPE_ID);
    }

    @Test
    public void testServiceTypeValidation() throws Exception {
        ServiceType serviceType = new ServiceType(null, null, null, null);

        Set<ConstraintViolation<ServiceType>> constraintViolations =
            validator.validate(serviceType);

        assertEquals(4, constraintViolations.size());
        assertEquals("may not be empty", constraintViolations.iterator().next().getMessage());

        final Set<String> policies = new HashSet<>();
        serviceType = new ServiceType("", "", policies, "");
        constraintViolations =
            validator.validate(serviceType);

        assertEquals(4, constraintViolations.size());
        assertEquals("may not be empty", constraintViolations.iterator().next().getMessage());

        constraintViolations =
            validator.validate(testServiceType);

        assertEquals(0, constraintViolations.size());
    }

    @Test
    public void testGetServiceTypeId() throws Exception {
        assertEquals(TestUtils.TEST_SERVICE_TYPE_ID_1, testServiceType.getServiceTypeId());
    }

    @Test
    public void testGetDescription() throws Exception {
        assertEquals(TestUtils.TEST_DESCRIPTION, testServiceType.getDescription());
    }

    @Test
    public void testGetPolicyTypeIds() throws Exception {
        assertEquals(TestUtils.TEST_POLICIES, testServiceType.getPolicyTypeIds());
    }

    @Test
    public void testGetProtocolTypeId() throws Exception {
        assertEquals(TestUtils.TEST_PROTOCOL_TYPE_ID, testServiceType.getProtocolTypeId());
    }

    @Test
    public void testEqualsRules() {
        EqualsVerifier.forClass(ServiceType.class).suppress(Warning.NONFINAL_FIELDS).verify();
    }

    @Test
    public void testToString() throws Exception {
        assertEquals("ServiceType{serviceTypeId='testServiceId1', description='testDescription', "
                         + "policyTypeIds=[testPolicyId1, testPolicyId2], protocolTypeId='testProtocolId'}",
                     testServiceType.toString());
    }
}
