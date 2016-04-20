package com.vennetics.bell.sam.apigateway.security;

import com.vennetics.bell.sam.apigateway.utils.TestUtils;
import com.vennetics.bell.sam.model.admin.service.entities.cassandra.CassandraApplication;
import com.vennetics.bell.sam.model.admin.service.entities.cassandra.CassandraClient;
import com.vennetics.bell.sam.model.admin.service.entities.cassandra.CassandraRole;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;

/**
 * Test class for ApplicationUserDetailsService
 */
@RunWith(MockitoJUnitRunner.class)
public class ApplicationUserDetailsServiceTest {

    @InjectMocks
    @Autowired
    private ApplicationUserDetailsService applicationUserDetailsService;

    @Mock
    private RestTemplate mockTemplate;

    @Test
    public void shouldLoadUserName() throws Exception {
        final String expectedUrlClient = String.format("http://%s/%s/%s",
                                                       ApplicationUserDetailsService.ADMIN_SERVER_NAME, CassandraClient.TABLE_NAME, TestUtils.TEST_CLIENT_ID);
        final String expectedUrlApplication = String.format("http://%s/%s/%s",
                                                            ApplicationUserDetailsService.ADMIN_SERVER_NAME, CassandraApplication.TABLE_NAME, TestUtils.TEST_APPLICATION_ID);

        CassandraClient cassandraClientReponse = TestUtils.createDummyClient();
        CassandraApplication cassandraApplicationResponse = TestUtils.createDummyApplication();
        when(mockTemplate.getForObject(expectedUrlClient, CassandraClient.class)).thenReturn(cassandraClientReponse);
        when(mockTemplate.getForObject(expectedUrlApplication, CassandraApplication.class)).thenReturn(cassandraApplicationResponse);

        UserDetails userDetails;
        userDetails = applicationUserDetailsService.loadUserByUsername("");
        assertNull(userDetails);
        ArrayList<String> roles = new ArrayList<>();
        roles.add(CassandraRole.SPRING_SEC_USER);
        roles.add(CassandraRole.SPRING_SEC_ADMIN);

        userDetails = applicationUserDetailsService.loadUserByUsername(TestUtils.TEST_CLIENT_ID);
        assertEquals(roles.toString(), userDetails.getAuthorities().toString());
        assertEquals(TestUtils.TEST_CLIENT_ID, userDetails.getUsername());
    }

}
