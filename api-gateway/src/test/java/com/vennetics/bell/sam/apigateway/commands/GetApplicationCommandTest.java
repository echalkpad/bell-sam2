package com.vennetics.bell.sam.apigateway.commands;

import com.vennetics.bell.sam.apigateway.utils.TestUtils;
import com.vennetics.bell.sam.model.admin.service.entities.cassandra.CassandraApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;

import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

/**
 * Test class for GetApplicationCommand
 */
@RunWith(MockitoJUnitRunner.class)
public class GetApplicationCommandTest {
    private static final Logger logger = LoggerFactory.getLogger(GetApplicationCommandTest.class);

    @Mock
    private RestTemplate mockTemplate;

    @Test
    public void shouldGetApplication() throws Exception {
        final String expectedUrl = String.format("http://admin-server/applications/%s", TestUtils.TEST_APPLICATION_ID);

        logger.debug(expectedUrl);
        CassandraApplication response = TestUtils.createDummyApplication();
        when(mockTemplate.getForObject(expectedUrl, CassandraApplication.class)).thenReturn(response);

        final GetApplicationCommand command = new GetApplicationCommand(TestUtils.TEST_APPLICATION_ID, mockTemplate);

        assertThat(command.execute(), sameInstance(response));
    }
}
