package com.vennetics.microservices.common.core.security.jwt;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.security.core.context.SecurityContextHolder;

import com.vennetics.microservices.common.api.model.admin.v1.application.ApplicationUserDetails;

/**
 * Interceptor that will add a JWT header based on
 * {@link ApplicationUserDetails} to all requests made via a RestTemplate
 */
public class JwtHeaderClientRequestInterceptor implements ClientHttpRequestInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(JwtHeaderClientRequestInterceptor.class);

    private final IJwtTokenBuilder<ApplicationUserDetails> jwtTokenBuilder;

    @Autowired
    public JwtHeaderClientRequestInterceptor(final IJwtTokenBuilder<ApplicationUserDetails> jwtTokenBuilder) {
        super();
        this.jwtTokenBuilder = jwtTokenBuilder;
    }

    @Override
    public ClientHttpResponse intercept(final HttpRequest request,
                                        final byte[] body,
                                        final ClientHttpRequestExecution execution) throws IOException {

        final ApplicationUserDetails applicationUserDetails = getApplicationUserDetailsIfOnContext();

        if (applicationUserDetails != null) {

            logger.debug("Adding user details to request: {}", applicationUserDetails);
            final HttpHeaders headers = request.getHeaders();
            headers.add(JwtConstants.JWT_HEADER_INTERNAL,
                        jwtTokenBuilder.createJwtTokenFromSubject(applicationUserDetails));
        } else {

            logger.debug("No user details to add to request, continuing");
        }

        return execution.execute(request, body);

    }

    private static ApplicationUserDetails getApplicationUserDetailsIfOnContext() {

        if (SecurityContextHolder.getContext() != null
                        && SecurityContextHolder.getContext().getAuthentication() != null) {
            final Object details = SecurityContextHolder.getContext()
                                                        .getAuthentication()
                                                        .getDetails();

            if (details instanceof ApplicationUserDetails) {
                return (ApplicationUserDetails) details;
            }

        }

        return null;
    }

}
