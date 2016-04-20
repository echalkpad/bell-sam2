package com.vennetics.bell.sam.oneapi.rest.v091.commands;

import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.same;

import static org.mockito.Matchers.isA;

import com.vennetics.bell.sam.model.smsx.SendSmsResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.vennetics.bell.sam.oneapi.rest.v091.TestConfiguration;

@RunWith(MockitoJUnitRunner.class)
public class SendSmsCommandTest {

    private static final Logger LOG = LoggerFactory.getLogger(SendSmsCommandTest.class);

    @Mock private RestTemplate mockTemplate;

    @Test
    public void shouldSendMessage() throws Exception {
        final String expectedUrl = String.format("http://oneapi-sms-enabler/outbound/requests");
        LOG.debug(expectedUrl);
        MultiValueMap<String, String> map = TestConfiguration.createMap();
        ResponseEntity<SendSmsResponse> response = TestConfiguration.dummyResponse();
        when(mockTemplate.postForEntity(eq(expectedUrl), isA(HttpEntity.class), same(SendSmsResponse.class))).thenReturn(response);

        final SendSmsCommand command = new SendSmsCommand("oneapi-sms-enabler", map,  mockTemplate);

        assertThat(command.execute(), sameInstance(response));
    }
}
