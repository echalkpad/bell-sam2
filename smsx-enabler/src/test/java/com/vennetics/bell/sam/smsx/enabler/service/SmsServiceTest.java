package com.vennetics.bell.sam.smsx.enabler.service;

import com.vennetics.bell.sam.adapters.messaging.smpp.ISmppAdapter;
import com.vennetics.bell.sam.adapters.messaging.smpp.SequenceNumberPair;
import com.vennetics.bell.sam.adapters.messaging.smpp.exception.SendSmppException;
import com.vennetics.bell.sam.error.exceptions.InvalidAddressException;
import com.vennetics.bell.sam.model.smsx.DeliveryStatusResponse;
import com.vennetics.bell.sam.sms.common.address.Address;
import com.vennetics.bell.sam.sms.common.address.IAddressNormalizer;
import com.vennetics.bell.sam.sms.common.dao.SmsDeliveryStatus;
import com.vennetics.bell.sam.sms.common.dao.SmsMessage;
import com.vennetics.bell.sam.sms.common.dao.SmsMessageRepository;
import com.vennetics.bell.sam.smsx.enabler.SendSmsRequest;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import rx.Observable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.vennetics.bell.sam.smsx.enabler.utils.matchers.DeliveryStatusMatcher.deliveryStatusEq;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
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
    private static final String DEST_ADDRESS_TWO = "54321";
    private final String messageText = "Test Message";
    private final UUID externalRequestId = UUID.randomUUID();
    private final Address address = new Address();
    private final String notifyUrl = "http://bbc.co.uk";
    private final String correlator = "abc123";

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
        final SendSmsRequest request = createDefaultOutboundMessage();
        when(mockAddressNormalizer.normalizeToE164Address(isA(Address.class))).thenThrow(new InvalidAddressException(
                        DEST_ADDRESS_ONE));
        try {
            objectUnderTest.sendSmsMessage(externalRequestId, request);
            fail("Test should have encountered exception");
        } catch (final InvalidAddressException iae) {
            assertArrayEquals(iae.getMessageArguments(), new String[] { DEST_ADDRESS_ONE });
        }
    }

    @Test
    public void testThatMessagePersistedWithDeliveryWaitingForSuccessfullyProcessedMessage() {
        when(mockAddressNormalizer.normalizeToE164Address(isA(Address.class))).thenReturn(address);
        final SendSmsRequest request = createTextOutboundMessage();
        final SequenceNumberPair sequencePair = new SequenceNumberPair();
        sequencePair.setDestinationAddress(DEST_ADDRESS_ONE);
        sequencePair.setSmppSequenceNumber(123);
        when(mockMessageRepository.save(isA(SmsMessage.class))).thenReturn(null);
        when(mockSmppAdapter.submitSmText(eq(SENDER_ADDRESS),
                                          eq(DEST_ADDRESS_ONE),
                                          eq(request.getMessage()))).thenReturn(Observable.just(
                        sequencePair));
        final Observable<Void> observable = objectUnderTest.sendSmsMessage(externalRequestId,
                                                                           request);
        final Void result = observable.toBlocking().single();
        assertNull(result);
        final SmsMessage expectedMessage = new SmsMessage();
        expectedMessage.setDeliveryStatus(SmsDeliveryStatus.MESSAGE_WAITING);
        verify(mockMessageRepository, times(1)).save(deliveryStatusEq(expectedMessage));
    }

    @Test
    public void testThatMessagePersistedWithDeliveryImpossibleForUnsuccessfullyProcessedMessage() {
        when(mockAddressNormalizer.normalizeToE164Address(isA(Address.class))).thenReturn(address);
        final SendSmsRequest request = createTextOutboundMessage();
        final SequenceNumberPair sequencePair = new SequenceNumberPair();
        sequencePair.setDestinationAddress(DEST_ADDRESS_ONE);
        sequencePair.setSmppSequenceNumber(123);
        when(mockMessageRepository.save(isA(SmsMessage.class))).thenReturn(null);
        when(mockSmppAdapter.submitSmText(eq(SENDER_ADDRESS),
                                          eq(DEST_ADDRESS_ONE),
                                          eq(request.getMessage()))).thenReturn(Observable.error(new SendSmppException(
                        DEST_ADDRESS_ONE,
                        new RuntimeException())));
        final Observable<Void> observable = objectUnderTest.sendSmsMessage(externalRequestId,
                                                                           request);
        final Void result = observable.toBlocking().single();
        assertNull(result);
        final SmsMessage expectedMessage = new SmsMessage();
        expectedMessage.setDeliveryStatus(SmsDeliveryStatus.DELIVERY_IMPOSSIBLE);
        verify(mockMessageRepository, times(1)).save(deliveryStatusEq(expectedMessage));
    }

    @Test
    public void testThatMessagePersistedWithDeliveryWaitingForTimedOutMessage() {
        when(mockAddressNormalizer.normalizeToE164Address(isA(Address.class))).thenReturn(address);
        final SendSmsRequest request = createTextOutboundMessage();
        final SequenceNumberPair sequencePair = new SequenceNumberPair();
        sequencePair.setDestinationAddress(DEST_ADDRESS_ONE);
        sequencePair.setSmppSequenceNumber(123);
        when(mockMessageRepository.save(isA(SmsMessage.class))).thenReturn(null);
        when(mockSmppAdapter.submitSmText(eq(SENDER_ADDRESS),
                                          eq(DEST_ADDRESS_ONE),
                                          eq(request.getMessage()))).thenReturn(Observable.just(
                        sequencePair).delay(3, TimeUnit.SECONDS));
        final Observable<Void> observable = objectUnderTest.sendSmsMessage(externalRequestId,
                                                                           request);
        final Void result = observable.toBlocking().single();
        assertNull(result);
        final SmsMessage expectedMessage = new SmsMessage();
        expectedMessage.setDeliveryStatus(SmsDeliveryStatus.MESSAGE_WAITING);
        verify(mockMessageRepository,
               timeout(3500).times(1)).save(deliveryStatusEq(expectedMessage));
    }

    @Test
    public void testThatAGetDeliveryStatusWithUnknownIdReturnsEmptyResponse() {
        when(mockMessageRepository.findByExternalRequestId(externalRequestId)).thenReturn(new ArrayList<SmsMessage>());
        final Observable<DeliveryStatusResponse> observable = objectUnderTest.getDeliveryStatus(
                        externalRequestId.toString());
        final DeliveryStatusResponse result = observable.toBlocking().single();
        assertNotNull(result);
        assertTrue(result.getMessageResponses().size() == 0);
    }

    @Test
    public void testThatAGetDeliveryStatusWithRubbishReturnsEmptyResponse() {
        final Observable<DeliveryStatusResponse> observable = objectUnderTest.getDeliveryStatus(
                        "Not a uuid");
        final DeliveryStatusResponse result = observable.toBlocking().single();
        verifyZeroInteractions(mockMessageRepository);
        assertNotNull(result);
        assertTrue(result.getMessageResponses().size() == 0);
    }

    @Test
    public void testThatAGetDeliveryStatusWithFoundIdReturnsSingleResponse() {
        when(mockMessageRepository.findByExternalRequestId(externalRequestId)).thenReturn(
                        createDefaultSmsMessageList());
        final Observable<DeliveryStatusResponse> observable = objectUnderTest.getDeliveryStatus(
                        externalRequestId.toString());
        final DeliveryStatusResponse result = observable.toBlocking().single();
        assertNotNull(result);
        assertTrue(result.getMessageResponses().size() == 1);
        assertTrue(StringUtils.equals(result.getMessageResponses().get(0).getAddress(),
                                      DEST_ADDRESS_ONE));
        assertTrue(StringUtils.equals(result.getMessageResponses().get(0).getStatus(),
                                      SmsDeliveryStatus.DELIVERED_TO_TERMINAL.value()));
    }

    @Test
    public void testThatAGetDeliveryStatusWithFoundIdReturnsMultipleResponse() {
        final List<SmsMessage> messageList = createDefaultSmsMessageList();
        final SmsMessage additionalMessage = createSmsMessage(DEST_ADDRESS_TWO, SmsDeliveryStatus.DELIVERY_UNCERTAIN);
        messageList.add(additionalMessage);
        when(mockMessageRepository.findByExternalRequestId(externalRequestId)).thenReturn(messageList);
        final Observable<DeliveryStatusResponse> observable = objectUnderTest.getDeliveryStatus(
                        externalRequestId.toString());
        final DeliveryStatusResponse result = observable.toBlocking().single();
        assertNotNull(result);
        assertTrue(result.getMessageResponses().size() == 2);
        assertTrue(StringUtils.equals(result.getMessageResponses().get(0).getAddress(),
                                      DEST_ADDRESS_ONE));
        assertTrue(StringUtils.equals(result.getMessageResponses().get(0).getStatus(),
                                      SmsDeliveryStatus.DELIVERED_TO_TERMINAL.value()));
        assertTrue(StringUtils.equals(result.getMessageResponses().get(1).getAddress(),
                                      DEST_ADDRESS_TWO));
        assertTrue(StringUtils.equals(result.getMessageResponses().get(1).getStatus(),
                                      SmsDeliveryStatus.DELIVERY_UNCERTAIN.value()));
    }

    private SendSmsRequest createTextOutboundMessage() {
        final SendSmsRequest outboundMessage = createDefaultOutboundMessage();
        outboundMessage.setMessage(messageText);
        return outboundMessage;
    }

    private SendSmsRequest createDefaultOutboundMessage() {
        final SendSmsRequest outboundMessage = new SendSmsRequest(SENDER_ADDRESS,
                                                                  messageText,
                                                                  Arrays.asList(DEST_ADDRESS_ONE),
                                                                  notifyUrl,
                                                                  correlator);
        return outboundMessage;
    }

    private SmsMessage createSmsMessage(final String destinationAddress, final SmsDeliveryStatus deliveryStatus) {
        final SmsMessage smsMessage = new SmsMessage();
        smsMessage.setDestinationAddress(destinationAddress);
        smsMessage.setDeliveryStatus(deliveryStatus);
        return smsMessage;
    }

    private SmsMessage createSmsMessage() {
        return createSmsMessage(DEST_ADDRESS_ONE, SmsDeliveryStatus.DELIVERED_TO_TERMINAL);
    }

    private List<SmsMessage> createDefaultSmsMessageList() {
        final SmsMessage smsMessage = createSmsMessage();
        final List<SmsMessage> response = new ArrayList<>();
        response.add(smsMessage);
        return response;
    }
}
