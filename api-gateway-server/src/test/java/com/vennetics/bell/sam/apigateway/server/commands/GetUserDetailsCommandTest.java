package com.vennetics.bell.sam.apigateway.server.commands;

import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.client.RestTemplate;

import com.google.common.collect.ImmutableSet;
import com.vennetics.microservices.common.api.model.admin.AdminApiModelConstants;
import com.vennetics.microservices.common.api.model.admin.util.ApplicationUserDetailsBuilder;
import com.vennetics.microservices.common.api.model.admin.v1.application.ApplicationUserDetails;

@RunWith(MockitoJUnitRunner.class)
public class GetUserDetailsCommandTest {

    private static final String CLIENT_ID = "clientId";

    private static final ApplicationUserDetails APPLICATION_USER_DETAILS = new ApplicationUserDetailsBuilder(CLIENT_ID,
                                                                                                             new BCryptPasswordEncoder().encode("secret"),
                                                                                                             "ACTIVE").withRoles(ImmutableSet.of("ROLE_USER"))
                                                                                                                      .build();

    @Mock
    private RestTemplate restTemplate;

    @Before
    public void init() {
        GetUserDetailsCommand.CACHE.invalidateAll();
    }

    @Test
    public void shouldReturnQueryResultOnSuccess() {

        when(restTemplate.getForEntity("http://admin-server"
                        + AdminApiModelConstants.PATH_USERDETAILS + "/"
                        + CLIENT_ID, ApplicationUserDetails.class)).thenReturn(new ResponseEntity<>(APPLICATION_USER_DETAILS,
                                                                                                    HttpStatus.OK));

        final GetUserDetailsCommand command = new GetUserDetailsCommand(CLIENT_ID, restTemplate);

        final ResponseEntity<ApplicationUserDetails> result = command.execute();

        assertThat(result.getBody(), equalTo(APPLICATION_USER_DETAILS));
    }

    @Test
    public void shouldReturnNulls() {

        when(restTemplate.getForEntity("http://admin-server"
                        + AdminApiModelConstants.PATH_USERDETAILS + "/"
                        + CLIENT_ID, ApplicationUserDetails.class)).thenReturn(new ResponseEntity<>(null,
                                                                                                    HttpStatus.OK));

        final GetUserDetailsCommand command = new GetUserDetailsCommand(CLIENT_ID, restTemplate);

        final ResponseEntity<ApplicationUserDetails> result = command.execute();

        assertThat(result.getBody(), nullValue());
    }

    @Test
    public void shouldHitCacheOnMultipleInvocations() {

        when(restTemplate.getForEntity("http://admin-server"
                        + AdminApiModelConstants.PATH_USERDETAILS + "/"
                        + CLIENT_ID, ApplicationUserDetails.class)).thenReturn(new ResponseEntity<>(APPLICATION_USER_DETAILS,
                                                                                                    HttpStatus.OK));

        assertThat(new GetUserDetailsCommand(CLIENT_ID, restTemplate).execute().getBody(),
                   equalTo(APPLICATION_USER_DETAILS));
        assertThat(new GetUserDetailsCommand(CLIENT_ID, restTemplate).execute().getBody(),
                   equalTo(APPLICATION_USER_DETAILS));
        assertThat(new GetUserDetailsCommand(CLIENT_ID, restTemplate).execute().getBody(),
                   equalTo(APPLICATION_USER_DETAILS));
        assertThat(new GetUserDetailsCommand(CLIENT_ID, restTemplate).execute().getBody(),
                   equalTo(APPLICATION_USER_DETAILS));

        verify(restTemplate).getForEntity("http://admin-server"
                        + AdminApiModelConstants.PATH_USERDETAILS + "/" + CLIENT_ID,
                                                    ApplicationUserDetails.class);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldNotCacheNullResult() {

        when(restTemplate.getForEntity("http://admin-server"
                        + AdminApiModelConstants.PATH_USERDETAILS + "/"
                        + CLIENT_ID, ApplicationUserDetails.class)).thenReturn(new ResponseEntity<>(null,
                                                                                                    HttpStatus.OK),
                                                                               new ResponseEntity<>(null,
                                                                                                    HttpStatus.OK),
                                                                               new ResponseEntity<>(null,
                                                                                                    HttpStatus.OK),
                                                                               new ResponseEntity<>(null,
                                                                                                    HttpStatus.OK));

        assertThat(new GetUserDetailsCommand(CLIENT_ID, restTemplate).execute().getBody(),
                   nullValue());
        assertThat(new GetUserDetailsCommand(CLIENT_ID, restTemplate).execute().getBody(),
                   nullValue());
        assertThat(new GetUserDetailsCommand(CLIENT_ID, restTemplate).execute().getBody(),
                   nullValue());
        assertThat(new GetUserDetailsCommand(CLIENT_ID, restTemplate).execute().getBody(),
                   nullValue());

        verify(restTemplate, times(4)).getForEntity("http://admin-server"
                        + AdminApiModelConstants.PATH_USERDETAILS + "/" + CLIENT_ID,
                                                    ApplicationUserDetails.class);
    }

}
