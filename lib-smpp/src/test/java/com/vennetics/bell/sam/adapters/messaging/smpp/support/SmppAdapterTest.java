package com.vennetics.bell.sam.adapters.messaging.smpp.support;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.anyObject;

import com.vennetics.bell.sam.adapters.messaging.smpp.SmppInboundEvent;
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
import com.cloudhopper.smpp.type.RecoverablePduException;
import com.cloudhopper.smpp.type.SmppChannelException;
import com.cloudhopper.smpp.type.SmppTimeoutException;
import com.cloudhopper.smpp.type.UnrecoverablePduException;
import com.netflix.hystrix.exception.HystrixRuntimeException;
import com.vennetics.bell.sam.adapters.messaging.smpp.SequenceNumberPair;
import com.vennetics.bell.sam.adapters.messaging.smpp.exception.SendSmppException;
import com.vennetics.bell.sam.adapters.messaging.smpp.support.address.IAddressFormatter;
import com.vennetics.bell.sam.adapters.messaging.smpp.support.connection.ISmppConnectionHandler;
import com.vennetics.bell.sam.adapters.messaging.smpp.throttle.ITokenBucket;

import rx.Observable;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

@RunWith(MockitoJUnitRunner.class)
public class SmppAdapterTest {

    // Object under test
    private SmppAdapter objectUnderTest;

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
    private Subject<SmppInboundEvent, SmppInboundEvent> inboundMessageBus;

    private static final String DESTINATION_NUMBER = "destinationAddress";

    private static final String SOURCE_NUMBER = "sourceAddress";

    private final String messageText = "Test Message";

    private int seqNum;

    private Address sourceAddress;

    private Address destinationAddress;

    @Before
    public void setup() {
        sourceAddress = new Address((byte) 0x03, (byte) 0x00, SOURCE_NUMBER);
        destinationAddress = new Address((byte) 0x01, (byte) 0x01, DESTINATION_NUMBER);
        inboundMessageBus = new SerializedSubject<>(PublishSubject.create());
        objectUnderTest = new SmppAdapter(mockConnectionHandler,
                                          inboundMessageBus,
                                          mockAddressFormatter);
    }

    @Test
    public void shouldReturnExpectedRegisterationObject() {
        assertSame(inboundMessageBus, objectUnderTest.getEventBus());
    }

    @Test
    public void testSubmitSmBinarySuccess() throws Exception {
        setupPduSendSuccessMocks();
        final Observable<SequenceNumberPair> result = objectUnderTest.submitSmBinary(SOURCE_NUMBER,
                                                                                     DESTINATION_NUMBER,
                                                                                     messageText.getBytes());
        assertNotNull(result);
        assertTrue(result.toBlocking().single().getSmppSequenceNumber() == seqNum);
    }

    @Test
    public void testSubmitSmTextSuccess() throws Exception {
        setupPduSendSuccessMocks();
        final Observable<SequenceNumberPair> result = objectUnderTest.submitSmText(SOURCE_NUMBER,
                                                                                   DESTINATION_NUMBER,
                                                                                   messageText);
        assertNotNull(result);
        assertTrue(result.toBlocking().single().getSmppSequenceNumber() == seqNum);
    }

    @Test
    public void testSubmitSmBinaryFailure() throws Exception {
        setupPduErrorResponseMocks();
        try {
            final Observable<SequenceNumberPair> result = objectUnderTest.submitSmBinary(SOURCE_NUMBER,
                                                                                         DESTINATION_NUMBER,
                                                                                         messageText.getBytes());
            fail(String.format("Should not reach this block of code: {}",
                               result.toBlocking().single().toString()));
        } catch (final HystrixRuntimeException hre) {
            assertNotNull(hre);
            assertTrue(hre.getCause() instanceof SendSmppException);
        }
    }

    @Test
    public void testSubmitSmTextFailure() throws Exception {
        setupPduErrorResponseMocks();
        try {
            final Observable<SequenceNumberPair> result = objectUnderTest.submitSmText(SOURCE_NUMBER,
                                                                                       DESTINATION_NUMBER,
                                                                                       messageText);
            fail(String.format("Should not reach this block of code: {}",
                               result.toBlocking().single().toString()));
        } catch (final HystrixRuntimeException hre) {
            assertNotNull(hre);
            assertTrue(hre.getCause() instanceof SendSmppException);
        }
    }

    private SubmitSm createDefaultSubmitSmRequest() throws Exception {
        final SubmitSm request = new SubmitSm();
        request.setSourceAddress(sourceAddress);
        request.setDestAddress(destinationAddress);
        request.setShortMessage(messageText.getBytes());
        request.setSequenceNumber(seqNum);
        return request;
    }

    private void setupPduSendSuccessMocks() throws Exception {
        final PduRequest request = createDefaultSubmitSmRequest();
        final WindowFuture<Integer, PduRequest, PduResponse> defaultResponse = new TestWindowFuture(true,
                                                                                                    true,
                                                                                                    request);
        when(mockAddressFormatter.formatDestinationAddress(destinationAddress.getAddress())).thenReturn(destinationAddress);
        when(mockAddressFormatter.formatSourceAddress(sourceAddress.getAddress())).thenReturn(sourceAddress);
        when(mockConnectionHandler.getSmppSession()).thenReturn(mockSmppSession);
        when(mockConnectionHandler.getRateLimitingTokenBucket()).thenReturn(mockTokenBucket);
        when(mockTokenBucket.acquirePermit(anyLong(), anyObject())).thenReturn(true);
        when(mockSmppSession.sendRequestPdu(isA(PduRequest.class),
                                            anyLong(),
                                            anyBoolean())).thenReturn(defaultResponse);
    }

    private void setupPduErrorResponseMocks() throws RecoverablePduException,
                                              UnrecoverablePduException,
                                              SmppTimeoutException,
                                              SmppChannelException,
                                              InterruptedException {
        when(mockAddressFormatter.formatDestinationAddress(destinationAddress.getAddress())).thenReturn(destinationAddress);
        when(mockAddressFormatter.formatSourceAddress(sourceAddress.getAddress())).thenReturn(sourceAddress);
        when(mockConnectionHandler.getSmppSession()).thenReturn(mockSmppSession);
        when(mockConnectionHandler.getRateLimitingTokenBucket()).thenReturn(mockTokenBucket);
        when(mockTokenBucket.acquirePermit(anyLong(), anyObject())).thenReturn(true);
        when(mockSmppSession.sendRequestPdu(isA(PduRequest.class),
                                            anyLong(),
                                            anyBoolean())).thenThrow(new SmppChannelException("channel error"));
    }
}
