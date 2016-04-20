package com.vennetics.microservices.common.api.model.admin.register.pojo;

import java.util.HashSet;
import java.util.Set;

public final class PolicyMetaData {

    private final String policyId;

    private final String policyDescription;

    private final Set<PolicyKeyMetaData> policyKeys;

    public PolicyMetaData(final String policyId,
                          final String policyDescription,
                          final Set<PolicyKeyMetaData> policyKeys) {
        super();
        this.policyId = policyId;
        this.policyDescription = policyDescription;
        this.policyKeys = policyKeys;
    }

    public String getPolicyDescription() {
        return policyDescription;
    }

    public String getPolicyId() {
        return policyId;
    }

    public Set<PolicyKeyMetaData> getPolicyKeys() {
        return policyKeys;
    }

    public Set<String> getPolicyMandatoryKeys() {
        Set<String> policyMandatoryKeys = new HashSet<>();
        policyKeys.stream()
                  .forEach(policyKeyMetaData -> policyMandatoryKeys.add(policyKeyMetaData.getKey()));
        return policyMandatoryKeys;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((policyDescription == null) ? 0 : policyDescription.hashCode());
        result = prime * result + ((policyId == null) ? 0 : policyId.hashCode());
        result = prime * result + ((policyKeys == null) ? 0 : policyKeys.hashCode());
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
        final PolicyMetaData other = (PolicyMetaData) obj;
        if (policyDescription == null) {
            if (other.policyDescription != null) {
                return false;
            }
        } else if (!policyDescription.equals(other.policyDescription)) {
            return false;
        }
        if (policyId == null) {
            if (other.policyId != null) {
                return false;
            }
        } else if (!policyId.equals(other.policyId)) {
            return false;
        }
        if (policyKeys == null) {
            if (other.policyKeys != null) {
                return false;
            }
        } else if (!policyKeys.equals(other.policyKeys)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "PolicyMetaData [policyId=" + policyId + ", policyDescription=" + policyDescription
                        + ", policyKeys=" + policyKeys + "]";
    }

}
