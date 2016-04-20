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
 * ProtocolType test
 */
public class ProtocolTypeTest {

    private static Validator validator;
    private static ProtocolType testProtocolType;
    @BeforeClass
    public static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        testProtocolType = new ProtocolType(TestUtils.TEST_POLICY_TYPE_ID,
                                            TestUtils.TEST_DESCRIPTION,
                                            TestUtils.TEST_SERVICE_TYPE_IDS);
    }

    @Test
    public void testProtocolTypeValidation() throws Exception {
        ProtocolType protocolType = new ProtocolType(null, null, null);

        Set<ConstraintViolation<ProtocolType>> constraintViolations =
            validator.validate(protocolType);

        assertEquals(3, constraintViolations.size());
        assertEquals("may not be empty", constraintViolations.iterator().next().getMessage());

        final Set<String> serviceTypeIds = new HashSet<>();
        protocolType = new ProtocolType("", "", serviceTypeIds);
        constraintViolations =
            validator.validate(protocolType);

        assertEquals(3, constraintViolations.size());
        assertEquals("may not be empty", constraintViolations.iterator().next().getMessage());

        constraintViolations =
            validator.validate(testProtocolType);

        assertEquals(0, constraintViolations.size());
    }

    @Test
    public void testGetProtocolTypeId() throws Exception {
        assertEquals(TestUtils.TEST_POLICY_TYPE_ID, testProtocolType.getProtocolTypeId());
    }

    @Test
    public void testGetDescription() throws Exception {
        assertEquals(TestUtils.TEST_DESCRIPTION, testProtocolType.getDescription());
    }

    @Test
    public void testGetServiceTypeIds() throws Exception {
        assertEquals(TestUtils.TEST_SERVICE_TYPE_IDS, testProtocolType.getServiceTypeIds());
    }

    @Test
    public void testToString() throws Exception {
        assertEquals("ProtocolType{protocolTypeId='testPolicyId', "
                         + "description='testDescription', "
                         + "serviceTypeIds=[testServiceTypeId2, testServiceTypeId1]}",
                     testProtocolType.toString());
    }

    @Test
    public void testEqualsRules() {
        EqualsVerifier.forClass(ProtocolType.class).suppress(Warning.NONFINAL_FIELDS).verify();
    }
}
