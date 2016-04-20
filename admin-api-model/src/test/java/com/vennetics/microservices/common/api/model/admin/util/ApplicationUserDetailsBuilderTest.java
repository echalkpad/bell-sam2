package com.vennetics.microservices.common.api.model.admin.util;

import com.vennetics.microservices.common.api.model.admin.v1.application.ApplicationAllowedService;
import com.vennetics.microservices.common.api.model.admin.v1.application.ApplicationUserDetails;
import org.junit.Test;

import java.util.Set;

import static com.vennetics.microservices.common.api.model.admin.util.AdminApiTestData.ACCOUNT_STATE;
import static com.vennetics.microservices.common.api.model.admin.util.AdminApiTestData.ALLOWED_SERVICE;
import static com.vennetics.microservices.common.api.model.admin.util.AdminApiTestData.ALLOWED_SERVICES;
import static com.vennetics.microservices.common.api.model.admin.util.AdminApiTestData.CLIENT_ID;
import static com.vennetics.microservices.common.api.model.admin.util.AdminApiTestData.POLICIES;
import static com.vennetics.microservices.common.api.model.admin.util.AdminApiTestData.ROLES;
import static com.vennetics.microservices.common.api.model.admin.util.AdminApiTestData.ROLE_ADMIN;
import static com.vennetics.microservices.common.api.model.admin.util.AdminApiTestData.ROLE_USER;
import static com.vennetics.microservices.common.api.model.admin.util.AdminApiTestData.SECRET;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class ApplicationUserDetailsBuilderTest {

    @Test
    public void shouldBuildObject() {

        final Set<ApplicationAllowedService> nullServices = null;

        final ApplicationUserDetailsBuilder builder = new ApplicationUserDetailsBuilder(CLIENT_ID,
                                                                                        SECRET,
                                                                                        ACCOUNT_STATE);

        assertThat(builder.build(),
                   equalTo(new ApplicationUserDetails(CLIENT_ID,
                                                      SECRET,
                                                      ACCOUNT_STATE,
                                                      null,
                                                      nullServices,
                                                      null)));

        builder.withRoles(ROLES);
        assertThat(builder.build(),
                   equalTo(new ApplicationUserDetails(CLIENT_ID,
                                                      SECRET,
                                                      ACCOUNT_STATE,
                                                      ROLES,
                                                      null,
                                                      null)));

        builder.withRoles(ROLE_ADMIN, ROLE_USER);
        assertThat(builder.build(),
                   equalTo(new ApplicationUserDetails(CLIENT_ID,
                                                      SECRET,
                                                      ACCOUNT_STATE,
                                                      ROLES,
                                                      null,
                                                      null)));

        builder.withServices(ALLOWED_SERVICES);
        assertThat(builder.build(),
                   equalTo(new ApplicationUserDetails(CLIENT_ID,
                                                      SECRET,
                                                      ACCOUNT_STATE,
                                                      ROLES,
                                                      ALLOWED_SERVICES,
                                                      null)));

        builder.withServices(ALLOWED_SERVICE);
        assertThat(builder.build(),
                   equalTo(new ApplicationUserDetails(CLIENT_ID,
                                                      SECRET,
                                                      ACCOUNT_STATE,
                                                      ROLES,
                                                      ALLOWED_SERVICES,
                                                      null)));

        builder.withPolicies(POLICIES);
        assertThat(builder.build(),
                   equalTo(new ApplicationUserDetails(CLIENT_ID,
                                                      SECRET,
                                                      ACCOUNT_STATE,
                                                      ROLES,
                                                      ALLOWED_SERVICES,
                                                      POLICIES)));
    }

}
