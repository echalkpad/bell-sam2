package com.vennetics.microservices.common.api.model.admin.util;

import com.vennetics.microservices.common.api.model.admin.v1.application.ApplicationAllowedService;
import com.vennetics.microservices.common.api.model.admin.v1.application.ApplicationPolicy;
import com.vennetics.microservices.common.api.model.admin.v1.application.ApplicationUserDetails;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Builder object for creating an application user details representation.
 */
public final class ApplicationUserDetailsBuilder {

    private final String clientId;

    private final String secret;

    private final String state;

    private Set<String> roles;

    private Set<ApplicationAllowedService> allowedServices;

    private Set<ApplicationPolicy> applicationPolicies;

    public ApplicationUserDetailsBuilder(final String clientId,
                                         final String secret,
                                         final String state) {
        super();
        this.clientId = clientId;
        this.secret = secret;
        this.state = state;
    }

    public ApplicationUserDetails build() {
        return new ApplicationUserDetails(clientId,
                                          secret,
                                          state,
                                          roles,
                                          allowedServices,
                                          applicationPolicies);
    }

    public ApplicationUserDetailsBuilder withRoles(final Set<String> newRoles) {
        roles = newRoles;
        return this;
    }

    public ApplicationUserDetailsBuilder withRoles(final String... newRoles) {
        roles = new HashSet<>();
        roles.addAll(Arrays.asList(newRoles));
        return this;
    }

    public ApplicationUserDetailsBuilder withServices(final Set<ApplicationAllowedService> newAllowedServices) {
        allowedServices = newAllowedServices;
        return this;
    }

    public ApplicationUserDetailsBuilder withServices(final ApplicationAllowedService... newAllowedServices) {
        allowedServices = new HashSet<>();
        allowedServices.addAll(Arrays.asList(newAllowedServices));
        return this;
    }

    public ApplicationUserDetailsBuilder withPolicies(final Set<ApplicationPolicy> newApplicationPolicies) {
        applicationPolicies = newApplicationPolicies;
        return this;
    }

}
