package com.vennetics.microservices.common.api.model.admin.register.pojo;

import java.util.Set;

public final class ServiceMetaData {

    private final String serviceId;

    private final String serviceDescription;

    private final Set<PolicyMetaData> policies;

    public ServiceMetaData(final String serviceName,
                           final String serviceDescription,
                           final Set<PolicyMetaData> policies) {
        this.serviceId = serviceName;
        this.policies = policies;
        this.serviceDescription = serviceDescription;
    }

    public String getServiceId() {
        return serviceId;
    }

    public String getServiceDescription() {
        return serviceDescription;
    }

    public Set<PolicyMetaData> getPolicies() {
        return policies;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((policies == null) ? 0 : policies.hashCode());
        result = prime * result
                        + ((serviceDescription == null) ? 0 : serviceDescription.hashCode());
        result = prime * result + ((serviceId == null) ? 0 : serviceId.hashCode());
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
        final ServiceMetaData other = (ServiceMetaData) obj;
        if (policies == null) {
            if (other.policies != null) {
                return false;
            }
        } else if (!policies.equals(other.policies)) {
            return false;
        }
        if (serviceDescription == null) {
            if (other.serviceDescription != null) {
                return false;
            }
        } else if (!serviceDescription.equals(other.serviceDescription)) {
            return false;
        }
        if (serviceId == null) {
            if (other.serviceId != null) {
                return false;
            }
        } else if (!serviceId.equals(other.serviceId)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ServiceMetaData [serviceId=" + serviceId + ", serviceDescription="
                        + serviceDescription + ", policies=" + policies + "]";
    }
}
