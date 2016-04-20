package com.vennetics.bell.sam.model.admin.service.entities.cassandra;

import org.springframework.data.cassandra.mapping.Column;
import org.springframework.data.cassandra.mapping.PrimaryKey;
import org.springframework.data.cassandra.mapping.Table;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * CassandraRole Type Entity
 *
 */
@Table(CassandraRole.TABLE_NAME)
public final class CassandraRole {

    public static final String TABLE_NAME = "roles";
    public static final String ROLE_ID = "role_id";
    public static final String DESCRIPTION = "description";
    public static final String SERVICE_TYPE_IDS = "service_type_ids";
    public static final String APPLICATION_IDS = "application_ids";
    public static final String ADMIN = "ADMIN";
    public static final String USER = "USER";
    public static final String SPRING_SEC_ADMIN = "ROLE_ADMIN";
    public static final String SPRING_SEC_USER = "ROLE_USER";


    @PrimaryKey(ROLE_ID)
    private String roleId;
    @Column(DESCRIPTION)
    // Naming squid for capital to lowercase differentiation
    @SuppressWarnings("squid:S1845")
    private String description;
    @Column(SERVICE_TYPE_IDS)
    private Set<String> serviceTypeIds;
    @Column(APPLICATION_IDS)
    private Set<UUID> applicationIds;

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(final String roleId) {
        this.roleId = roleId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public Set<String> getServiceTypeIds() {
        return serviceTypeIds;
    }

    public void setServiceTypeIds(final Set<String> serviceTypeIds) {
        this.serviceTypeIds = serviceTypeIds;
    }

    public Set<UUID> getApplicationIds() {
        return applicationIds;
    }

    public void setApplicationIds(final Set<UUID> applicationIds) {
        this.applicationIds = applicationIds;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final CassandraRole cassandraRole = (CassandraRole) o;
        return Objects.equals(roleId, cassandraRole.roleId)
                && Objects.equals(description, cassandraRole.description)
                && Objects.equals(serviceTypeIds, cassandraRole.serviceTypeIds)
                && Objects.equals(applicationIds, cassandraRole.applicationIds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(roleId, description, serviceTypeIds, applicationIds);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("CassandraRole{");
        sb.append("roleId='").append(roleId).append('\'');
        sb.append(", description='").append(description).append('\'');
        sb.append(", serviceTypeIds=").append(serviceTypeIds);
        sb.append(", applicationIds=").append(applicationIds);
        sb.append('}');
        return sb.toString();
    }
}
