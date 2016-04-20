package com.vennetics.bell.sam.netapi.sms.enabler.service;

import com.vennetics.bell.sam.adapters.messaging.smpp.ISmppAdapter;
import com.vennetics.bell.sam.adapters.messaging.smpp.SequenceNumberPair;
import com.vennetics.bell.sam.adapters.messaging.smpp.exception.SendSmppException;
import com.vennetics.bell.sam.error.exceptions.InvalidAddressException;
import com.vennetics.bell.sam.sms.common.address.Address;
import com.vennetics.bell.sam.sms.common.address.IAddressNormalizer;
import com.vennetics.bell.sam.sms.common.dao.SmsDeliveryStatus;
import com.vennetics.bell.sam.sms.common.dao.SmsMessage;
import com.vennetics.bell.sam.sms.common.dao.SmsMessageRepository;
import generated.oma.xml.rest.netapi.sms._1.DeliveryStatus;
import generated.oma.xml.rest.netapi.sms._1.OutboundSMSBinaryMessage;
import generated.oma.xml.rest.netapi.sms._1.OutboundSMSMessageRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import rx.Observable;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SmsServiceTest {

    // Object Under Test
    private ISmsService objectUnderTest;

    // Mock Objects
    @Mock
    private ISmppAdapter mockSmppAdapter;

    @Mock
    private IAddressNormalizer mockAddressNormalizer;

    @Mock
    private SmsMessageRepository mockMessageRepository;

    // Concrete objects
    private static final String SENDER_ADDRESS = "123456";
    private static final String DEST_ADDRESS_ONE = "12345";
    private final String messageText = "Test Message";
    private final UUID externalRequestId = UUID.randomUUID();
    private final Address address = new Address();

    @Before
    public void setUp() {
        objectUnderTest = new SmsService(mockSmppAdapter,
                                         mockMessageRepository,
                                         mockAddressNormalizer);
        address.setSuppliedAddress(DEST_ADDRESS_ONE);
        address.setE164Address(DEST_ADDRESS_ONE);
    }

    @Test
    public void testThatExceptionThrownForInvalidAddresses() {
        final OutboundSMSMessageRequest request = createDefaultOutboundMessage();
        when(mockAddressNormalizer.normalizeToE164Address(isA(Address.class))).thenThrow(new InvalidAddressException(DEST_ADDRESS_ONE));
        try {
            objectUnderTest.sendSmsMessage(externalRequestId, SENDER_ADDRESS, request);
            fail("Test should have encountered exception");
        } catch (final InvalidAddressException iae) {
            assertArrayEquals(iae.getMessageArguments(), new String[] { DEST_ADDRESS_ONE });
        }
    }

    @Test
    public void testThatDeliveryWaitingInfoIsAddedForNonBinaryMessage() {
        final OutboundSMSMessageRequest request = createDefaultOutboundMessage();
        final Observable<OutboundSMSMessageRequest> observable = objectUnderTest.sendSmsMessage(externalRequestId,
                                                                                                SENDER_ADDRESS,
                                                                                                request);
        final OutboundSMSMessageRequest result = observable.toBlocking().single();
        assertNotNull(result.getDeliveryInfoList());
        assertEquals(result.getDeliveryInfoList().getDeliveryInfos().size(), 1);
        assertEquals(result.getDeliveryInfoList().getDeliveryInfos().get(0).getAddress(),
                     DEST_ADDRESS_ONE);
        assertEquals(result.getDeliveryInfoList().getDeliveryInfos().get(0).getDeliveryStatus(),
                     DeliveryStatus.MESSAGE_WAITING);
        verifyZeroInteractions(mockSmppAdapter);
    }

    @Test
    public void testThatDeliveryWaitingInfoIsAddedForSuccessfullyProcessedBinaryMessage() {
        when(mockAddressNormalizer.normalizeToE164Address(isA(Address.class))).thenReturn(address);
        final OutboundSMSMessageRequest request = createBinaryOutboundMessage();
        final SequenceNumberPair sequencePair = new SequenceNumberPair();
        sequencePair.setDestinationAddress(DEST_ADDRESS_ONE);
        sequencePair.setSmppSequenceNumber(123);
        when(mockMessageRepository.save(isA(SmsMessage.class))).thenReturn(null);
        when(mockSmppAdapter.submitSmBinary(eq(SENDER_ADDRESS),
                                            eq(DEST_ADDRESS_ONE),
                                            eq(request.getOutboundSMSBinaryMessage()
                                                      .getMessage()))).thenReturn(Observable.just(sequencePair));
        final Observable<OutboundSMSMessageRequest> observable = objectUnderTest.sendSmsMessage(externalRequestId,
                                                                                                SENDER_ADDRESS,
                                                                                                request);
        final OutboundSMSMessageRequest result = observable.toBlocking().single();
        assertNotNull(result.getDeliveryInfoList());
        assertEquals(result.getDeliveryInfoList().getDeliveryInfos().size(), 1);
        assertEquals(result.getDeliveryInfoList().getDeliveryInfos().get(0).getAddress(),
                     DEST_ADDRESS_ONE);
        assertEquals(result.getDeliveryInfoList().getDeliveryInfos().get(0).getDeliveryStatus(),
                     DeliveryStatus.MESSAGE_WAITING);
    }

    @Test
    public void testThatDeliveryWaitingInfoIsAddedForUnsuccessfullyProcessedBinaryMessage() {
        when(mockAddressNormalizer.normalizeToE164Address(isA(Address.class))).thenReturn(address);
        final OutboundSMSMessageRequest request = createBinaryOutboundMessage();
        final SequenceNumberPair sequencePair = new SequenceNumberPair();
        sequencePair.setDestinationAddress(DEST_ADDRESS_ONE);
        sequencePair.setSmppSequenceNumber(123);
        when(mockMessageRepository.save(isA(SmsMessage.class))).thenReturn(null);
        when(mockSmppAdapter.submitSmBinary(eq(SENDER_ADDRESS),
                                            eq(DEST_ADDRESS_ONE),
                                            eq(request.getOutboundSMSBinaryMessage()
                                                      .getMessage()))).thenReturn(Observable.error(new SendSmppException(DEST_ADDRESS_ONE,
                                                                                                                         new RuntimeException())));
        final Observable<OutboundSMSMessageRequest> observable = objectUnderTest.sendSmsMessage(externalRequestId,
                                                                                                SENDER_ADDRESS,
                                                                                                request);
        final OutboundSMSMessageRequest result = observable.toBlocking().single();
        assertNotNull(result.getDeliveryInfoList());
        assertEquals(result.getDeliveryInfoList().getDeliveryInfos().size(), 1);
        assertEquals(result.getDeliveryInfoList().getDeliveryInfos().get(0).getAddress(),
                     DEST_ADDRESS_ONE);
        assertEquals(result.getDeliveryInfoList().getDeliveryInfos().get(0).getDeliveryStatus(),
                     DeliveryStatus.MESSAGE_WAITING);
    }

    @Test
    public void testThatDeliveryWaitingInfoIsAddedForTimedOutBinaryMessage() {
        when(mockAddressNormalizer.normalizeToE164Address(isA(Address.class))).thenReturn(address);
        final OutboundSMSMessageRequest request = createBinaryOutboundMessage();
        final SequenceNumberPair sequencePair = new SequenceNumberPair();
        sequencePair.setDestinationAddress(DEST_ADDRESS_ONE);
        sequencePair.setSmppSequenceNumber(123);
        when(mockMessageRepository.save(isA(SmsMessage.class))).thenReturn(null);
        when(mockSmppAdapter.submitSmBinary(eq(SENDER_ADDRESS),
                                            eq(DEST_ADDRESS_ONE),
                                            eq(request.getOutboundSMSBinaryMessage()
                                                      .getMessage()))).thenReturn(Observable.just(sequencePair)
                                                                                            .delay(3,
                                                                                                   TimeUnit.SECONDS));
        final Observable<OutboundSMSMessageRequest> observable = objectUnderTest.sendSmsMessage(externalRequestId,
                                                                                                SENDER_ADDRESS,
                                                                                                request);
        final OutboundSMSMessageRequest result = observable.toBlocking().single();
        assertNotNull(result.getDeliveryInfoList());
        assertEquals(result.getDeliveryInfoList().getDeliveryInfos().size(), 1);
        assertEquals(result.getDeliveryInfoList().getDeliveryInfos().get(0).getAddress(),
                     DEST_ADDRESS_ONE);
        assertEquals(result.getDeliveryInfoList().getDeliveryInfos().get(0).getDeliveryStatus(),
                     DeliveryStatus.MESSAGE_WAITING);
    }

    @Test
    public void deliveryStatusAligns() throws Exception {
        final DeliveryStatus messageWaiting = DeliveryStatus.MESSAGE_WAITING;
        assertSame(SmsDeliveryStatus.MESSAGE_WAITING, SmsDeliveryStatus.fromValue(messageWaiting.value()));
    }

    private OutboundSMSMessageRequest createBinaryOutboundMessage() {
        final OutboundSMSMessageRequest outboundMessage = createDefaultOutboundMessage();
        OutboundSMSBinaryMessage binaryMessage = new OutboundSMSBinaryMessage();
        byte[] value = messageText.getBytes();
        binaryMessage.setMessage(value);
        outboundMessage.setOutboundSMSBinaryMessage(binaryMessage);
        return outboundMessage;
    }

    private OutboundSMSMessageRequest createDefaultOutboundMessage() {
        final OutboundSMSMessageRequest outboundMessage = new OutboundSMSMessageRequest();
        outboundMessage.getAddresses().add(DEST_ADDRESS_ONE);
        outboundMessage.setSenderAddress(SENDER_ADDRESS);
        return outboundMessage;
    }

}
