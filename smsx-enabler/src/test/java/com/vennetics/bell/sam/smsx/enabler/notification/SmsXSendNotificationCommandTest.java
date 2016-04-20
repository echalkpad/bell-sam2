package com.vennetics.bell.sam.smsx.enabler.notification;

import com.vennetics.bell.sam.sms.common.dao.SmsCallbackData;
import com.vennetics.bell.sam.sms.common.dao.SmsDeliveryStatus;
import com.vennetics.bell.sam.sms.common.dao.SmsMessage;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestOperations;

import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * Created on 18/02/2016.
 */
@RunWith(MockitoJUnitRunner.class)
public class SmsXSendNotificationCommandTest {

    // Object under test
    private SmsXSendNotificationCommand objectUnderTest;

    // Mock Objects
    @Mock
    private RestOperations mockRestTemplate;

    // Concrete objects
    private SmsMessage smsMessage;
    private String correlator = "correlateThis";
    private byte[] message = new byte[0];
    private String destinationAddress = "123456";
    private UUID externalRequestId = UUID.randomUUID();
    private Integer sequenceNumber = new Integer(1);
    private String sourceAddress = "1234";
    private SmsCallbackData smsCallbackData;
    private String deliveryReceiptUrl = "http://bbc.co.uk";

    @Before public void setUp() throws Exception {
        smsCallbackData = new SmsCallbackData();
        smsCallbackData.setCallbackData(correlator);
        smsCallbackData.setDeliveryReceiptUrl(deliveryReceiptUrl);
        smsMessage = new SmsMessage();
        smsMessage.setDeliveryStatus(SmsDeliveryStatus.DELIVERY_IMPOSSIBLE);
        smsMessage.setBinary(false);
        smsMessage.setClientCorrelator(correlator);
        smsMessage.setContent(message);
        smsMessage.setDestinationAddress(destinationAddress);
        smsMessage.setExternalRequestId(externalRequestId);
        smsMessage.setSequenceNumber(sequenceNumber);
        smsMessage.setSourceAddress(sourceAddress);
        smsMessage.setSmsCallbackData(smsCallbackData);
        objectUnderTest = new SmsXSendNotificationCommand(smsMessage, mockRestTemplate);

    }

    @Test public void shouldBuildHttpEntityMatchingSmsMessage() throws Exception {
        final HttpEntity<?> result = objectUnderTest.buildHttpEntity();
        assertThat(result.getBody() instanceof DeliveryStatus, is(true));
        final DeliveryStatus body = (DeliveryStatus) result.getBody();
        assertThat(body.getMessages().size(), is(1));
        assertThat(body.getMessages().get(0).getAddress(), is(destinationAddress));
        assertThat(body.getMessages().get(0).getStatus(), is(SmsDeliveryStatus.DELIVERY_IMPOSSIBLE.value()));
        assertThat(result.getHeaders().size(), is(2));
        assertThat(result.getHeaders().getContentType(), is(MediaType.APPLICATION_XML));
        assertThat(result.getHeaders().getAccept().size(), is(1));
        assertThat(result.getHeaders().getAccept().get(0), is(MediaType.APPLICATION_XML));
    }

    @Test public void shouldBuildNotificationEndpointWithCorrelator() throws Exception {
        final String result = objectUnderTest.buildNotificationEndpoint();
        assertThat(result, is(deliveryReceiptUrl + "?correlator=" + correlator));
    }

    @Test public void shouldBuildNotificationEndpointWithCorrelatorAppended() throws Exception {
        smsCallbackData.setDeliveryReceiptUrl(deliveryReceiptUrl + "?test=data");
        smsMessage.setSmsCallbackData(smsCallbackData);
        objectUnderTest = new SmsXSendNotificationCommand(smsMessage, mockRestTemplate);
        final String result = objectUnderTest.buildNotificationEndpoint();
        assertThat(result, is(deliveryReceiptUrl + "?test=data&correlator=" + correlator));
    }

    @Test public void shouldBuildNotificationEndpointWithoutCorrelator() throws Exception {
        smsCallbackData.setCallbackData(null);
        smsMessage.setSmsCallbackData(smsCallbackData);
        objectUnderTest = new SmsXSendNotificationCommand(smsMessage, mockRestTemplate);
        final String result = objectUnderTest.buildNotificationEndpoint();
        assertThat(result, is(deliveryReceiptUrl));
    }
}
