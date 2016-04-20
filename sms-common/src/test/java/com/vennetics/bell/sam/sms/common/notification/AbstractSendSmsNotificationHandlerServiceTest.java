package com.vennetics.bell.sam.sms.common.notification;

import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

import com.vennetics.bell.sam.adapters.messaging.smpp.DeliverySuccessfulNotification;
import com.vennetics.bell.sam.adapters.messaging.smpp.ISmppAdapter;
import com.vennetics.bell.sam.adapters.messaging.smpp.SmppSendEvent;
import com.vennetics.bell.sam.adapters.messaging.smpp.SubmitSmResponse;
import com.vennetics.bell.sam.adapters.messaging.smpp.SubmitSmTimeout;
import com.vennetics.bell.sam.sms.common.dao.NotificationFormat;
import com.vennetics.bell.sam.sms.common.dao.SmsCallbackData;
import com.vennetics.bell.sam.sms.common.dao.SmsMessage;
import com.vennetics.bell.sam.sms.common.dao.SmsMessageRepository;

import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;

@RunWith(MockitoJUnitRunner.class)
public class AbstractSendSmsNotificationHandlerServiceTest {

    // Object under test
    private AbstractSendSmsNotificationHandlerService objectUnderTest;

    // Mock Objects
    @Mock
    private SmsMessageRepository mockMessageRepository;
    @Mock
    private ISmppAdapter mockSmppAdapter;
    //    @Mock private RestTemplateFactory mockTemplateFactory;
    @Mock
    private RestTemplate mockRestTemplate;

    // Concrete Objects
    @SuppressWarnings("rawtypes")
    private SerializedSubject inboundMessageBus;

    private final int sequenceNumber = 1234;

    private static final String DELIVERY_RECEIPT_URL = "http://example.com";

    private static final String CALLBACK_DATA = "test";

    private static final String MESSAGE_ID = "messageId";

