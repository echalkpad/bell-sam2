package com.vennetics.bell.sam.model.admin.service.pojo;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;
import java.util.Set;

/**
 * Policy Pojo
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public final class Policy {

    private final String policyId;
    private final Map<String, Set<String>> values;
    private final String serviceTypeId;

    @JsonCreator
    public Policy(@JsonProperty("policyId") final String policyId,
                  @JsonProperty("values") final Map<String, Set<String>> values,
                  @JsonProperty("serviceTypeId") final String serviceTypeId) {
        this.policyId = policyId;
        this.values = values;
        this.serviceTypeId = serviceTypeId;
    }

    public String getPolicyId() {
        return policyId;
    }

    public Map<String, Set<String>> getValues() {
        return values;
    }

    public String getServiceTypeId() {
        return serviceTypeId;
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
        final Policy other = (Policy) obj;
        if (policyId == null) {
            if (other.policyId != null) {
                return false;
            }
        } else if (!policyId.equals(other.policyId)) {
            return false;
        }
        if (serviceTypeId == null) {
            if (other.serviceTypeId != null) {
                return false;
            }
        } else if (!serviceTypeId.equals(other.serviceTypeId)) {
            return false;
        }
        if (values == null) {
            if (other.values != null) {
                return false;
            }
        } else if (!values.equals(other.values)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Policy [policyId=" + policyId + ", values=" + values + ", serviceTypeId="
                        + serviceTypeId + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (policyId == null ? 0 : policyId.hashCode());
        result = prime * result + (serviceTypeId == null ? 0 : serviceTypeId.hashCode());
        result = prime * result + (values == null ? 0 : values.hashCode());
        return result;
    }
}
