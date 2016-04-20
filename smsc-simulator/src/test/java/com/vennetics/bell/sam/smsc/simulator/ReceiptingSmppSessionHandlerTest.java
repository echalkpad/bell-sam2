package com.vennetics.bell.sam.smsc.simulator;

import com.cloudhopper.commons.util.windowing.WindowFuture;
import com.cloudhopper.smpp.SmppConstants;
import com.cloudhopper.smpp.SmppSession;
import com.cloudhopper.smpp.pdu.DeliverSm;
import com.cloudhopper.smpp.pdu.DeliverSmResp;
import com.cloudhopper.smpp.pdu.EnquireLink;
import com.cloudhopper.smpp.pdu.EnquireLinkResp;
import com.cloudhopper.smpp.pdu.PduRequest;
import com.cloudhopper.smpp.pdu.PduResponse;
import com.cloudhopper.smpp.pdu.SubmitSm;
import com.cloudhopper.smpp.pdu.SubmitSmResp;
import com.cloudhopper.smpp.type.Address;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ReceiptingSmppSessionHandlerTest {

    // Objects under test
    private ReceiptingSmppSessionHandler objectUnderTest;
    private ReceiptingSmppSessionHandler.Receiptor receiptorUnderTest;
    private ReceiptingSmppSessionHandler.Sender senderUnderTest;

    // Mock objects
    @Mock
    private SmppSession mockSession;

    @Mock
    private PduRequest mockPduRequest;

    @Mock
    private WindowFuture<Integer, PduRequest, PduResponse> mockFuture;

    // Concrete Objects
    private SubmitSm request;
    private final String messageId = "1234";
    private final String addressRange = "\\d{3}";
    private static final String DESTINATION_ADDRESS = "1234567";

    @Before
    public void setup() {
        objectUnderTest = new ReceiptingSmppSessionHandler(mockSession, addressRange);
        request = new SubmitSm();
        receiptorUnderTest = new ReceiptingSmppSessionHandler.Receiptor(mockSession,
                                                                        request,
                                                                        messageId);
        senderUnderTest = new ReceiptingSmppSessionHandler.Sender(mockSession,
                                                                        request);
    }

    @Test
    public void shouldJustCreateResponseIfNoSubmitSm() {
        Object result = objectUnderTest.firePduRequestReceived(new EnquireLink());
        assertTrue(result instanceof EnquireLinkResp);
        verifyZeroInteractions(mockSession);
    }

    @Test
    public void shouldJustCreateResponseIfSubmitSmWithoutReceipting() {
        final Address destinationAddress = new Address(SmppConstants.TON_NATIONAL,
                                                       SmppConstants.NPI_E164,
                                                       DESTINATION_ADDRESS);
        request.setDestAddress(destinationAddress);
        Object result = objectUnderTest.firePduRequestReceived(request);
        assertTrue(result instanceof SubmitSmResp);
        verifyZeroInteractions(mockSession);
    }

    @Test
    public void shouldCreateResponseAndSendTwoPdusIfSubmitSmWithReceiptingAndDestinationInAddressRange() throws Exception {
        request.setRegisteredDelivery(SmppConstants.REGISTERED_DELIVERY_SMSC_RECEIPT_REQUESTED);
        final Address destinationAddress = new Address(SmppConstants.TON_NATIONAL,
                                                       SmppConstants.NPI_E164,
                                                       DESTINATION_ADDRESS);
        request.setDestAddress(destinationAddress);
        when(mockFuture.await()).thenReturn(false);
        when(mockSession.sendRequestPdu(isA(DeliverSm.class), anyLong(), anyBoolean())).thenReturn(
                        mockFuture);
        Object result = objectUnderTest.firePduRequestReceived(request);
        assertTrue(result instanceof SubmitSmResp);
        verify(mockSession,
               timeout(2500).times(2)).sendRequestPdu(isA(DeliverSm.class), anyLong(), anyBoolean());
        verifyNoMoreInteractions(mockSession);
    }

    @Test
    public void shouldCreateResponseAndSendOnePduIfSubmitSmWithReceiptingAndDestinationNotInAddressRange() throws Exception {
        request.setRegisteredDelivery(SmppConstants.REGISTERED_DELIVERY_SMSC_RECEIPT_REQUESTED);
        final Address destinationAddress = new Address(SmppConstants.TON_NATIONAL,
                                                       SmppConstants.NPI_E164,
                                                       "abcd");
        request.setDestAddress(destinationAddress);
        when(mockFuture.await()).thenReturn(false);
        when(mockSession.sendRequestPdu(isA(DeliverSm.class), anyLong(), anyBoolean())).thenReturn(
                        mockFuture);
        Object result = objectUnderTest.firePduRequestReceived(request);
        assertTrue(result instanceof SubmitSmResp);
        verify(mockSession,
               timeout(1500).times(1)).sendRequestPdu(isA(DeliverSm.class), anyLong(), anyBoolean());
        verifyNoMoreInteractions(mockSession);
    }

    @Test
    public void shouldInvokeSessiontoSendDeliveryReceiptAndHandleAwaitFail() throws Exception {
        when(mockFuture.await()).thenReturn(false);
        when(mockSession.sendRequestPdu(isA(DeliverSm.class), anyLong(), anyBoolean())).thenReturn(
                        mockFuture);
        receiptorUnderTest.sendDeliveryReceipt();
    }

    @Test
    public void shouldInvokeSessiontoSendDeliveryReceiptAndHandleSuccess() throws Exception {
        when(mockFuture.await()).thenReturn(true);
        when(mockFuture.isSuccess()).thenReturn(true);
        when(mockFuture.getResponse()).thenReturn(new DeliverSmResp());
        when(mockSession.sendRequestPdu(isA(DeliverSm.class), anyLong(), anyBoolean())).thenReturn(
                        mockFuture);
        receiptorUnderTest.sendDeliveryReceipt();
    }

    @Test
    public void shouldInvokeSessiontoSendDeliveryReceiptAndHandleError() throws Exception {
        when(mockFuture.await()).thenReturn(true);
        when(mockFuture.isSuccess()).thenReturn(false);
        when(mockFuture.getCause()).thenReturn(new RuntimeException());
        when(mockSession.sendRequestPdu(isA(DeliverSm.class), anyLong(), anyBoolean())).thenReturn(
                        mockFuture);
        receiptorUnderTest.sendDeliveryReceipt();
    }


    @Test
    public void shouldInvokeSessionToSendMoPduAndHandleAwaitFail() throws Exception {
        when(mockFuture.await()).thenReturn(false);
        when(mockSession.sendRequestPdu(isA(DeliverSm.class), anyLong(), anyBoolean())).thenReturn(
                        mockFuture);
        senderUnderTest.sendMoMessage();
    }

    @Test
    public void shouldInvokeSessionToSendMoPduAndHandleSuccess() throws Exception {
        when(mockFuture.await()).thenReturn(true);
        when(mockFuture.isSuccess()).thenReturn(true);
        when(mockFuture.getResponse()).thenReturn(new DeliverSmResp());
        when(mockSession.sendRequestPdu(isA(DeliverSm.class), anyLong(), anyBoolean())).thenReturn(
                        mockFuture);
        senderUnderTest.sendMoMessage();
    }

    @Test
    public void shouldInvokeSessionToSendMoPduAndHandleError() throws Exception {
        when(mockFuture.await()).thenReturn(true);
        when(mockFuture.isSuccess()).thenReturn(false);
        when(mockFuture.getCause()).thenReturn(new RuntimeException());
        when(mockSession.sendRequestPdu(isA(DeliverSm.class), anyLong(), anyBoolean())).thenReturn(
                        mockFuture);
        senderUnderTest.sendMoMessage();
    }
}
