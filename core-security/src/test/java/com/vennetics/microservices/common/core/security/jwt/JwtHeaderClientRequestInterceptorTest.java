package com.vennetics.microservices.common.core.security.jwt;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import com.vennetics.microservices.common.api.model.admin.util.AdminApiTestData;
import com.vennetics.microservices.common.api.model.admin.v1.application.ApplicationUserDetails;

@RunWith(MockitoJUnitRunner.class)
public class JwtHeaderClientRequestInterceptorTest {

    private static final String TEST_JWT_VALUE = "TEST-JWT-VALUE";

    private static final byte[] BODY = new byte[0];

    @Mock
    private IJwtTokenBuilder<ApplicationUserDetails> jwtTokenBuilder;

    @Mock
    private ClientHttpRequestExecution execution;

    @Mock
    private HttpRequest request;

    @Mock
    private ClientHttpResponse response;

    private JwtHeaderClientRequestInterceptor testClass;

    @Before
    public void init() {
        testClass = new JwtHeaderClientRequestInterceptor(jwtTokenBuilder);
    }

    @Test
    public void shouldIgnoreRequestsWithNoAuthContext() throws IOException {

        when(execution.execute(request, BODY)).thenReturn(response);

        final ClientHttpResponse result = testClass.intercept(request, BODY, execution);

        assertEquals(response, result);
    }

    @Test
    public void shouldIgnoreRequestWithNullContext() throws IOException {

        final SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);

        when(execution.execute(request, BODY)).thenReturn(response);
        when(securityContext.getAuthentication()).thenReturn(null);

        final ClientHttpResponse result = testClass.intercept(request, BODY, execution);

        assertEquals(response, result);
    }

    @Test
    public void shouldIgnoreRequestUnsupportedAuthenticationToken() throws IOException {

        final SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);

        when(execution.execute(request, BODY)).thenReturn(response);
        when(securityContext.getAuthentication()).thenReturn(new UsernamePasswordAuthenticationToken(null,
                                                                                                     null));

        final ClientHttpResponse result = testClass.intercept(request, BODY, execution);

        assertEquals(response, result);
    }

    @Test
    public void shouldCreateJwtTokenHeaderWhenAuthenticationIsCorrect() throws IOException {

        final UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(null,
                                                                                                           null);
        authentication.setDetails(AdminApiTestData.APPLICATION_USER_DETAILS);

        final SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);

        final HttpHeaders headers = mock(HttpHeaders.class);

        when(execution.execute(request, BODY)).thenReturn(response);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(jwtTokenBuilder.createJwtTokenFromSubject(AdminApiTestData.APPLICATION_USER_DETAILS)).thenReturn(TEST_JWT_VALUE);
        when(request.getHeaders()).thenReturn(headers);

        final ClientHttpResponse result = testClass.intercept(request, BODY, execution);

        assertEquals(response, result);

        verify(headers).add(JwtConstants.JWT_HEADER_INTERNAL, TEST_JWT_VALUE);
    }

}
