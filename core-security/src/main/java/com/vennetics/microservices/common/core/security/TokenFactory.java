package com.vennetics.microservices.common.core.security;

import java.util.Collection;
import java.util.HashSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.util.CollectionUtils;

import com.vennetics.microservices.common.api.model.admin.AdminApiModelConstants;
import com.vennetics.microservices.common.api.model.admin.v1.application.ApplicationUserDetails;

/**
 * Factory interface for mapping from J-Box {@link ApplicationUserDetails} to a
 * Spring {@link Authentication} POJO
 */
public final class TokenFactory {

    private static final Logger logger = LoggerFactory.getLogger(TokenFactory.class);

    private TokenFactory() {

    }

    /**
     * Creates the authentication token that will be passed back into
     * spring-security.
     *
     * @param authentication
     * @param clientId
     * @param userDetails
     * @return the token.
     */
    public static Authentication createToken(final ApplicationUserDetails userDetails) {
        final Collection<GrantedAuthority> authorities = new HashSet<>();

        // Add role IDs as authority
        if (!CollectionUtils.isEmpty(userDetails.getRoles())) {
            userDetails.getRoles().stream().forEach(
                                                    role -> authorities.add(new SimpleGrantedAuthority(AdminApiModelConstants.AUTHORITY_ROLE_PREFIX
                                                                    + role.toUpperCase())));
        }

        // Add allowed services as a granted authority
        if (!CollectionUtils.isEmpty(userDetails.getAllowedServices())) {

            userDetails.getAllowedServices()
                       .stream()
                       .forEach(allowedService -> authorities.add(new SimpleGrantedAuthority(AdminApiModelConstants.AUTHORITY_SERVICE_PREFIX
                                       + allowedService.getServiceTypeId())));

        }
        // Checks passed
        final UsernamePasswordAuthenticationToken result = new UsernamePasswordAuthenticationToken(userDetails.getClientId(),
                                                                                                   userDetails.getSecret(),
                                                                                                   authorities);

        // Store user details in the POJO additional information
        result.setDetails(userDetails);

        logger.debug("Created authentication token: {}", userDetails);
        return result;
    }

}
