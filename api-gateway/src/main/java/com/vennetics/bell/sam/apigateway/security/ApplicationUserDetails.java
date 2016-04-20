package com.vennetics.bell.sam.apigateway.security;

import com.vennetics.bell.sam.model.admin.service.AccountState;
import com.vennetics.bell.sam.model.admin.service.entities.cassandra.CassandraApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.List;

/**
 * User object to abstract the Applications properties
 * required for API-Gateway Authentication
 */
public class ApplicationUserDetails extends CassandraApplication implements UserDetails {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationUserDetails.class);
    private final List<String> userRoles;

    public ApplicationUserDetails(final CassandraApplication cassandraApplication, final List<String> userRoles) {
        super(cassandraApplication);
        this.userRoles = userRoles;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        final String roles = StringUtils.collectionToCommaDelimitedString(userRoles);
        logger.debug("ApplicationUserDetails Authorities {}", AuthorityUtils.commaSeparatedStringToAuthorityList(roles).toString());
        return AuthorityUtils.commaSeparatedStringToAuthorityList(roles);
    }

    @Override
    public String getPassword() {
        return super.getSecret();
    }

    @Override
    public String getUsername() {
        return super.getClientId();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return AccountState.ACTIVE.toString().equalsIgnoreCase(super.getAccountState());
    }
}
