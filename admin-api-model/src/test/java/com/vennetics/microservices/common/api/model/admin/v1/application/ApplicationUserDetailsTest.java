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
import java.util.Set;
import java.util.UUID;

import static com.vennetics.microservices.common.api.model.admin.util.AdminApiTestData.ACCOUNT_STATE;
import static com.vennetics.microservices.common.api.model.admin.util.AdminApiTestData.ALLOWED_SERVICES;
import static com.vennetics.microservices.common.api.model.admin.util.AdminApiTestData.APPLICATION_ID;
import static com.vennetics.microservices.common.api.model.admin.util.AdminApiTestData.CLIENT_ID;
import static com.vennetics.microservices.common.api.model.admin.util.AdminApiTestData.EMAIL;
import static com.vennetics.microservices.common.api.model.admin.util.AdminApiTestData.FULL_APPLICATION_USER_DETAILS;
import static com.vennetics.microservices.common.api.model.admin.util.AdminApiTestData.NAME;
import static com.vennetics.microservices.common.api.model.admin.util.AdminApiTestData.ROLE_ADMIN;
import static com.vennetics.microservices.common.api.model.admin.util.AdminApiTestData.ROLE_USER;
import static com.vennetics.microservices.common.api.model.admin.util.AdminApiTestData.SECRET;
import static com.vennetics.microservices.common.api.model.admin.util.AdminApiTestData.SERVICE_TYPE_ID;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class ApplicationUserDetailsTest {

    private static Validator validator;
    @Before
    public void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void shouldReturnNonSecretFieldsInToString() {

        assertThat(FULL_APPLICATION_USER_DETAILS.toString(), containsString("clientId=" + CLIENT_ID));
        assertThat(FULL_APPLICATION_USER_DETAILS.toString(), containsString(ROLE_USER));
        assertThat(FULL_APPLICATION_USER_DETAILS.toString(), containsString(SERVICE_TYPE_ID));
        assertThat(FULL_APPLICATION_USER_DETAILS.toString(), containsString(NAME));
        assertThat(FULL_APPLICATION_USER_DETAILS.toString(), containsString(EMAIL));
        assertThat(FULL_APPLICATION_USER_DETAILS.toString(), containsString(APPLICATION_ID.toString()));

        assertThat(FULL_APPLICATION_USER_DETAILS.toString(), not(containsString(SECRET)));

    }

    @Test
    public void shouldSupportEqualsHashCode() {
        EqualsVerifier.forClass(ApplicationUserDetails.class)
                      .suppress(Warning.NONFINAL_FIELDS)
                      .verify();
    }

    @Test
    public void shouldSupportGetters() {

        assertThat(FULL_APPLICATION_USER_DETAILS.getClientId(), equalTo(CLIENT_ID));
        assertThat(FULL_APPLICATION_USER_DETAILS.getSecret(), equalTo(SECRET));
        assertThat(FULL_APPLICATION_USER_DETAILS.getState(), equalTo(ACCOUNT_STATE));
        assertThat(FULL_APPLICATION_USER_DETAILS.getRoles(), containsInAnyOrder(ROLE_USER, ROLE_ADMIN));
        assertThat(FULL_APPLICATION_USER_DETAILS.getAllowedServices(), equalTo(ALLOWED_SERVICES));
        assertThat(FULL_APPLICATION_USER_DETAILS.getName(), equalTo(NAME));
        assertThat(FULL_APPLICATION_USER_DETAILS.getEmail(), equalTo(EMAIL));
        assertThat(FULL_APPLICATION_USER_DETAILS.getApplicationId(), equalTo(APPLICATION_ID));

    }

    @Test
    public void shouldBeTwoWaySerialisable() throws Exception {

        final String json = new JsonConverter().jsonObjectMapper()
                                               .writeValueAsString(FULL_APPLICATION_USER_DETAILS);

        final ApplicationUserDetails result = new JsonConverter().jsonObjectMapper()
                                                                 .readValue(json,
                                                                            ApplicationUserDetails.class);

        assertEquals(FULL_APPLICATION_USER_DETAILS, result);
    }

    @Test
    public void testApplicationValidation() throws Exception {
        UUID id = null;
        ApplicationUserDetails appCore = new ApplicationUserDetails(null, null, null, null, null, id);

        Set<ConstraintViolation<ApplicationUserDetails>> constraintViolations =
            validator.validate(appCore);

        assertEquals(5, constraintViolations.size());
        assertEquals("may not be empty", constraintViolations.iterator().next().getMessage());

        appCore = new ApplicationUserDetails("", "", "", "", id);
        constraintViolations =
            validator.validate(appCore);

        assertEquals(5, constraintViolations.size());
        assertEquals("may not be empty", constraintViolations.iterator().next().getMessage());
        id = UUID.randomUUID();
        appCore = new ApplicationUserDetails("id", "name", "invalidEmail", "secret", "state", id);
        constraintViolations =
            validator.validate(appCore);

        assertEquals(1, constraintViolations.size());
        assertEquals("not a well-formed email address", constraintViolations.iterator().next().getMessage());
    }

}
