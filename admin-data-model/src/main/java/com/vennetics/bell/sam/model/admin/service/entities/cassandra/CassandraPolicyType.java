package com.vennetics.bell.sam.model.admin.service.entities.cassandra;

import org.springframework.data.cassandra.mapping.Column;
import org.springframework.data.cassandra.mapping.PrimaryKey;
import org.springframework.data.cassandra.mapping.Table;

import java.util.Objects;
import java.util.Set;

/**
 * Policy Type Entity
 *
 */
@Table(CassandraPolicyType.TABLE_NAME)
public final class CassandraPolicyType {

    public static final String TABLE_NAME = "policy_types";
    public static final String POLICY_TYPE_ID = "policy_type_id";
    public static final String NAME = "name";
    public static final String POLICY_ORDER = "policy_order";
    public static final String SERVICE_TYPE_IDS = "service_type_ids";

    @PrimaryKey(POLICY_TYPE_ID)
    private String policyTypeId;
    @Column(NAME)
    // Naming squid for capital to lowercase differentiation
    @SuppressWarnings("squid:S1845")
    private String name;
    @Column(POLICY_ORDER)
    private Integer policyOrder;
    @Column(SERVICE_TYPE_IDS)
    private Set<String> serviceTypeIds;

    public String getPolicyTypeId() {
        return policyTypeId;
    }

    public void setPolicyTypeId(final String policyTypeId) {
        this.policyTypeId = policyTypeId;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public Set<String> getServiceTypeIds() {
        return serviceTypeIds;
    }

    public void setServiceTypeIds(final Set<String> serviceTypeIds) {
        this.serviceTypeIds = serviceTypeIds;
    }

    public Integer getPolicyOrder() {
        return policyOrder;
    }

    public void setPolicyOrder(final Integer policyOrder) {
        this.policyOrder = policyOrder;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final CassandraPolicyType that = (CassandraPolicyType) o;
        return Objects.equals(policyTypeId, that.policyTypeId)
                && Objects.equals(name, that.name)
                && Objects.equals(policyOrder, that.policyOrder)
                && Objects.equals(serviceTypeIds, that.serviceTypeIds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(policyTypeId, name, policyOrder, serviceTypeIds);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("CassandraPolicyType{");
        sb.append("policyTypeId='").append(policyTypeId).append('\'');
        sb.append(", name='").append(name).append('\'');
        sb.append(", policyOrder=").append(policyOrder);
        sb.append(", serviceTypeIds=").append(serviceTypeIds);
        sb.append('}');
        return sb.toString();
    }
}
