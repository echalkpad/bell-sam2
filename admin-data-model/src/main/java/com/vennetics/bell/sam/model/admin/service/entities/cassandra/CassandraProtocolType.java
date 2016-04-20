package com.vennetics.bell.sam.model.admin.service.entities.cassandra;

import org.springframework.data.cassandra.mapping.Column;
import org.springframework.data.cassandra.mapping.PrimaryKey;
import org.springframework.data.cassandra.mapping.Table;

import java.util.Objects;
import java.util.Set;

/**
 * Protocol Type Entity
 *
 */
@Table(CassandraProtocolType.TABLE_NAME)
public final class CassandraProtocolType {

    public static final String TABLE_NAME = "protocol_types";
    public static final String PROTOCOL_TYPE_ID = "protocol_type_Id";
    public static final String DESCRIPTION = "description";
    public static final String SERVICE_TYPE_IDS = "service_type_ids";

    @PrimaryKey(PROTOCOL_TYPE_ID)
    private String protocolTypeId;
    @Column(DESCRIPTION)
    // Naming squid for capital to lowercase differentiation
    @SuppressWarnings("squid:S1845")
    private String description;
    @Column(SERVICE_TYPE_IDS)
    private Set<String> serviceTypeIds;

    public Set<String> getServiceTypeIds() {
        return serviceTypeIds;
    }

    public void setServiceTypeIds(final Set<String> serviceTypeIds) {
        this.serviceTypeIds = serviceTypeIds;
    }

    public String getProtocolTypeId() {
        return protocolTypeId;
    }

    public void setProtocolTypeId(final String protocolTypeId) {
        this.protocolTypeId = protocolTypeId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }



    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final CassandraProtocolType that = (CassandraProtocolType) o;
        return Objects.equals(protocolTypeId, that.protocolTypeId)
                && Objects.equals(description, that.description)
                && Objects.equals(serviceTypeIds, that.serviceTypeIds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(protocolTypeId, description, serviceTypeIds);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("CassandraProtocolType{");
        sb.append("protocolTypeId='").append(protocolTypeId).append('\'');
        sb.append(", description='").append(description).append('\'');
        sb.append(", serviceTypeIds=").append(serviceTypeIds);
        sb.append('}');
        return sb.toString();
    }
}
