package com.vennetics.microservices.common.api.model.admin.v1.application;

import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Map;
import java.util.Set;

@XmlRootElement(name = "applicationPolicy")
public final class ApplicationPolicy {

    @NotBlank
    @XmlElement(name = "policyId")
    private String policyId;

    @NotEmpty
    @XmlElement(name = "values")
    private Map<String, Set<String>> values;

    protected ApplicationPolicy() {
        // For JSON deserialisation
    }

    /**
     * Full constructor.
     *
     * @param policyId
     * @param values
     */
    public ApplicationPolicy(final String policyId, final Map<String, Set<String>> values) {
        super();
        this.policyId = policyId;
        this.values = values;
    }

    public String getPolicyId() {
        return policyId;
    }

    public Map<String, Set<String>> getValues() {
        return values;
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
        final ApplicationPolicy other = (ApplicationPolicy) obj;
        if (policyId == null) {
            if (other.policyId != null) {
                return false;
            }
        } else if (!policyId.equals(other.policyId)) {
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
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (policyId == null ? 0 : policyId.hashCode());
        result = prime * result + (values == null ? 0 : values.hashCode());
        return result;
    }

    @Override
    public String toString() {
        return "ApplicationPolicy [policyId=" + policyId + ", values=" + values + "]";
    }

}
