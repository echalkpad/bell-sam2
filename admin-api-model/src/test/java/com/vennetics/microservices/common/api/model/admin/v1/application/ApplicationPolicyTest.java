package com.vennetics.microservices.common.api.model.admin.v1.application;

import com.vennetics.bell.sam.rest.config.converters.JsonConverter;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.Before;
import org.junit.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.vennetics.microservices.common.api.model.admin.util.AdminApiTestData.APPLICATION_POLICY;
import static com.vennetics.microservices.common.api.model.admin.util.AdminApiTestData.POLICY_ID;
import static com.vennetics.microservices.common.api.model.admin.util.AdminApiTestData.POLICY_MAP;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class ApplicationPolicyTest {

    private static Validator validator;
    @Before
    public void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void shouldImplementHashcodeEquals() {

        EqualsVerifier.forClass(ApplicationPolicy.class).suppress(Warning.NONFINAL_FIELDS).verify();
    }

    @Test
    public void shouldSupportToString() {

        assertThat(APPLICATION_POLICY.toString(), containsString(POLICY_ID));
        assertThat(APPLICATION_POLICY.toString(), containsString("Value1"));
        assertThat(APPLICATION_POLICY.toString(), containsString("Value2"));
    }

    @Test
    public void shouldSupportGetters() {

        assertThat(APPLICATION_POLICY.getPolicyId(), equalTo(POLICY_ID));
        assertThat(APPLICATION_POLICY.getValues(), equalTo(POLICY_MAP));
    }

    @Test
    public void shouldBeTwoWaySerialisable() throws Exception {
        final String json = new JsonConverter().jsonObjectMapper()
                                               .writeValueAsString(APPLICATION_POLICY);

        final ApplicationPolicy policy = new JsonConverter().jsonObjectMapper()
                                                            .readValue(json,
                                                                       ApplicationPolicy.class);

        assertEquals(APPLICATION_POLICY, policy);
    }

    @Test
    public void testPolicyValidation() throws Exception {
        ApplicationPolicy policyVal = new ApplicationPolicy(null, null);

        Set<ConstraintViolation<ApplicationPolicy>> constraintViolations =
            validator.validate(policyVal);

        assertEquals(2, constraintViolations.size());
        assertEquals("may not be empty", constraintViolations.iterator().next().getMessage());

        Map<String, Set<String>> map = new HashMap<>();

        policyVal = new ApplicationPolicy("", map);
        constraintViolations =
            validator.validate(policyVal);

        assertEquals(2, constraintViolations.size());
        assertEquals("may not be empty", constraintViolations.iterator().next().getMessage());

        final Set<String> values = new HashSet<>();
        values.add("someValues");
        map.put("", values);
        policyVal = new ApplicationPolicy("id", map);
        constraintViolations =
            validator.validate(policyVal);

        assertEquals(0, constraintViolations.size());
    }

}
