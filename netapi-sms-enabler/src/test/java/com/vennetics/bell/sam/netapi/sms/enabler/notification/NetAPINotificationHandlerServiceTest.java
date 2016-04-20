package com.vennetics.bell.sam.netapi.sms.enabler.notification;

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

import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class NetAPINotificationHandlerServiceTest {

    // Object under test
    private NetAPISendSmsNotificationHandlerService objectUnderTest;

    // Mock Objects
    @Mock
    private SmsMessageRepository mockMessageRepository;
    @Mock
    private ISmppAdapter mockSmppAdapter;
    @Mock
    private RestOperations mockRestTemplate;

    @Before
    public void setUp() {
        objectUnderTest = new NetAPISendSmsNotificationHandlerService(mockMessageRepository,
                                                                      mockSmppAdapter,
                                                                      mockRestTemplate);
    }

    @Test
    public void shouldConstructExpectedCommandInstance() throws Exception {
        final AbstractSendNotificationCommand result = objectUnderTest
                        .createCommand(new SmsMessage());
        assertTrue(result instanceof NetAPISendNotificationCommand);
    }

}
