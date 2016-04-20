package com.vennetics.bell.sam.model.admin.service.entities.cassandra;

import org.springframework.data.cassandra.mapping.Column;
import org.springframework.data.cassandra.mapping.PrimaryKey;
import org.springframework.data.cassandra.mapping.Table;

import java.util.Objects;
import java.util.Set;

/**
 * AllowedService Type Entity
 *
 */
@Table(CassandraServiceType.TABLE_NAME)
public final class CassandraServiceType {

    public static final String TABLE_NAME = "service_types";
    public static final String SERVICE_TYPE_ID = "service_type_id";
    public static final String NAME = "name";
    public static final String POLICY_TYPE_IDS = "policy_type_ids";
    public static final String PROTOCOL_TYPE_ID = "protocol_type_id";

    @PrimaryKey(SERVICE_TYPE_ID)
    private String serviceTypeId;
    @Column(NAME)
    // Naming squid for capital to lowercase differentiation
    @SuppressWarnings("squid:S1845")
    private String name;
    @Column(POLICY_TYPE_IDS)
    private Set<String> policyTypeIds;
    @Column(PROTOCOL_TYPE_ID)
    private String protocolTypeId;

    public String getServiceTypeId() {
        return serviceTypeId;
    }

    public void setServiceTypeId(final String serviceTypeId) {
        this.serviceTypeId = serviceTypeId;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public Set<String> getPolicyTypeIds() {
        return policyTypeIds;
    }

    public void setPolicyTypeIds(final Set<String> policyTypeIds) {
        this.policyTypeIds = policyTypeIds;
    }

    public String getProtocolTypeId() {
        return protocolTypeId;
    }

    public void setProtocolTypeId(final String protocolTypeId) {
        this.protocolTypeId = protocolTypeId;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final CassandraServiceType that = (CassandraServiceType) o;
        return Objects.equals(serviceTypeId, that.serviceTypeId)
                && Objects.equals(name, that.name)
                && Objects.equals(policyTypeIds, that.policyTypeIds)
                && Objects.equals(protocolTypeId, that.protocolTypeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(serviceTypeId, name, policyTypeIds, protocolTypeId);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("CassandraServiceType{");
        sb.append("serviceTypeId='").append(serviceTypeId).append('\'');
        sb.append(", name='").append(name).append('\'');
        sb.append(", policyTypeIds=").append(policyTypeIds);
        sb.append(", protocolTypeId='").append(protocolTypeId).append('\'');
        sb.append('}');
        return sb.toString();
    }
}

