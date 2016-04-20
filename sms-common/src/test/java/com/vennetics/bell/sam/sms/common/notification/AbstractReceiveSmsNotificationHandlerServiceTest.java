package com.vennetics.bell.sam.sms.common.notification;

import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.vennetics.bell.sam.adapters.messaging.smpp.DeliverSmNotification;
import com.vennetics.bell.sam.adapters.messaging.smpp.DeliveryFailedNotification;
import com.vennetics.bell.sam.adapters.messaging.smpp.ISmppAdapter;
import com.vennetics.bell.sam.sms.common.dao.SmsNotification;
import com.vennetics.bell.sam.sms.common.dao.SmsNotificationRepository;
import com.vennetics.bell.sam.sms.common.dao.SmsNotificationState;

import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;

@RunWith(MockitoJUnitRunner.class)
public class AbstractReceiveSmsNotificationHandlerServiceTest {

    // Object under test
    private AbstractReceiveSmsNotificationHandlerService objectUnderTest;

    // Mock Objects
    @Mock
    private SmsNotificationRepository mockNotificationRepository;
    @Mock
    private ISmppAdapter mockSmppAdapter;

    // Concrete Objects
    @SuppressWarnings("rawtypes")
    private SerializedSubject inboundMessageBus;

    private final int sequenceNumber = 1234;

    private static final String DEST_ADDRESS = "1234567";

    private static final String SOURCE_ADDRESS = "7654321";

    private static final String MESSAGE_CONTENT = "Hi there";

    @Before
    public void setUp() {
        objectUnderTest = new TestReceiveSmsNotificationHandlerService(mockNotificationRepository,
                                                                       mockSmppAdapter);
        inboundMessageBus = new SerializedSubject<>(PublishSubject.create());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldPersistIncomingNotification() throws Exception {
        when(mockSmppAdapter.getFilteredEventBus(DeliverSmNotification.class)).thenReturn(inboundMessageBus);
        final SmsNotification smsNotification = createDefaultSmsNotification();
        when(mockNotificationRepository.save(smsNotification)).thenReturn(smsNotification);
        objectUnderTest.subscribeForEvents();
        final DeliverSmNotification event = new DeliverSmNotification(MESSAGE_CONTENT,
                                                                      SOURCE_ADDRESS,
                                                                      DEST_ADDRESS);
        event.setSequenceNumber(sequenceNumber);
        inboundMessageBus.onNext(event);

        // Verify the expected interactions have occured.
        verify(mockSmppAdapter).getFilteredEventBus(DeliverSmNotification.class);
        verify(mockNotificationRepository).save(isA(SmsNotification.class));
        verifyNoMoreInteractions(mockNotificationRepository, mockSmppAdapter);
    }


    @SuppressWarnings("unchecked")
    @Test
    public void shouldNotPersistIncomingNotificationIfOtherNotificationType() throws Exception {
        when(mockSmppAdapter.getFilteredEventBus(DeliverSmNotification.class)).thenReturn(inboundMessageBus);
        objectUnderTest.subscribeForEvents();
        final DeliveryFailedNotification event = new DeliveryFailedNotification(MESSAGE_CONTENT);
        event.setSequenceNumber(sequenceNumber);
        inboundMessageBus.onNext(event);

        // Verify the expected interactions have occured.
        verify(mockSmppAdapter).getFilteredEventBus(DeliverSmNotification.class);
        verifyNoMoreInteractions(mockNotificationRepository, mockSmppAdapter);
    }

    private SmsNotification createDefaultSmsNotification() {
        final SmsNotification smsNotification = new SmsNotification();
        smsNotification.setSourceAddress(SOURCE_ADDRESS);
        smsNotification.setDestinationAddress(DEST_ADDRESS);
        smsNotification.setNotificationState(SmsNotificationState.RECEIVED_FROM_NETWORK);
        smsNotification.setBinary(false);
        smsNotification.setContent(MESSAGE_CONTENT.getBytes());
        return smsNotification;
    }

    private static class TestReceiveSmsNotificationHandlerService
                    extends AbstractReceiveSmsNotificationHandlerService {

        public TestReceiveSmsNotificationHandlerService(final SmsNotificationRepository notificationRepository,
                                                        final ISmppAdapter smppAdapter) {
            super(notificationRepository, smppAdapter);
        }
    }

}

