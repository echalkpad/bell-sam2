package com.vennetics.bell.sam.adapters.messaging.smpp.support.commands;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.cloudhopper.commons.util.windowing.WindowFuture;
import com.cloudhopper.smpp.SmppSession;
import com.cloudhopper.smpp.pdu.PduRequest;
import com.cloudhopper.smpp.pdu.PduResponse;
import com.cloudhopper.smpp.pdu.SubmitSm;
import com.cloudhopper.smpp.type.Address;
import com.netflix.hystrix.exception.HystrixBadRequestException;
import com.vennetics.bell.sam.adapters.messaging.smpp.support.TestWindowFuture;
import com.vennetics.bell.sam.adapters.messaging.smpp.support.address.IAddressFormatter;
import com.vennetics.bell.sam.adapters.messaging.smpp.support.connection.ISmppConnectionHandler;
import com.vennetics.bell.sam.adapters.messaging.smpp.throttle.ITokenBucket;
import com.vennetics.bell.sam.error.exceptions.InvalidAddressException;

@RunWith(MockitoJUnitRunner.class)
public class SendTextSubmitSmRequestCommandTest {

    // Object under test
    private SendTextSubmitSmRequestCommand objectUnderTest;

    // Mock Objects
    @Mock
    private ISmppConnectionHandler mockConnectionHandler;

    @Mock
    private SmppSession mockSmppSession;

    @Mock
    private IAddressFormatter mockAddressFormatter;
    
    @Mock
    private ITokenBucket mockTokenBucket;

    // Concrete Objects
    private static final String DESTINATION_ADDRESS = "destinationAddress";

    private static final String SOURCE_ADDRESS = "sourceAddress";

    private final String messageText = "Test Message";

    private SubmitSm pduRequest;

    private int seqNum = 123;

    @Before
    public void setUp() throws Exception {
        pduRequest = createDefaultSubmitSmRequest();
        objectUnderTest = new SendTextSubmitSmRequestCommand(mockConnectionHandler,
                                                             SOURCE_ADDRESS,
                                                             DESTINATION_ADDRESS,
                                                             messageText,
                                                             mockAddressFormatter);
    }

    @Test
    public void shouldSendRequest() throws Exception {
        final WindowFuture<Integer, PduRequest, PduResponse> defaultResponse = new TestWindowFuture(true,
                                                                                                    true,
                                                                                                    pduRequest);
        when(mockConnectionHandler.getSmppSession()).thenReturn(mockSmppSession);
        when(mockAddressFormatter.formatSourceAddress(SOURCE_ADDRESS)).thenReturn(pduRequest.getSourceAddress());
        when(mockAddressFormatter.formatDestinationAddress(DESTINATION_ADDRESS)).thenReturn(pduRequest.getDestAddress());
        when(mockConnectionHandler.getRateLimitingTokenBucket()).thenReturn(mockTokenBucket);
        when(mockTokenBucket.acquirePermit(anyLong(), anyObject())).thenReturn(true);
        when(mockSmppSession.sendRequestPdu(isA(PduRequest.class),
                                            anyLong(),
                                            anyBoolean())).thenReturn(defaultResponse);
        assertTrue(objectUnderTest.observe()
                                  .toBlocking()
                                  .single()
                                  .getSmppSequenceNumber() == seqNum);
    }

    @Test
    public void shouldThrowBadRequestExceptionForInvalidNumber() throws Exception {
        when(mockAddressFormatter.formatSourceAddress(SOURCE_ADDRESS)).thenReturn(new Address());
        when(mockAddressFormatter.formatDestinationAddress(DESTINATION_ADDRESS)).thenThrow(new InvalidAddressException(SOURCE_ADDRESS));
        try {
            objectUnderTest.observe().toBlocking().single();
            fail("Should have thrown a Hystrix Bad Request");
        } catch (final HystrixBadRequestException hbre) {
            assertTrue(hbre.getCause() instanceof InvalidAddressException);
        }
    }

    private SubmitSm createDefaultSubmitSmRequest() throws Exception {
        final SubmitSm request = new SubmitSm();
        request.setSourceAddress(new Address((byte) 0x03, (byte) 0x00, SOURCE_ADDRESS));
        request.setDestAddress(new Address((byte) 0x01, (byte) 0x01, DESTINATION_ADDRESS));
        request.setShortMessage(messageText.getBytes());
        request.setSequenceNumber(seqNum);
        return request;
    }

}
