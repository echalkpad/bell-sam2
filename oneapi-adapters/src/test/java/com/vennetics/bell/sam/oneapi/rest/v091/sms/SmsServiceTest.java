package com.vennetics.bell.sam.oneapi.rest.v091.sms;

import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Matchers.isNull;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.when;

import com.vennetics.bell.sam.model.smsx.DeliveryStatusResponse;
import com.vennetics.bell.sam.model.smsx.SendSmsResponse;
import com.vennetics.bell.sam.oneapi.rest.v091.config.OneApiAdaptersConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.vennetics.bell.sam.oneapi.rest.v091.TestConfiguration;
import com.vennetics.bell.sam.oneapi.rest.v091.commands.GetSmsDeliveryStatusCommand;
import rx.Observable;

@RunWith(MockitoJUnitRunner.class)
public class SmsServiceTest {

    @InjectMocks
    @Autowired
    private SmsService service;

    @Mock private OneApiAdaptersConfig mockTemplateFactory;
    @Mock private RestTemplate mockTemplate;
    
    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
        when(mockTemplateFactory.oneapiRestTemplate()).thenReturn(mockTemplate);
        service.afterPropertiesSet();
    }

    @Test
    public void shouldSendSms() throws Exception {
        final String expectedUrl = "http://oneapi-sms-enabler/outbound/requests";
        final ResponseEntity<SendSmsResponse> dummyResponse = TestConfiguration.dummyResponse();
        final MultiValueMap<String, String> map = TestConfiguration.createMap();

        when(mockTemplate.postForEntity(eq(expectedUrl), isA(HttpEntity.class), same(SendSmsResponse.class))).thenReturn(dummyResponse);

        Observable<ResponseEntity<SendSmsResponse>> observable = service.sendSms(TestConfiguration.SERVICE,
                                                                               map);
        final ResponseEntity<SendSmsResponse> response = observable.toBlocking().single();
        assertThat(response, is(not(nullValue())));
        assertThat(response, is(sameInstance(dummyResponse)));
    }
    
    @Test
    public void shouldGetSmsDeliveryStatus() throws Exception {
        final String expectedUrl = String.format("http://oneapi-sms-enabler/outbound/requests?messageId=%s", TestConfiguration.MESSAGE_ID);
        final ResponseEntity<DeliveryStatusResponse> dummyResponse = TestConfiguration.buildDeliveryInfoResponse();

        when(mockTemplate.exchange(eq(expectedUrl),
                                   eq(HttpMethod.GET),
                                   isNull(HttpEntity.class),
                                   same(GetSmsDeliveryStatusCommand.DELIVERINFO_LIST_TYPE))).thenReturn(dummyResponse);

        final Observable<ResponseEntity<DeliveryStatusResponse>> observable = service.getSmsDeliveryStatus(
                        TestConfiguration.SERVICE,
                        TestConfiguration.MESSAGE_ID);
        final ResponseEntity<DeliveryStatusResponse> response = observable.toBlocking().single();
        assertThat(response, is(not(nullValue())));
        assertThat(response, is(sameInstance(dummyResponse)));
    }
}
