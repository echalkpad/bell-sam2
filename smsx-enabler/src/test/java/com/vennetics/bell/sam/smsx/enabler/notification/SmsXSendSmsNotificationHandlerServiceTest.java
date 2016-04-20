package com.vennetics.bell.sam.smsx.enabler.notification;

import com.vennetics.bell.sam.adapters.messaging.smpp.ISmppAdapter;
import com.vennetics.bell.sam.sms.common.dao.SmsMessage;
import com.vennetics.bell.sam.sms.common.dao.SmsMessageRepository;
import com.vennetics.bell.sam.sms.common.notification.AbstractSendNotificationCommand;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.web.client.RestOperations;
import rx.subjects.SerializedSubject;

import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class SmsXSendSmsNotificationHandlerServiceTest {

    // Object under test
    private SmsXSendSmsNotificationHandlerService objectUnderTest;

    // Mock Objects
    @Mock
    private SmsMessageRepository mockMessageRepository;
    @Mock
    private ISmppAdapter mockSmppAdapter;
    @Mock
    private RestOperations mockRestTemplate;

    // Concrete Objects
    @SuppressWarnings("rawtypes")
    private SerializedSubject inboundMessageBus;

    private int sequenceNumber = 1234;

    private static final String DELIVERY_RECEIPT_URL = "http://example.com";

    private static final String CALLBACK_DATA = "test";

    private static final String MESSAGE_ID = "messageId";

    @Before
    public void setUp() {
        objectUnderTest = new SmsXSendSmsNotificationHandlerService(mockMessageRepository,
                                                                    mockSmppAdapter,
                                                                    mockRestTemplate);
    }

    @Test
    public void shouldConstructExpectedCommandInstance() throws Exception {
        final AbstractSendNotificationCommand result = objectUnderTest
                        .createCommand(new SmsMessage());
        assertTrue(result instanceof SmsXSendNotificationCommand);
    }

}
