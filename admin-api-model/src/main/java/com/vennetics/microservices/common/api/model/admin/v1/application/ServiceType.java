package com.vennetics.microservices.common.api.model.admin.v1.application;

import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Objects;
import java.util.Set;

/**
 * ServiceType  pojo for use in REST calls
 */
@XmlRootElement(name = "serviceType")
public final class ServiceType {

    @NotBlank
    @XmlElement(name = "serviceTypeId")
    private String serviceTypeId;
    @NotBlank
    @XmlElement(name = "description")
    private String description;
    @NotEmpty
    @XmlElement(name = "policyTypeIds")
    private Set<String> policyTypeIds;
    @NotBlank
    @XmlElement(name = "protocolTypeId")
    private String protocolTypeId;

    public ServiceType() {
        // For JSON deserialsation.
    }

    public ServiceType(final String serviceTypeId,
                       final String description,
                       final Set<String> policyTypeIds,
                       final String protocolTypeId) {
        this.serviceTypeId = serviceTypeId;
        this.description = description;
        this.policyTypeIds = policyTypeIds;
        this.protocolTypeId = protocolTypeId;
    }

    public ServiceType(final String serviceTypeId,
                       final String description,
                       final Set<String> policyTypeIds) {
        this.serviceTypeId = serviceTypeId;
        this.description = description;
        this.policyTypeIds = policyTypeIds;
    }

    public String getServiceTypeId() {
        return serviceTypeId;
    }

    public String getDescription() {
        return description;
    }

    public Set<String> getPolicyTypeIds() {
        return policyTypeIds;
    }

    public String getProtocolTypeId() {
        return protocolTypeId;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final ServiceType that = (ServiceType) o;
        return Objects.equals(serviceTypeId, that.serviceTypeId)
            && Objects.equals(description, that.description)
            && Objects.equals(policyTypeIds, that.policyTypeIds)
            && Objects.equals(protocolTypeId, that.protocolTypeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(serviceTypeId, description, policyTypeIds, protocolTypeId);
    }

    @Override public String toString() {
        return "ServiceType{" + "serviceTypeId='" + serviceTypeId + '\''
            + ", description='" + description + '\''
            + ", policyTypeIds=" + policyTypeIds
            + ", protocolTypeId='" + protocolTypeId + '\'' + '}';
    }
}
