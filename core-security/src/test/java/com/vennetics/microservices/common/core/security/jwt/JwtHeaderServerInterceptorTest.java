package com.vennetics.microservices.common.core.security.jwt;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.vennetics.microservices.common.api.model.admin.util.AdminApiTestData;
import com.vennetics.microservices.common.api.model.admin.v1.application.ApplicationUserDetails;

@RunWith(MockitoJUnitRunner.class)
public class JwtHeaderServerInterceptorTest {

    private static final String JWT_HEADER_VALUE = "JWT_HEADER_VALUE";

    private JwtHeaderServerInterceptor testClass;

    @Mock
    private IJwtTokenBuilder<ApplicationUserDetails> jwtTokenBuilder;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Before
    public void init() {
        testClass = new JwtHeaderServerInterceptor(jwtTokenBuilder);
    }

    @Test
    public void shouldDoNothingOnPostHandle() {
        testClass.postHandle(request, response, null, null);
    }

    @Test
    public void shouldDoNothingAfterCompletion() {
        testClass.afterCompletion(request, response, null, null);
    }

    @Test
    public void shouldIgnoreRequestWithMissingJwtHeader() {
        when(request.getHeader(JwtConstants.JWT_HEADER_INTERNAL)).thenReturn(null);

        assertThat(testClass.preHandle(request, response, null), equalTo(true));
    }

    @Test
    public void shouldUpdateRequestWhenHeaderFound() {
        when(request.getHeader(JwtConstants.JWT_HEADER_INTERNAL)).thenReturn(JWT_HEADER_VALUE);

        when(jwtTokenBuilder.getSubjectFromJwtToken(JWT_HEADER_VALUE,
                                                    ApplicationUserDetails.class)).thenReturn(AdminApiTestData.APPLICATION_USER_DETAILS);

        assertThat(testClass.preHandle(request, response, null), equalTo(true));

        verify(request).setAttribute(JwtConstants.JWT_HEADER_INTERNAL,
                                     AdminApiTestData.APPLICATION_USER_DETAILS);
    }

}
