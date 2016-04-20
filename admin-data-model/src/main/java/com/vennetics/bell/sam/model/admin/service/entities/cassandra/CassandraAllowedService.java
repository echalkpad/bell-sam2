package com.vennetics.bell.sam.model.admin.service.entities.cassandra;

import org.springframework.data.cassandra.mapping.Column;
import org.springframework.data.cassandra.mapping.PrimaryKey;
import org.springframework.data.cassandra.mapping.Table;

import java.util.Date;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * Allowed Services Entity
 *
 */
@Table(value = CassandraAllowedService.TABLE_NAME)
public final class CassandraAllowedService {

    public static final String TABLE_NAME = "allowed_services";
    public static final String ALLOWED_SERVICE_ID = "allowed_service_id";
    public static final String APPLICATION_IDS = "application_ids";
    public static final String CREATED_TIMESTAMP = "created_timestamp";
    public static final String LAST_MODIFIED_TIMESTAMP = "last_modified_timestamp";

    @PrimaryKey(ALLOWED_SERVICE_ID)
    private String allowedServiceId;
    @Column(APPLICATION_IDS)
    private Set<UUID> applicationIds;
    @Column(CREATED_TIMESTAMP)
    private Date createdTimestamp;
    @Column(LAST_MODIFIED_TIMESTAMP)
    private Date lastModifiedTimestamp;

    public String getAllowedServiceId() {
        return allowedServiceId;
    }

    public void setAllowedServiceId(final String allowedServiceId) {
        this.allowedServiceId = allowedServiceId;
    }

    public Set<UUID> getApplicationIds() {
        return applicationIds;
    }

    public void setApplicationIds(final Set<UUID> applicationIds) {
        this.applicationIds = applicationIds;
    }

    public Date getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(final Date createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    public Date getLastModifiedTimestamp() {
        return lastModifiedTimestamp;
    }

    public void setLastModifiedTimestamp(final Date lastModifiedTimestamp) {
        this.lastModifiedTimestamp = lastModifiedTimestamp;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final CassandraAllowedService that = (CassandraAllowedService) o;
        return Objects.equals(allowedServiceId, that.allowedServiceId)
                && Objects.equals(applicationIds, that.applicationIds)
                && Objects.equals(createdTimestamp, that.createdTimestamp)
                && Objects.equals(lastModifiedTimestamp, that.lastModifiedTimestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(allowedServiceId, applicationIds, createdTimestamp, lastModifiedTimestamp);
    }

    @Override
    public String toString() {
        return "CassandraAllowedService{"
                + "allowedServiceId='" + allowedServiceId + '\''
                + ", applicationIds=" + applicationIds
                + ", createdTimestamp=" + createdTimestamp
                + ", lastModifiedTimestamp=" + lastModifiedTimestamp + '}';
    }
}
