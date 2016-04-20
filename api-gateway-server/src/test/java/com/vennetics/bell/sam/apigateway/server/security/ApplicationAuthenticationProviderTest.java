package com.vennetics.bell.sam.apigateway.server.security;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.vennetics.bell.sam.apigateway.server.commands.GetUserDetailsCommand;
import com.vennetics.microservices.common.api.model.admin.AdminApiModelConstants;
import com.vennetics.microservices.common.api.model.admin.util.ApplicationUserDetailsBuilder;
import com.vennetics.microservices.common.api.model.admin.v1.application.ApplicationAllowedService;
import com.vennetics.microservices.common.api.model.admin.v1.application.ApplicationUserDetails;

@RunWith(MockitoJUnitRunner.class)
public class ApplicationAuthenticationProviderTest {

    private static final String ROLE_ADMIN = "admin";

    private static final String ROLE_USER = "user";

    private static final String STATE_INACTIVE = "INACTIVE";

    private static final String SECRET = "secret";

    private static final String STATE_ACTIVE = "ACTIVE";

    private static final String ENCODED_SECRET = "encodedSecret";

    private static final String CLIENT_ID = "clientId";

    @Mock
    private ICachedPasswordChecker passwordChecker;

    @Mock
    private RestTemplate restTemplate;

    private ApplicationAuthenticationProvider testClass;

    @Before
    public void init() {
        GetUserDetailsCommand.CACHE.invalidateAll();
        testClass = new ApplicationAuthenticationProvider(passwordChecker, restTemplate);
    }

    @Test(expected = AuthenticationCredentialsNotFoundException.class)
    public void shouldRejectMissingClientId() {

        final Authentication authentication = new UsernamePasswordAuthenticationToken(null, null);
        testClass.authenticate(authentication);
    }

    @Test(expected = BadCredentialsException.class)
    public void shouldRejectMissingCredential() {

        final Authentication authentication = new UsernamePasswordAuthenticationToken(CLIENT_ID,
                                                                                      null);
        testClass.authenticate(authentication);
    }

    @Test
    public void shouldReturnNullOnNotFound() {

        when(restTemplate.getForEntity("http://admin-server"
                        + AdminApiModelConstants.PATH_USERDETAILS + "/clientId",
                                       ApplicationUserDetails.class)).thenReturn(new ResponseEntity<>(null,
                                                                                                      HttpStatus.OK));

        final Authentication authentication = new UsernamePasswordAuthenticationToken(CLIENT_ID,
                                                                                      SECRET);
        assertNull(testClass.authenticate(authentication));
    }

    @Test(expected = BadCredentialsException.class)
    public void shouldRejectInvalidPassword() {

        final ApplicationUserDetails userDetails = new ApplicationUserDetailsBuilder(CLIENT_ID,
                                                                                     ENCODED_SECRET,
                                                                                     STATE_ACTIVE).build();

        when(restTemplate.getForEntity("http://admin-server"
                        + AdminApiModelConstants.PATH_USERDETAILS + "/clientId",
                                       ApplicationUserDetails.class)).thenReturn(new ResponseEntity<>(userDetails,
                                                                                                      HttpStatus.OK));

        when(passwordChecker.matches(SECRET, ENCODED_SECRET)).thenReturn(false);

        final Authentication authentication = new UsernamePasswordAuthenticationToken(CLIENT_ID,
                                                                                      SECRET);
        testClass.authenticate(authentication);
    }

    @Test(expected = DisabledException.class)
    public void shouldRejectInactiveAccount() {

        final ApplicationUserDetails userDetails = new ApplicationUserDetailsBuilder(CLIENT_ID,
                                                                                     ENCODED_SECRET,
                                                                                     STATE_INACTIVE).build();

        when(restTemplate.getForEntity("http://admin-server"
                        + AdminApiModelConstants.PATH_USERDETAILS + "/clientId",
                                       ApplicationUserDetails.class)).thenReturn(new ResponseEntity<>(userDetails,
                                                                                                      HttpStatus.OK));

        when(passwordChecker.matches(SECRET, ENCODED_SECRET)).thenReturn(true);

        final Authentication authentication = new UsernamePasswordAuthenticationToken(CLIENT_ID,
                                                                                      SECRET);
        testClass.authenticate(authentication);
    }

