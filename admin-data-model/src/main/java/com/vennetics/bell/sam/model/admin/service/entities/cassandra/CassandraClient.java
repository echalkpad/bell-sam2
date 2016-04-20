package com.vennetics.bell.sam.model.admin.service.entities.cassandra;

import org.springframework.data.cassandra.mapping.Column;
import org.springframework.data.cassandra.mapping.PrimaryKey;
import org.springframework.data.cassandra.mapping.Table;

import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

/**
 * CassandraClient Entity
 *
 */
@Table(CassandraClient.TABLE_NAME)
public final class CassandraClient {

    public static final String TABLE_NAME = "clients";
    public static final String CLIENT_ID = "client_id";
    public static final String APPLICATION_ID = "application_id";
    public static final String CREATED_TIMESTAMP = "created_timestamp";
    public static final String LAST_MODIFIED_TIMESTAMP = "last_modified_timestamp";

    @PrimaryKey(CLIENT_ID)
    private String clientId;
    @Column(APPLICATION_ID)
    private UUID applicationId;
    @Column(CREATED_TIMESTAMP)
    private Date createdTimestamp;
    @Column(LAST_MODIFIED_TIMESTAMP)
    private Date lastModifiedTimestamp;

    /**
     * Constructor for creating a new table entry. Internally sets the
     * timestamps rather than requiring the user to do so.
     * 
     * @param clientId
     * @param applicationId
     */
    public CassandraClient(final String clientId, final UUID applicationId) {
        super();
        this.clientId = clientId;
        this.applicationId = applicationId;

        createdTimestamp = Date.from(ZonedDateTime.now().toInstant());
        lastModifiedTimestamp = Date.from(ZonedDateTime.now().toInstant());
    }

    public CassandraClient(final String clientId,
                           final UUID applicationId,
                           final Date createdTimestamp,
                           final Date lastModifiedTimestamp) {
        this.clientId = clientId;
        this.applicationId = applicationId;
        this.createdTimestamp = createdTimestamp;
        this.lastModifiedTimestamp = lastModifiedTimestamp;
    }

    public CassandraClient() {
        // Default constructor
    }

    public Date getLastModifiedTimestamp() {
        return lastModifiedTimestamp;
    }

    public void setLastModifiedTimestamp(final Date lastModifiedTimestamp) {
        this.lastModifiedTimestamp = lastModifiedTimestamp;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(final String clientId) {
        this.clientId = clientId;
    }

    public Date getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(final Date createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    public UUID getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(final UUID applicationId) {
        this.applicationId = applicationId;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final CassandraClient cassandraClient = (CassandraClient) o;
        return Objects.equals(clientId, cassandraClient.clientId)
                && Objects.equals(applicationId, cassandraClient.applicationId)
                && Objects.equals(createdTimestamp, cassandraClient.createdTimestamp)
                && Objects.equals(lastModifiedTimestamp, cassandraClient.lastModifiedTimestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clientId, applicationId, createdTimestamp, lastModifiedTimestamp);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("CassandraClient{");
        sb.append("clientId='").append(clientId).append('\'');
        sb.append(", applicationId=").append(applicationId);
        sb.append(", createdTimestamp=").append(createdTimestamp);
        sb.append(", lastModifiedTimestamp=").append(lastModifiedTimestamp);
        sb.append('}');
        return sb.toString();
    }
}
