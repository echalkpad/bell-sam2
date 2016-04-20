package com.vennetics.bell.sam.apigateway.server.security;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.client.RestTemplate;

import com.netflix.hystrix.exception.HystrixBadRequestException;
import com.vennetics.bell.sam.apigateway.server.commands.GetUserDetailsCommand;
import com.vennetics.microservices.common.api.model.admin.v1.application.ApplicationUserDetails;
import com.vennetics.microservices.common.core.security.TokenFactory;

/**
 * Implements username password authentication for TPA application.
 */
public class ApplicationAuthenticationProvider implements AuthenticationProvider {

    private static final String AUTHENTICATION_FAILED_CREDENTIALS_MISSING = "Authentication failed: credentials missing";

    private static final Logger logger = LoggerFactory.getLogger(ApplicationAuthenticationProvider.class);

    private final ICachedPasswordChecker passwordChecker;

    private final RestTemplate restTemplate;

    public ApplicationAuthenticationProvider(final ICachedPasswordChecker passwordChecker,
                                             final RestTemplate restTemplate) {
        super();
        this.passwordChecker = passwordChecker;
        this.restTemplate = restTemplate;
    }

    @Override
    public Authentication authenticate(final Authentication authentication) {

        final String clientId = (String) authentication.getPrincipal();

        validateClientIdSupplied(clientId);

        validateCredentialsSupplied(authentication, clientId);

        final ApplicationUserDetails userDetails = queryAdminServer(clientId);

        if (userDetails == null) {
            // On not found
            // Could return null here and mean the request is passed to another
            // provider.
            // Or throw an ex and not allow any other auth schemes.
            // Will return null to allow for other providers.

            logger.warn("Authentication failed:  to find application by clientId: {}. ", clientId);
            return null;
        }

        validatePassword(authentication, clientId, userDetails);

        validateClientIsActive(clientId, userDetails);

        logger.debug("Authentication succeeded for :{}", clientId);

        return TokenFactory.createToken(userDetails);
    }

    private static void validateClientIsActive(final String clientId,
                                               final ApplicationUserDetails userDetails) {
        if (!"ACTIVE".equalsIgnoreCase(userDetails.getState())) {

            logger.warn("Authentication failed: client: {} is disabled", clientId);

            throw new DisabledException("Authentication failed: client: " + clientId
                            + " is disabled");
        }
    }

    private static void validateClientIdSupplied(final String clientId) {
        if (StringUtils.isBlank(clientId)) {
            logger.warn(AUTHENTICATION_FAILED_CREDENTIALS_MISSING);
            throw new AuthenticationCredentialsNotFoundException(AUTHENTICATION_FAILED_CREDENTIALS_MISSING);
        }
    }

    private static void validateCredentialsSupplied(final Authentication authentication,
                                                    final String clientId) {
        if (authentication.getCredentials() == null) {
            logger.warn("Authentication failed: no credentials provided by: {}", clientId);
            throw new BadCredentialsException("Authentication failed: no credentials provided by: "
                            + clientId);
        }
    }

    private void validatePassword(final Authentication authentication,
                                  final String clientId,
                                  final ApplicationUserDetails userDetails) {
        final String presentedPassword = authentication.getCredentials().toString();

        if (!passwordChecker.matches(presentedPassword, userDetails.getSecret())) {
            logger.warn("Authentication failed: password does not match stored value for clientId: ",
                        clientId);

            throw new BadCredentialsException("Authentication failed: password does not match stored value for clientId: "
                            + clientId);
        }
    }

    private ApplicationUserDetails queryAdminServer(final String clientId) {
        try {
            // Call to remote service
            // Using execute() as this call has to be blocking as implementing a
            // Spring defined interface.
            final ResponseEntity<ApplicationUserDetails> responseEntity = new GetUserDetailsCommand(clientId,
                                                                                                    restTemplate).execute();

            return responseEntity.getBody();
        } catch (final HystrixBadRequestException e) {
            // Explicit exception mapping to a spring-security framework based
            // error

            throw new InternalAuthenticationServiceException("Authentication failed: internal error authenticating clientId:"
                            + clientId, e);
        }
    }

    @Override
    public boolean supports(final Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }

}
