package com.vennetics.microservices.common.core.security.jwt;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.vennetics.microservices.common.api.model.admin.v1.application.ApplicationUserDetails;

/**
 * Will intecept a Controller request and setup a spring header containing the
 * {@link ApplicationUserDetails} POJO.
 */
public class JwtHeaderServerInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(JwtHeaderServerInterceptor.class);

    private final IJwtTokenBuilder<ApplicationUserDetails> controllerTokenBuilder;

    public JwtHeaderServerInterceptor(final IJwtTokenBuilder<ApplicationUserDetails> controllerTokenBuilder) {
        super();
        this.controllerTokenBuilder = controllerTokenBuilder;
    }

    @Override
    public boolean preHandle(final HttpServletRequest request,
                             final HttpServletResponse response,
                             final Object handler) {

        final String jwtHeader = request.getHeader(JwtConstants.JWT_HEADER_INTERNAL);

        if (jwtHeader != null) {

            logger.debug("Found Header:{} hydrating security info.",
                         JwtConstants.JWT_HEADER_INTERNAL);

            final ApplicationUserDetails applicationUserDetails = controllerTokenBuilder.getSubjectFromJwtToken(jwtHeader,
                                                                                                         ApplicationUserDetails.class);

            request.setAttribute(JwtConstants.JWT_HEADER_INTERNAL, applicationUserDetails);
        }
        return true;
    }

    @Override
    public void postHandle(final HttpServletRequest request,
                           final HttpServletResponse response,
                           final Object handler,
                           final ModelAndView modelAndView) {
        // Do nothing

    }

    @Override
    public void afterCompletion(final HttpServletRequest request,
                                final HttpServletResponse response,
                                final Object handler,
                                final Exception ex) {
        // Do nothing

    }


}
