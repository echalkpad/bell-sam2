package com.vennetics.bell.sam.apigateway.server;

import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import javax.servlet.Filter;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;

import com.google.common.collect.ImmutableSet;
import com.vennetics.bell.sam.apigateway.server.commands.GetUserDetailsCommand;
import com.vennetics.bell.sam.apigateway.server.security.SecurityConfiguration;
import com.vennetics.bell.sam.registry.ServiceConstants;
import com.vennetics.bell.sam.rest.config.converters.JsonConverter;
import com.vennetics.microservices.common.api.model.admin.AdminApiModelConstants;
import com.vennetics.microservices.common.api.model.admin.util.ApplicationUserDetailsBuilder;
import com.vennetics.microservices.common.api.model.admin.v1.application.ApplicationUserDetails;

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(
                classes = { SecurityConfiguration.class, SecurityTestConfig.class,
                        JsonConverter.class })
public class ApiGatewaySecurityConfigTests {

    private static final ApplicationUserDetails APPLICATION_USER_DETAILS = new ApplicationUserDetailsBuilder("clientId",
                                                                                                             new BCryptPasswordEncoder().encode("secret"),
                                                                                                             "ACTIVE").withRoles(ImmutableSet.of("user"))
                                                                                                                      .build();

    private MockMvc mvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private Filter springSecurityFilterChain;

    @Autowired
    private RestTemplate gatewayAdapterRestTemplate;

    @Before
    public void setup() throws Exception { // NOPMD

        reset(gatewayAdapterRestTemplate);
        GetUserDetailsCommand.CACHE.invalidateAll();
        GetUserDetailsCommand.CACHE.cleanUp();
        mvc = MockMvcBuilders.webAppContextSetup(context)
                             .addFilters(springSecurityFilterChain)
                             .build();

    }

    @Test
    public void shouldAllowBellSubAccessWithCorrectCredentials() throws Exception { // NOPMD

        when(gatewayAdapterRestTemplate.getForEntity("http://admin-server"
                        + AdminApiModelConstants.PATH_USERDETAILS
                        + "/clientId", ApplicationUserDetails.class)).thenReturn(new ResponseEntity<>(APPLICATION_USER_DETAILS,
                                                                                                      HttpStatus.OK));

        mvc.perform(get(ServiceConstants.BELL_SUBSCRIBER_URL + "/extraPath")
                                                                            .with(httpBasic("clientId",
                                                                                            "secret")))
           .andDo(print())
           .andExpect(status().isOk());
    }

    @Test
    public void shouldFailWithWrongPassword() throws Exception { // NOPMD

        when(gatewayAdapterRestTemplate.getForEntity("http://admin-server"
                        + AdminApiModelConstants.PATH_USERDETAILS
                        + "/clientId", ApplicationUserDetails.class)).thenReturn(new ResponseEntity<>(APPLICATION_USER_DETAILS,
                                                                                                      HttpStatus.OK));

        mvc.perform(get(ServiceConstants.BELL_SUBSCRIBER_URL + "/extraPath")
                                                                            .with(httpBasic("clientId",
                                                                                            "password")))
           .andDo(print())
           .andExpect(status().isUnauthorized());
    }

    @Test
    public void shouldNotAuthorizeWhenInternalAuthenticationErrorOccurs() throws Exception { // NOPMD

        when(gatewayAdapterRestTemplate.getForEntity("http://admin-server"
                        + AdminApiModelConstants.PATH_USERDETAILS + "/clientId",
                                                     ApplicationUserDetails.class)).thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        mvc.perform(get(ServiceConstants.BELL_SUBSCRIBER_URL + "/extraPath")
                                                                            .with(httpBasic("clientId",
                                                                                            "password")))
           .andDo(print())
           .andExpect(status().isUnauthorized());
    }

    @Test
    public void shouldNotAuthorizeWhenInternalSevereAuthenticationErrorOccurs() throws Exception { // NOPMD

        when(gatewayAdapterRestTemplate.getForEntity("http://admin-server"
                        + AdminApiModelConstants.PATH_USERDETAILS + "/clientId",
                                                     ApplicationUserDetails.class)).thenThrow(new HttpClientErrorException(HttpStatus.BAD_GATEWAY));

        mvc.perform(get(ServiceConstants.BELL_SUBSCRIBER_URL + "/extraPath")
                                                                            .with(httpBasic("clientId",
                                                                                            "password")))
           .andDo(print())
           .andExpect(status().isUnauthorized());
    }
}
