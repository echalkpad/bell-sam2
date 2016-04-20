package com.vennetics.microservices.common.api.model.admin.v1.application;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Set;
import java.util.UUID;

/**
 * Access control POJO providing the user details of an application.
 */
@XmlRootElement(name = "applicationUserDetails")
public final class ApplicationUserDetails {

    @NotBlank
    @XmlElement(name = "clientId")
    private String clientId;

    @NotBlank
    @XmlElement(name = "secret")
    private String secret;

    @NotBlank
    @XmlElement(name = "state")
    private String state;

    @NotBlank
    @XmlElement(name = "name")
    private String name;

    @NotBlank
    @Email
    @XmlElement(name = "email")
    private String email;

    @XmlElement(name = "roles")
    private Set<String> roles;

    @XmlElement(name = "allowedServices")
    private Set<ApplicationAllowedService> allowedServices;

    @XmlElement(name = "applicationPolicies")
    private Set<ApplicationPolicy> applicationPolicies;

    @XmlElement(name = "applicationId")
    private UUID applicationId;


    protected ApplicationUserDetails() {
        // For JSON deserialisation
    }

    // Suppress more than 7 constructor parameters
    @SuppressWarnings("squid:S00107")
    public ApplicationUserDetails(final String clientId,
                                  final String secret,
                                  final String state,
                                  final String name,
                                  final String email,
                                  final Set<String> roles,
                                  final Set<ApplicationAllowedService> allowedServices,
                                  final Set<ApplicationPolicy> applicationPolicies,
                                  final UUID applicationId) {
        this.clientId = clientId;
        this.secret = secret;
        this.state = state;
        this.name = name;
        this.email = email;
        this.roles = roles;
        this.allowedServices = allowedServices;
        this.applicationPolicies = applicationPolicies;
        this.applicationId = applicationId;
    }

    public ApplicationUserDetails(final String clientId,
                                  final String secret,
                                  final String state,
                                  final Set<String> roles,
                                  final Set<ApplicationAllowedService> allowedServices,
                                  final Set<ApplicationPolicy> applicationPolicies) {
        super();
        this.clientId = clientId;
        this.secret = secret;
        this.state = state;
        this.roles = roles;
        this.allowedServices = allowedServices;
        this.applicationPolicies = applicationPolicies;
    }


    public ApplicationUserDetails(final String clientId,
                                  final String name,
                                  final String email,
                                  final String state,
                                  final UUID applicationId) {
        super();
        this.clientId = clientId;
        this.name = name;
        this.email = email;
        this.state = state;
        this.applicationId = applicationId;
    }

    public ApplicationUserDetails(final String clientId,
                                  final String name,
                                  final String email,
                                  final String secret,
                                  final String state,
                                  final UUID applicationId) {
        super();
        this.clientId = clientId;
        this.name = name;
        this.email = email;
        this.secret = secret;
        this.state = state;
        this.applicationId = applicationId;
    }

    // Suppress more than 7 constructor parameters
    @SuppressWarnings("squid:S00107")
    public ApplicationUserDetails(final String clientId,
                                  final String name,
                                  final String email,
                                  final String state,
                                  final UUID applicationId,
                                  final Set<String> roles,
                                  final Set<ApplicationPolicy> applicationPolicies,
                                  final Set<ApplicationAllowedService> allowedServices) {
        super();
        this.clientId = clientId;
        this.name = name;
        this.email = email;
        this.state = state;
        this.applicationId = applicationId;
        this.roles = roles;
        this.applicationPolicies = applicationPolicies;
        this.allowedServices = allowedServices;
    }

    public String getClientId() {
        return clientId;
    }


    public String getSecret() {
        return secret;
    }

    public String getState() {
        return state;
    }

    public Set<String> getRoles() {
        return roles;
    }


    public Set<ApplicationAllowedService> getAllowedServices() {
        return allowedServices;
    }


    public Set<ApplicationPolicy> getApplicationPolicies() {
        return applicationPolicies;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public UUID getApplicationId() {
        return applicationId;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (allowedServices == null ? 0 : allowedServices.hashCode());
        result = prime * result
                        + (applicationPolicies == null ? 0 : applicationPolicies.hashCode());
        result = prime * result + (clientId == null ? 0 : clientId.hashCode());
        result = prime * result + (roles == null ? 0 : roles.hashCode());
        result = prime * result + (secret == null ? 0 : secret.hashCode());
        result = prime * result + (state == null ? 0 : state.hashCode());
        result = prime * result + (name == null ? 0 : name.hashCode());
        result = prime * result + (email == null ? 0 : email.hashCode());
        return result;
    }


    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ApplicationUserDetails other = (ApplicationUserDetails) obj;
        if (allowedServices == null) {
            if (other.allowedServices != null) {
                return false;
            }
        } else if (!allowedServices.equals(other.allowedServices)) {
            return false;
        }
        if (applicationPolicies == null) {
            if (other.applicationPolicies != null) {
                return false;
            }
        } else if (!applicationPolicies.equals(other.applicationPolicies)) {
            return false;
        }
        if (clientId == null) {
            if (other.clientId != null) {
                return false;
            }
        } else if (!clientId.equals(other.clientId)) {
            return false;
        }
        if (roles == null) {
            if (other.roles != null) {
                return false;
            }
        } else if (!roles.equals(other.roles)) {
            return false;
        }
        if (secret == null) {
            if (other.secret != null) {
                return false;
            }
        } else if (!secret.equals(other.secret)) {
            return false;
        }
        if (state == null) {
            if (other.state != null) {
                return false;
            }
        } else if (!state.equals(other.state)) {
            return false;
        }
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        if (email == null) {
            if (other.email != null) {
                return false;
            }
        } else if (!email.equals(other.email)) {
            return false;
        }
        return true;
    }


    @Override
    public String toString() {
        return "ApplicationUserDetails [clientId=" + clientId + ", secret=*****, state=" + state
                        + ", name=" + name + ", email=" + email + ", applicationId=" + applicationId
                        + ", roles=" + roles + ", allowedServices=" + allowedServices
                        + ", applicationPolicies=" + applicationPolicies + "]";
    }


}