    @Test
    public void shouldSupportTokenWithNoRolesAndAllowedServices() {

        final ApplicationUserDetails userDetails = new ApplicationUserDetailsBuilder(CLIENT_ID,
                                                                                     ENCODED_SECRET,
                                                                                     STATE_ACTIVE).build();

        when(restTemplate.getForEntity("http://admin-server"
                        + AdminApiModelConstants.PATH_USERDETAILS + "/clientId",
                                       ApplicationUserDetails.class)).thenReturn(new ResponseEntity<>(userDetails,
                                                                                                      HttpStatus.OK));

        when(passwordChecker.matches(SECRET, ENCODED_SECRET)).thenReturn(true);

        final Authentication authentication = new UsernamePasswordAuthenticationToken(CLIENT_ID,
                                                                                      SECRET);

        final Authentication result = testClass.authenticate(authentication);

        assertThat(result, not(nullValue()));
        assertThat(result.getPrincipal(), equalTo(CLIENT_ID));
        assertThat(result.getCredentials(), equalTo(ENCODED_SECRET));
        assertThat(result.getDetails(), equalTo(userDetails));
        assertThat(result.getAuthorities(), empty());
    }

    @Test(expected = InternalAuthenticationServiceException.class)
    public void shouldHandleClientErrorsAsBadRequest() {

        when(restTemplate.getForEntity("http://admin-server"
                        + AdminApiModelConstants.PATH_USERDETAILS + "/clientId",
                                       ApplicationUserDetails.class)).thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        final Authentication authentication = new UsernamePasswordAuthenticationToken(CLIENT_ID,
                                                                                      SECRET);

        testClass.authenticate(authentication);

    }

    @Test(expected = InternalAuthenticationServiceException.class)
    public void shouldHandleServerErrorsAsBadRequest() {

        when(restTemplate.getForEntity("http://admin-server"
                        + AdminApiModelConstants.PATH_USERDETAILS + "/clientId",
                                       ApplicationUserDetails.class)).thenThrow(new HttpClientErrorException(HttpStatus.BAD_GATEWAY));

        final Authentication authentication = new UsernamePasswordAuthenticationToken(CLIENT_ID,
                                                                                      SECRET);

        testClass.authenticate(authentication);

    }

    @Test
    public void shouldMapRolesToAuthorities() {

        final ApplicationUserDetails userDetails = new ApplicationUserDetailsBuilder(CLIENT_ID,
                                                                                     ENCODED_SECRET,
                                                                                     STATE_ACTIVE).withRoles(ROLE_USER,
                                                                                                         ROLE_ADMIN)
                                                                                              .build();

        when(restTemplate.getForEntity("http://admin-server"
                        + AdminApiModelConstants.PATH_USERDETAILS + "/clientId",
                                       ApplicationUserDetails.class)).thenReturn(new ResponseEntity<>(userDetails,
                                                                                                      HttpStatus.OK));

        when(passwordChecker.matches(SECRET, ENCODED_SECRET)).thenReturn(true);

        final Authentication authentication = new UsernamePasswordAuthenticationToken(CLIENT_ID,
                                                                                      SECRET);

        final Authentication result = testClass.authenticate(authentication);

        // Upper case with ROLE prefix
        assertThat(result.getAuthorities(),
                   containsInAnyOrder(new SimpleGrantedAuthority("ROLE_USER"),
                                      new SimpleGrantedAuthority("ROLE_ADMIN")));
    }

    @Test
    public void shouldMapServicesToAuthorities() {

        final ApplicationUserDetails userDetails = new ApplicationUserDetailsBuilder(CLIENT_ID,
                                                                                     ENCODED_SECRET,
                                                                                     STATE_ACTIVE).withServices(new ApplicationAllowedService("subscriber-thirdparty-sdm-server",
                                                                                                                                              null))
                                                                                                  .build();

        when(restTemplate.getForEntity("http://admin-server"
                        + AdminApiModelConstants.PATH_USERDETAILS + "/clientId",
                                       ApplicationUserDetails.class)).thenReturn(new ResponseEntity<>(userDetails,
                                                                                                      HttpStatus.OK));

        when(passwordChecker.matches(SECRET, ENCODED_SECRET)).thenReturn(true);

        final Authentication authentication = new UsernamePasswordAuthenticationToken(CLIENT_ID,
                                                                                      SECRET);

        final Authentication result = testClass.authenticate(authentication);

        assertThat(result.getAuthorities(),
                   contains(new SimpleGrantedAuthority("SERVICE_subscriber-thirdparty-sdm-server")));
    }

    @Test
    public void shouldSupportUsernamePasswordTokens() {

        assertTrue(testClass.supports(UsernamePasswordAuthenticationToken.class));

        assertFalse(testClass.supports(AnonymousAuthenticationToken.class));
    }

}
