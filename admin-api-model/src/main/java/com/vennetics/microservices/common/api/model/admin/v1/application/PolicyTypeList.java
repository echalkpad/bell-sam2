package com.vennetics.microservices.common.api.model.admin.v1.application;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;
import java.util.Objects;

/**
 * PolicyTypeList pojo for use in REST calls
 */
@XmlRootElement(name = "policyTypeList")
public final class PolicyTypeList {

    @XmlElement(name = "policyTypes")
    private List<PolicyType> policyTypes;

    public PolicyTypeList() {
        // For JSON deserialsation.
    }

    public PolicyTypeList(final List<PolicyType> policyTypes) {
        this.policyTypes = policyTypes;
    }

    public List<PolicyType> getPolicyTypes() {
        return policyTypes;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final PolicyTypeList that = (PolicyTypeList) o;
        return Objects.equals(policyTypes, that.policyTypes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(policyTypes);
    }

    @Override public String toString() {
        return "PolicyTypeList{" + "policyTypes=" + policyTypes + '}';
    }
}
