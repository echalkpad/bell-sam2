package com.vennetics.bell.sam.sdm.adapter.api;

import java.util.Map;
import java.util.Set;

/**
 * Class represents and SDM Account
 */
public final class SdmAccount {

    private final Map<String, Set<String>> attributes;

    /**
     * SdmAccount Constructor
     * @param attributes
     */
    public SdmAccount(final Map<String, Set<String>> attributes) {
        this.attributes = attributes;
    }

    /**
     * Getter to return a map that represents the attributes on the SDM LDAP
     *
     * @return attributes
     */
    public Map<String, Set<String>> getAttributes() {
        return  attributes;
    }

    @Override
    public String toString() {
        return "SdmAccount [attributes=" + attributes + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (attributes == null ? 0 : attributes.hashCode());
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
        final SdmAccount other = (SdmAccount) obj;
        if (attributes == null) {
            if (other.attributes != null) {
                return false;
            }
        } else if (!attributes.equals(other.attributes)) {
            return false;
        }
        return true;
    }

}
