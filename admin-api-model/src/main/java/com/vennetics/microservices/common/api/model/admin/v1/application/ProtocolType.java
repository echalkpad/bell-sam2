package com.vennetics.microservices.common.api.model.admin.v1.application;

import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Objects;
import java.util.Set;

/**
 * ProtocolType pojo for use in REST calls
 */
@XmlRootElement(name = "protocolType")
public final class ProtocolType {

    @NotBlank
    @XmlElement(name = "protocolTypeId")
    private String protocolTypeId;
    @NotBlank
    @XmlElement(name = "description")
    private String description;
    @NotEmpty
    @XmlElement(name = "serviceTypeIds")
    private Set<String> serviceTypeIds;

    public ProtocolType() {
        // For JSON deserialsation.
    }

    public ProtocolType(final String protocolTypeId, final String description, final Set<String> serviceTypeIds) {
        this.protocolTypeId = protocolTypeId;
        this.description = description;
        this.serviceTypeIds = serviceTypeIds;
    }

    public String getProtocolTypeId() {
        return protocolTypeId;
    }

    public String getDescription() {
        return description;
    }

    public Set<String> getServiceTypeIds() {
        return serviceTypeIds;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final ProtocolType that = (ProtocolType) o;
        return Objects.equals(protocolTypeId, that.protocolTypeId)
            && Objects.equals(description, that.description)
            && Objects.equals(serviceTypeIds, that.serviceTypeIds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(protocolTypeId, description, serviceTypeIds);
    }

    @Override public String toString() {
        return "ProtocolType{"
            + "protocolTypeId='" + protocolTypeId + '\''
            + ", description='" + description + '\''
            + ", serviceTypeIds=" + serviceTypeIds + '}';
    }
}
