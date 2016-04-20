package com.vennetics.bell.sam.model.admin.service.entities.cassandra;

import org.springframework.data.cassandra.mapping.Column;
import org.springframework.data.cassandra.mapping.PrimaryKey;
import org.springframework.data.cassandra.mapping.Table;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Date;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * Third Party CassandraApplication Entity
 *
 */
// Naming squid for capital to lowercase differentiation
@SuppressWarnings("squid:S1845")
@Table(value = CassandraApplication.TABLE_NAME)
public class CassandraApplication {

    public static final BCryptPasswordEncoder PASSWORD_ENCODER = new BCryptPasswordEncoder();
    public static final String TABLE_NAME = "applications";
    public static final String APPLICATION_ID = "application_id";
    public static final String CLIENT_ID = "client_id";
    public static final String ROLE_IDS = "role_ids";
    public static final String POLICIES = "policies";
    public static final String GATEWAY_POLICIES = "gateway_policies";
    public static final String ALLOWED_SERVICE_IDS = "allowed_service_ids";
    public static final String NAME = "name";
    public static final String EMAIL = "email";
    public static final String SECRET = "secret";
    public static final String ACCOUNT_STATE = "account_state";
    public static final String CREATED_TIMESTAMP = "created_timestamp";
    public static final String LAST_MODIFIED_TIMESTAMP = "last_modified_timestamp";
    public static final String BCRYPT_PREFIX = "$2a$10$";

    @PrimaryKey(APPLICATION_ID)
    private UUID applicationId;
    @Column(CLIENT_ID)
    private String clientId;
    @Column(ROLE_IDS)
    private Set<String> roleIds;
    @Column(POLICIES)
    private Set<String> policies;
    @Column(GATEWAY_POLICIES)
    private Set<String> gatewayPolicies;
    @Column(ALLOWED_SERVICE_IDS)
    private Set<String> allowedServiceIds;
    @Column(NAME)
    private String name;
    @Column(EMAIL)
    private String email;
    @Column(SECRET)
    private String secret;
    @Column(ACCOUNT_STATE)
    private String accountState;
    @Column(CREATED_TIMESTAMP)
    private Date createdTimestamp;
    @Column(LAST_MODIFIED_TIMESTAMP)
    private Date lastModifiedTimestamp;

    public CassandraApplication(final CassandraApplication cassandraApplication) {
        this.applicationId = cassandraApplication.getApplicationId();
        this.clientId = cassandraApplication.getClientId();
        this.roleIds = cassandraApplication.getRoleIds();
        this.policies = cassandraApplication.getPolicies();
        this.gatewayPolicies = cassandraApplication.getGatewayPolicies();
        this.allowedServiceIds = cassandraApplication.getAllowedServiceIds();
        this.name = cassandraApplication.getName();
        this.email = cassandraApplication.getEmail();
        this.secret = cassandraApplication.getSecret();
        this.accountState = cassandraApplication.getAccountState();
        this.createdTimestamp = cassandraApplication.getCreatedTimestamp();
        this.lastModifiedTimestamp = cassandraApplication.getLastModifiedTimestamp();
    }

    // Suppress more than 7 constructor parameters
    @SuppressWarnings("squid:S00107")
    public CassandraApplication(final UUID applicationId,
                                final String clientId,
                                final String name,
                                final String email,
                                final String secret,
                                final String accountState,
                                final Date createdTimestamp,
                                final Date lastModifiedTimestamp) {
        this.applicationId = applicationId;
        this.clientId = clientId;
        this.name = name;
        this.email = email;
        this.secret = secret;
        this.accountState = accountState;
        this.createdTimestamp = createdTimestamp;
        this.lastModifiedTimestamp = lastModifiedTimestamp;
    }

    public CassandraApplication() {
        //Default Constructor
    }

    public UUID getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(final UUID applicationId) {
        this.applicationId = applicationId;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(final String clientId) {
        this.clientId = clientId;
    }

    public Set<String> getRoleIds() {
        return roleIds;
    }

    public void setRoleIds(final Set<String> roleIds) {
        this.roleIds = roleIds;
    }

    public Set<String> getPolicies() {
        return policies;
    }

    public void setPolicies(final Set<String> policies) {
        this.policies = policies;
    }

    public Set<String> getGatewayPolicies() {
        return gatewayPolicies;
    }

    public void setGatewayPolicies(final Set<String> gatewayPolicies) {
        this.gatewayPolicies = gatewayPolicies;
    }

    public Set<String> getAllowedServiceIds() {
        return allowedServiceIds;
    }

    public void setAllowedServiceIds(final Set<String> allowedServiceIds) {
        this.allowedServiceIds = allowedServiceIds;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(final String email) {
        this.email = email;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(final String secret) {
        if (!secret.startsWith(BCRYPT_PREFIX)) {
            this.secret = PASSWORD_ENCODER.encode(secret);
        } else {
            this.secret = secret;
        }
    }

    public String getAccountState() {
        return accountState;
    }

    public void setAccountState(final String accountState) {
        this.accountState = accountState;
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

    // Suppress conditional operations warning
    @SuppressWarnings("squid:S1067")
    @Override
    public final boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CassandraApplication)) {
            return false;
        }
        final CassandraApplication that = (CassandraApplication) o;
        return Objects.equals(applicationId, that.applicationId)
                && Objects.equals(clientId, that.clientId)
                && Objects.equals(roleIds, that.roleIds)
                && Objects.equals(policies, that.policies)
                && Objects.equals(gatewayPolicies, that.gatewayPolicies)
                && Objects.equals(allowedServiceIds, that.allowedServiceIds)
                && Objects.equals(name, that.name)
                && Objects.equals(email, that.email)
                && Objects.equals(secret, that.secret)
                && Objects.equals(accountState, that.accountState)
                && Objects.equals(createdTimestamp, that.createdTimestamp)
                && Objects.equals(lastModifiedTimestamp, that.lastModifiedTimestamp);
    }

    @Override
    public final int hashCode() {
        return Objects.hash(applicationId,
                            clientId,
                            roleIds,
                            policies,
                            gatewayPolicies,
                            allowedServiceIds,
                            name,
                            email,
                            secret,
                            accountState,
                            createdTimestamp,
                            lastModifiedTimestamp);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("CassandraApplication{");
        sb.append("applicationId=").append(applicationId);
        sb.append(", clientId='").append(clientId).append('\'');
        sb.append(", roleIds=").append(roleIds);
        sb.append(", policies=").append(policies);
        sb.append(", gatewayPolicies=").append(gatewayPolicies);
        sb.append(", allowedServiceIds=").append(allowedServiceIds);
        sb.append(", name='").append(name).append('\'');
        sb.append(", email='").append(email).append('\'');
        sb.append(", accountState='").append(accountState).append('\'');
        sb.append(", createdTimestamp=").append(createdTimestamp);
        sb.append(", lastModifiedTimestamp=").append(lastModifiedTimestamp);
        sb.append('}');
        return sb.toString();
    }
}