    @Before
    public void setUp() {
        objectUnderTest = new TestSendSmsNotificationHandlerService(mockMessageRepository,
                                                                    mockSmppAdapter,
                                                                    mockRestTemplate);
        inboundMessageBus = new SerializedSubject<>(PublishSubject.create());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldNotifyRemoteClientOfDeliveredSms() throws Exception {
        when(mockSmppAdapter.getFilteredEventBus(SmppSendEvent.class)).thenReturn(inboundMessageBus);
        final List<SmsMessage> messageList = new ArrayList<>();
        final SmsMessage smsMessage = createDefaultSmsMessage();
        messageList.add(smsMessage);
        when(mockMessageRepository.findByMessageId(MESSAGE_ID)).thenReturn(messageList);
        when(mockMessageRepository.save(smsMessage)).thenReturn(smsMessage);
        when(mockRestTemplate.postForEntity(eq(DELIVERY_RECEIPT_URL),
                                            isA(HttpEntity.class),
                                            eq(String.class))).thenReturn(new ResponseEntity<>(
                        HttpStatus.NO_CONTENT));
        objectUnderTest.subscribeForEvents();
        final DeliverySuccessfulNotification event = new DeliverySuccessfulNotification(MESSAGE_ID);
        event.setSequenceNumber(sequenceNumber);
        inboundMessageBus.onNext(event);

        // Verify the expected interactions have occured.
        verify(mockSmppAdapter).getFilteredEventBus(SmppSendEvent.class);
        verify(mockMessageRepository).findByMessageId(MESSAGE_ID);
        verify(mockMessageRepository).save(eq(smsMessage));
        verify(mockRestTemplate, timeout(1000)).postForEntity(eq(
                        DELIVERY_RECEIPT_URL + "?correlator=" + CALLBACK_DATA),
                                                              isA(HttpEntity.class),
                                                              eq(String.class));
        verifyNoMoreInteractions(mockMessageRepository, mockRestTemplate, mockSmppAdapter);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldNotifyRemoteClientOfFailedSms() throws Exception {
        when(mockSmppAdapter.getFilteredEventBus(SmppSendEvent.class)).thenReturn(inboundMessageBus);
        final List<SmsMessage> messageList = new ArrayList<>();
        final SmsMessage smsMessage = createDefaultSmsMessage();
        messageList.add(smsMessage);
        when(mockMessageRepository.findBySequenceNumber(sequenceNumber)).thenReturn(messageList);
        when(mockMessageRepository.save(smsMessage)).thenReturn(smsMessage);
        when(mockRestTemplate.postForEntity(eq(DELIVERY_RECEIPT_URL),
                                            isA(HttpEntity.class),
                                            eq(String.class))).thenReturn(new ResponseEntity<>(
                        HttpStatus.NO_CONTENT));
        objectUnderTest.subscribeForEvents();
        final SubmitSmTimeout event = new SubmitSmTimeout();
        event.setSequenceNumber(sequenceNumber);
        inboundMessageBus.onNext(event);

        // Verify the expected interactions have occured.
        verify(mockSmppAdapter).getFilteredEventBus(SmppSendEvent.class);
        verify(mockMessageRepository).findBySequenceNumber(sequenceNumber);
        verify(mockMessageRepository).save(eq(smsMessage));
        verify(mockRestTemplate, timeout(1000)).postForEntity(eq(
                        DELIVERY_RECEIPT_URL + "?correlator=" + CALLBACK_DATA),
                                                              isA(HttpEntity.class),
                                                              eq(String.class));
        verifyNoMoreInteractions(mockMessageRepository, mockRestTemplate, mockSmppAdapter);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldNotifyRemoteClientWithJsonOfFailedSms() throws Exception {
        when(mockSmppAdapter.getFilteredEventBus(SmppSendEvent.class)).thenReturn(inboundMessageBus);
        final List<SmsMessage> messageList = new ArrayList<>();
        final SmsMessage smsMessage = createDefaultSmsMessage();
        smsMessage.getSmsCallbackData().setNotificationFormat(NotificationFormat.JSON);
        messageList.add(smsMessage);
        when(mockMessageRepository.findBySequenceNumber(sequenceNumber)).thenReturn(messageList);
        when(mockMessageRepository.save(smsMessage)).thenReturn(smsMessage);
        when(mockRestTemplate.postForEntity(eq(DELIVERY_RECEIPT_URL),
                                            isA(HttpEntity.class),
                                            eq(String.class))).thenReturn(new ResponseEntity<>(
                        HttpStatus.NO_CONTENT));
        objectUnderTest.subscribeForEvents();
        final SubmitSmTimeout event = new SubmitSmTimeout();
        event.setSequenceNumber(sequenceNumber);
        inboundMessageBus.onNext(event);

        // Verify the expected interactions have occured.
        verify(mockSmppAdapter).getFilteredEventBus(SmppSendEvent.class);
        verify(mockMessageRepository).findBySequenceNumber(sequenceNumber);
        verify(mockMessageRepository).save(eq(smsMessage));
        verify(mockRestTemplate, timeout(1000)).postForEntity(eq(
                        DELIVERY_RECEIPT_URL + "?correlator=" + CALLBACK_DATA),
                                                              isA(HttpEntity.class),
                                                              eq(String.class));
        verifyNoMoreInteractions(mockMessageRepository, mockRestTemplate, mockSmppAdapter);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldNotNotifyRemoteClientOfSubmitSms() throws Exception {
        when(mockSmppAdapter.getFilteredEventBus(SmppSendEvent.class)).thenReturn(inboundMessageBus);
        final List<SmsMessage> messageList = new ArrayList<>();
        final SmsMessage smsMessage = createDefaultSmsMessage();
        messageList.add(smsMessage);
        when(mockMessageRepository.findBySequenceNumber(sequenceNumber)).thenReturn(messageList);
        when(mockMessageRepository.save(smsMessage)).thenReturn(smsMessage);
        objectUnderTest.subscribeForEvents();
        final SubmitSmResponse event = new SubmitSmResponse(MESSAGE_ID);
        event.setSequenceNumber(sequenceNumber);
        inboundMessageBus.onNext(event);

        // Verify the expected interactions have occured.
        verify(mockSmppAdapter).getFilteredEventBus(SmppSendEvent.class);
        verify(mockMessageRepository).findBySequenceNumber(sequenceNumber);
        verify(mockMessageRepository).save(eq(smsMessage));
        verifyNoMoreInteractions(mockMessageRepository, mockSmppAdapter);
        verifyZeroInteractions(mockRestTemplate);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldNotNotifyIfNoNotificationDataPresent() throws Exception {
        when(mockSmppAdapter.getFilteredEventBus(SmppSendEvent.class)).thenReturn(inboundMessageBus);
        final List<SmsMessage> messageList = new ArrayList<>();
        final SmsMessage smsMessage = new SmsMessage();
        smsMessage.setMessageId(MESSAGE_ID);
        messageList.add(smsMessage);
        when(mockMessageRepository.findByMessageId(MESSAGE_ID)).thenReturn(messageList);
        objectUnderTest.subscribeForEvents();
        final DeliverySuccessfulNotification event = new DeliverySuccessfulNotification(MESSAGE_ID);
        event.setSequenceNumber(sequenceNumber);
        inboundMessageBus.onNext(event);
        verify(mockSmppAdapter).getFilteredEventBus(SmppSendEvent.class);
        verify(mockMessageRepository).findByMessageId(MESSAGE_ID);
        verify(mockMessageRepository).save(eq(smsMessage));
        verifyNoMoreInteractions(mockMessageRepository, mockSmppAdapter);
        verifyZeroInteractions(mockRestTemplate);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldNotNotifyIfNoNotificationDataPresentButNoUrl() throws Exception {
        when(mockSmppAdapter.getFilteredEventBus(SmppSendEvent.class)).thenReturn(inboundMessageBus);
        final List<SmsMessage> messageList = new ArrayList<>();
        final SmsMessage smsMessage = new SmsMessage();
        final SmsCallbackData smsCallbackData = new SmsCallbackData();
        smsMessage.setSmsCallbackData(smsCallbackData);
        smsMessage.setMessageId(MESSAGE_ID);
        messageList.add(smsMessage);
        when(mockMessageRepository.findByMessageId(MESSAGE_ID)).thenReturn(messageList);
        objectUnderTest.subscribeForEvents();
        final DeliverySuccessfulNotification event = new DeliverySuccessfulNotification(MESSAGE_ID);
        event.setSequenceNumber(sequenceNumber);
        inboundMessageBus.onNext(event);
        verify(mockSmppAdapter).getFilteredEventBus(SmppSendEvent.class);
        verify(mockMessageRepository).findByMessageId(MESSAGE_ID);
        verify(mockMessageRepository).save(eq(smsMessage));
        verifyNoMoreInteractions(mockMessageRepository, mockSmppAdapter);
        verifyZeroInteractions(mockRestTemplate);
    }

    private SmsMessage createDefaultSmsMessage() {
        final SmsMessage smsMessage = new SmsMessage();
        final SmsCallbackData smsCallbackData = new SmsCallbackData();
        smsCallbackData.setCallbackData(CALLBACK_DATA);
        smsCallbackData.setDeliveryReceiptUrl(DELIVERY_RECEIPT_URL);
        smsMessage.setSmsCallbackData(smsCallbackData);
        smsMessage.setMessageId(MESSAGE_ID);
        return smsMessage;
    }

    private class TestSendSmsNotificationHandlerService
                    extends AbstractSendSmsNotificationHandlerService {

        public TestSendSmsNotificationHandlerService(final SmsMessageRepository messageRepository,
                                                     final ISmppAdapter smppAdapter,
                                                     final RestTemplate restTemplate) {
            super(messageRepository, smppAdapter, restTemplate);
        }

        @Override
        protected AbstractSendNotificationCommand createCommand(final SmsMessage smsMessage) {
            return new TestCommand("Test", "Test", smsMessage, getRestTemplate());
        }
    }

    private static class TestCommand extends AbstractSendNotificationCommand {

        /**
         * Sole constructor.
         *
         * @param commandGroupKey
         * @param commandNameKey
         * @param smsMessage      Details of the message to notify against
         * @param template        The {@link RestTemplate} to be used to address the service.
         */
        public TestCommand(final String commandGroupKey,
                           final String commandNameKey,
                           final SmsMessage smsMessage,
                           final RestOperations template) {
            super(commandGroupKey, commandNameKey, smsMessage, template);
        }

        @Override
        protected HttpEntity<?> buildHttpEntity() {
            return new HttpEntity<>(null, null);
        }

        @Override
        protected String buildNotificationEndpoint() {
            return DELIVERY_RECEIPT_URL + "?correlator=" + CALLBACK_DATA;
        }
    }

}

