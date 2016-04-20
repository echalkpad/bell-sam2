package com.vennetics.bell.sam.netapi.sms.commands;

import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.when;
import generated.oma.xml.rest.netapi.sms._1.OutboundSMSMessageRequest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@RunWith(MockitoJUnitRunner.class)
public class SendMessageCommandTest {

    @Mock private RestTemplate mockTemplate;

    @Mock private OutboundSMSMessageRequest mockRequest;

    @Test
    public void shouldSendMessage() throws Exception {
        final String expectedUrl = "http://netapi-sms-enabler/outbound/SENDER/requests";
        final ResponseEntity<OutboundSMSMessageRequest> dummyResponse = newOutboundSMSMessageRequest("myName");

        when(mockTemplate.postForEntity(eq(expectedUrl), isA(HttpEntity.class), same(OutboundSMSMessageRequest.class))).thenReturn(dummyResponse);

        final SendMessageCommand command = new SendMessageCommand("netapi-sms-enabler", "SENDER", mockRequest, mockTemplate);

        assertThat(command.observe().toBlocking().single(), sameInstance(dummyResponse));
    }

    private ResponseEntity<OutboundSMSMessageRequest> newOutboundSMSMessageRequest(final String name) {
        final OutboundSMSMessageRequest request = new OutboundSMSMessageRequest()
                .withSenderName(name);
        return new ResponseEntity<>(request, HttpStatus.CREATED);
    }
}
