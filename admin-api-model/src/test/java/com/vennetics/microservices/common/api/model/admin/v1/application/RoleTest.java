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
import java.util.UUID;

import static org.junit.Assert.assertEquals;

/**
 * Role Entity Test
 */
public class RoleTest {

    private static Validator validator;
    private static Role testRole;
    @BeforeClass
    public static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        testRole = new Role(TestUtils.TEST_ROLE_ID_1,
                                          TestUtils.TEST_DESCRIPTION,
                                          TestUtils.TEST_SERVICES,
                                          TestUtils.TEST_APPLICATIONS);
    }

    @Test
    public void testRoleValidation() throws Exception {
        Role role = new Role(null, null, null, null);

        Set<ConstraintViolation<Role>> constraintViolations =
            validator.validate(role);

        assertEquals(4, constraintViolations.size());
        assertEquals("may not be empty", constraintViolations.iterator().next().getMessage());

        final Set<String> services = new HashSet<>();
        final Set<UUID> applicationIds = new HashSet<>();
        role = new Role("", "", services, applicationIds);
        constraintViolations =
            validator.validate(role);

        assertEquals(4, constraintViolations.size());
        assertEquals("may not be empty", constraintViolations.iterator().next().getMessage());

        constraintViolations =
            validator.validate(testRole);

        assertEquals(0, constraintViolations.size());
    }

    @Test
    public void testGetRoleId() throws Exception {
        assertEquals(TestUtils.TEST_ROLE_ID_1, testRole.getRoleId());
    }

    @Test
    public void testGetDescription() throws Exception {
        assertEquals(TestUtils.TEST_DESCRIPTION, testRole.getDescription());
    }

    @Test
    public void testGetServiceTypeIds() throws Exception {
        assertEquals(TestUtils.TEST_SERVICES, testRole.getServiceTypeIds());
    }

    @Test
    public void testGetApplicationIds() throws Exception {
        assertEquals(TestUtils.TEST_APPLICATIONS, testRole.getApplicationIds());
    }

    @Test
    public void testEqualsRules() {
        EqualsVerifier.forClass(Role.class).suppress(Warning.NONFINAL_FIELDS).verify();
    }

    @Test
    public void testToString() throws Exception {
        assertEquals("Role{roleId='testRoleId1', description='testDescription', "
                         + "serviceTypeIds=[testServiceId1, testServiceId2], "
                         + "applicationIds=" + TestUtils.TEST_APPLICATIONS + "}",
                     testRole.toString());
    }
}
