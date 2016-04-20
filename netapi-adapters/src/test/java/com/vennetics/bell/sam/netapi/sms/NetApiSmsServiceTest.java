package com.vennetics.bell.sam.netapi.sms;

import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.when;

import com.vennetics.bell.sam.netapi.config.NetApiAdaptersConfig;
import generated.oma.xml.rest.netapi.sms._1.OutboundSMSMessageRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@RunWith(MockitoJUnitRunner.class)
public class NetApiSmsServiceTest {

    @InjectMocks
    @Autowired
    private SmsService service;

    @Mock private NetApiAdaptersConfig mockTemplateFactory;
    @Mock private RestTemplate mockTemplate;


    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
        when(mockTemplateFactory.netapiRestTemplate()).thenReturn(mockTemplate);
        service.afterPropertiesSet();
    }

    @Test
    public void shouldSendMessage() throws Exception {
        final String expectedUrl = "http://netapi-sms-enabler/outbound/SENDER/requests";

        final OutboundSMSMessageRequest dummyRequest = new OutboundSMSMessageRequest();
        final ResponseEntity<OutboundSMSMessageRequest> dummyResponse =
                new ResponseEntity<>(new OutboundSMSMessageRequest(), HttpStatus.CREATED);

        when(mockTemplate.postForEntity(eq(expectedUrl), isA(HttpEntity.class), same(OutboundSMSMessageRequest.class))).thenReturn(dummyResponse);

        final ResponseEntity<OutboundSMSMessageRequest> response = service.sendMessage("netapi-sms-enabler", "SENDER", dummyRequest);

        assertThat(response, is(not(nullValue())));
        assertThat(response, is(sameInstance(dummyResponse)));
    }
}
