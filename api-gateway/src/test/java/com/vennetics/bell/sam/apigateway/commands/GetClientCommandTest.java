package com.vennetics.bell.sam.apigateway.commands;

import com.netflix.hystrix.exception.HystrixRuntimeException;
import com.vennetics.bell.sam.apigateway.utils.TestUtils;
import com.vennetics.bell.sam.model.admin.service.entities.cassandra.CassandraClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

/**
 * Test class for GetClientCommand
 */
@RunWith(MockitoJUnitRunner.class)
public class GetClientCommandTest {

    private static final Logger logger = LoggerFactory.getLogger(GetClientCommandTest.class);

    @Mock
    private RestTemplate mockTemplate;

    @Test
    public void shouldGetClient() throws Exception {
        final String expectedUrl = String.format("http://admin-server/clients/%s", TestUtils.TEST_CLIENT_ID);
        CassandraClient response = TestUtils.createDummyClient();
        when(mockTemplate.getForObject(expectedUrl, CassandraClient.class)).thenReturn(response);

        final GetClientCommand command = new GetClientCommand(TestUtils.TEST_CLIENT_ID, mockTemplate);
        assertThat(command.execute(), sameInstance(response));
    }

    @Test
    public void shouldThrowException() throws HttpClientErrorException {
        final String badUrl = String.format("http://admin-server/clients/nouser");
        HttpClientErrorException exception = new HttpClientErrorException(HttpStatus.BAD_REQUEST);
        when(mockTemplate.getForObject(badUrl, CassandraClient.class)).thenThrow(exception);
        final GetClientCommand command = new GetClientCommand("nouser", mockTemplate);

        try {
            command.execute();
        } catch (HystrixRuntimeException expectedError) {
            assertThat(expectedError.getCause(), is(exception));
        }
    }
}
