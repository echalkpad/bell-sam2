package com.vennetics.microservices.common.api.model.admin.v1.application;

import com.vennetics.bell.sam.rest.config.converters.JsonConverter;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.Test;

import static com.vennetics.microservices.common.api.model.admin.util.AdminApiTestData.ALLOWED_SERVICE;
import static com.vennetics.microservices.common.api.model.admin.util.AdminApiTestData.POLICIES;
import static com.vennetics.microservices.common.api.model.admin.util.AdminApiTestData.POLICY_ID;
import static com.vennetics.microservices.common.api.model.admin.util.AdminApiTestData.SERVICE_TYPE_ID;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class ApplicationAllowedServiceTest {

    @Test
    public void shouldImplementHashcodeEquals() {

        EqualsVerifier.forClass(ApplicationAllowedService.class)
                      .suppress(Warning.NONFINAL_FIELDS)
                      .verify();
    }

    @Test
    public void shouldSupportToString() {

        assertThat(ALLOWED_SERVICE.toString(), containsString(SERVICE_TYPE_ID));
        assertThat(ALLOWED_SERVICE.toString(), containsString(POLICY_ID));
        assertThat(ALLOWED_SERVICE.toString(), containsString("Value1"));
        assertThat(ALLOWED_SERVICE.toString(), containsString("Value2"));
    }

    @Test
    public void shouldSupportGetters() {

        assertThat(ALLOWED_SERVICE.getPolicies(), equalTo(POLICIES));
        assertThat(ALLOWED_SERVICE.getServiceTypeId(), equalTo(SERVICE_TYPE_ID));
    }

    @Test
    public void shouldBeTwoWaySerialisable() throws Exception {
        final String json = new JsonConverter().jsonObjectMapper()
                                               .writeValueAsString(ALLOWED_SERVICE);


        final ApplicationAllowedService policy = new JsonConverter().jsonObjectMapper()
                                                                    .readValue(json,
                                                                               ApplicationAllowedService.class);

        assertEquals(ALLOWED_SERVICE, policy);
    }
}
