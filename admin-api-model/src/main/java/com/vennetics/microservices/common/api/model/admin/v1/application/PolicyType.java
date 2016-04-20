package com.vennetics.microservices.common.api.model.admin.v1.application;

import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Objects;
import java.util.Set;

/**
 * PolicyType pojo for use in REST calls
 */
@XmlRootElement(name = "policyType")
public final class PolicyType {

    @NotBlank
    @XmlElement(name = "policyTypeId")
    private String policyTypeId;
    @NotBlank
    @XmlElement(name = "description")
    private String description;
    @NotNull
    @Digits(integer = Integer.MAX_VALUE, fraction = 0)
    @XmlElement(name = "policyOrder")
    private Integer policyOrder;
    @NotEmpty
    @XmlElement(name = "serviceTypeIds")
    private Set<String> serviceTypeIds;

    public PolicyType() {
        // For JSON deserialsation.
    }

    public PolicyType(final String policyTypeId, final String description, final Integer policyOrder, final Set<String> serviceTypeIds) {
        this.policyTypeId = policyTypeId;
        this.description = description;
        this.policyOrder = policyOrder;
        this.serviceTypeIds = serviceTypeIds;
    }

    public String getPolicyTypeId() {
        return policyTypeId;
    }

    public String getDescription() {
        return description;
    }

    public Integer getPolicyOrder() {
        return policyOrder;
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
        final PolicyType that = (PolicyType) o;
        return Objects.equals(policyTypeId, that.policyTypeId)
            && Objects.equals(description, that.description)
            && Objects.equals(policyOrder, that.policyOrder)
            && Objects.equals(serviceTypeIds, that.serviceTypeIds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(policyTypeId, description, policyOrder, serviceTypeIds);
    }

    @Override public String toString() {
        return "PolicyType{"
            + "policyTypeId='" + policyTypeId + '\''
            + ", description='" + description + '\''
            + ", policyOrder=" + policyOrder
            + ", serviceTypeIds=" + serviceTypeIds + '}';
    }
}
