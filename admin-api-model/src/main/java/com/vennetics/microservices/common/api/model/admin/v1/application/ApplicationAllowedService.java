package com.vennetics.microservices.common.api.model.admin.v1.application;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Set;

@XmlRootElement(name = "allowedService")
public final class ApplicationAllowedService {

    @XmlElement(name = "serviceTypeId")
    private String serviceTypeId;

    @XmlElement(name = "policies")
    private Set<ApplicationPolicy> policies;

    protected ApplicationAllowedService() {
        // For JSON
    }

    public ApplicationAllowedService(final String serviceTypeId,
                                     final Set<ApplicationPolicy> policies) {
        super();
        this.serviceTypeId = serviceTypeId;
        this.policies = policies;
    }

    public String getServiceTypeId() {
        return serviceTypeId;
    }

    public Set<ApplicationPolicy> getPolicies() {
        return policies;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (policies == null ? 0 : policies.hashCode());
        result = prime * result + (serviceTypeId == null ? 0 : serviceTypeId.hashCode());
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
        final ApplicationAllowedService other = (ApplicationAllowedService) obj;
        if (policies == null) {
            if (other.policies != null) {
                return false;
            }
        } else if (!policies.equals(other.policies)) {
            return false;
        }
        if (serviceTypeId == null) {
            if (other.serviceTypeId != null) {
                return false;
            }
        } else if (!serviceTypeId.equals(other.serviceTypeId)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ApplicationAllowedService [serviceTypeId=" + serviceTypeId + ", policies="
                        + policies + "]";
    }

}
