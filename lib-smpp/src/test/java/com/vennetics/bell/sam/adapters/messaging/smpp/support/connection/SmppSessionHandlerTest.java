package com.vennetics.bell.sam.adapters.messaging.smpp.support.connection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.when;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.cloudhopper.smpp.SmppConstants;
import com.cloudhopper.smpp.pdu.DataSm;
import com.cloudhopper.smpp.pdu.DeliverSm;
import com.cloudhopper.smpp.pdu.PduResponse;
import com.cloudhopper.smpp.pdu.SubmitSm;
import com.cloudhopper.smpp.pdu.SubmitSmResp;
import com.cloudhopper.smpp.tlv.Tlv;
import com.cloudhopper.smpp.type.Address;
import com.vennetics.bell.sam.adapters.messaging.smpp.DeliverSmNotification;
import com.vennetics.bell.sam.adapters.messaging.smpp.DeliveryFailedNotification;
import com.vennetics.bell.sam.adapters.messaging.smpp.DeliverySuccessfulNotification;
import com.vennetics.bell.sam.adapters.messaging.smpp.SmppInboundEvent;
import com.vennetics.bell.sam.adapters.messaging.smpp.SubmitSmFailed;
import com.vennetics.bell.sam.adapters.messaging.smpp.SubmitSmResponse;
import com.vennetics.bell.sam.adapters.messaging.smpp.SubmitSmTimeout;
import com.vennetics.bell.sam.adapters.messaging.smpp.throttle.ITokenBucket;

import rx.Observable;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

@RunWith(MockitoJUnitRunner.class)
public class SmppSessionHandlerTest {

    // Object under test
    private SmppSessionHandler objectUnderTest;

    // Concrete Objects
    private Subject<? super SmppInboundEvent, ? super SmppInboundEvent> inboundMessageBus;

    @Mock
    private ITokenBucket mockOutBoundRateLimitingTokenBucket;

    @Before
    public void setUp() {
        inboundMessageBus = new SerializedSubject<>(PublishSubject.create());
        objectUnderTest = new SmppSessionHandler(inboundMessageBus, mockOutBoundRateLimitingTokenBucket);
    }

    @Test
    public void testSubmitSmWithOkStatusNotificationsArePublishedAsSuccessful() throws Exception {
        final int numEvents = 20;

        final CountDownLatch countDownLatch = new CountDownLatch(numEvents);

        final Observable<? super SmppInboundEvent> eventObserver = inboundMessageBus;

        eventObserver.subscribe(event -> {
            if (event instanceof SubmitSmResponse) {
                countDownLatch.countDown();
            }
        });

        final SubmitSmResp ssr = new SubmitSmResp();
        ssr.setCommandStatus(SmppConstants.STATUS_OK);
        for (int i = 0; i < numEvents; i++) {
            objectUnderTest.firePduReceived(ssr);
        }

        assertTrue(countDownLatch.await(1, TimeUnit.SECONDS));
    }

    @Test
    public void testSubmitSmWithFailStatusNotificationsArePublishedAsFailed() throws Exception {
        final int numEvents = 20;

        final CountDownLatch countDownLatch = new CountDownLatch(numEvents);

        final Observable<? super SmppInboundEvent> eventObserver = inboundMessageBus;

        eventObserver.subscribe(event -> {
            if (event instanceof SubmitSmFailed) {
                countDownLatch.countDown();
            }
        });

        final SubmitSmResp ssr = new SubmitSmResp();
        ssr.setCommandStatus(SmppConstants.STATUS_SUBMITFAIL);
        for (int i = 0; i < numEvents; i++) {
            objectUnderTest.firePduReceived(ssr);
        }

        assertTrue(countDownLatch.await(1, TimeUnit.SECONDS));
    }

    @Test
    public void testDeliverSmAsDeliveryReceiptWithDeliveredStateNotificationsArePublished() throws Exception {
        final int numEvents = 20;

        final CountDownLatch countDownLatch = new CountDownLatch(numEvents);

        final Observable<? super SmppInboundEvent> eventObserver = inboundMessageBus;

        when(mockOutBoundRateLimitingTokenBucket.acquirePermit(anyLong(), anyObject())).thenReturn(true);

        eventObserver.subscribe(event -> {
            if (event instanceof DeliverySuccessfulNotification) {
                countDownLatch.countDown();
            }
        });

        final DeliverSm ds = new DeliverSm();
        ds.setEsmClass(SmppConstants.ESM_CLASS_MT_SMSC_DELIVERY_RECEIPT);
        ds.addOptionalParameter(new Tlv(SmppConstants.TAG_RECEIPTED_MSG_ID,
                                        "38601fa\u0000".getBytes()));
        ds.addOptionalParameter(new Tlv(SmppConstants.TAG_MSG_STATE,
                                        new byte[] { SmppConstants.STATE_DELIVERED }));
        for (int i = 0; i < numEvents; i++) {
            objectUnderTest.firePduRequestReceived(ds);
        }

        assertTrue(countDownLatch.await(1, TimeUnit.SECONDS));
    }


    @Test
    public void testDeliverSmArePublished() throws Exception {
        final int numEvents = 20;

        final CountDownLatch countDownLatch = new CountDownLatch(numEvents);

        final Observable<? super SmppInboundEvent> eventObserver = inboundMessageBus;

        when(mockOutBoundRateLimitingTokenBucket.acquirePermit(anyLong(), anyObject())).thenReturn(true);

        eventObserver.subscribe(event -> {
            if (event instanceof DeliverSmNotification) {
                countDownLatch.countDown();
            }
        });

        final DeliverSm ds = new DeliverSm();
        ds.setShortMessage("Message".getBytes());
        ds.setSourceAddress(new Address(SmppConstants.TON_NETWORK, SmppConstants.NPI_E164, "1234567"));
        ds.setDestAddress(new Address(SmppConstants.TON_NETWORK, SmppConstants.NPI_E164, "1234567"));
        for (int i = 0; i < numEvents; i++) {
            objectUnderTest.firePduRequestReceived(ds);
        }

        assertTrue(countDownLatch.await(1, TimeUnit.SECONDS));
    }

    @Test
    public void testDeliverSmAsDeliveryReceiptWithOtherStateNotificationsArePublished() throws Exception {
        final int numEvents = 20;

        final CountDownLatch countDownLatch = new CountDownLatch(numEvents);

        final Observable<? super SmppInboundEvent> eventObserver = inboundMessageBus;

        when(mockOutBoundRateLimitingTokenBucket.acquirePermit(anyLong(), anyObject())).thenReturn(true);

        eventObserver.subscribe(event -> {
            if (event instanceof DeliveryFailedNotification) {
                countDownLatch.countDown();
            }
        });

        final DeliverSm ds = new DeliverSm();
        ds.setEsmClass(SmppConstants.ESM_CLASS_MT_SMSC_DELIVERY_RECEIPT);
        ds.addOptionalParameter(new Tlv(SmppConstants.TAG_RECEIPTED_MSG_ID,
                                        "38601fa\u0000".getBytes()));
        ds.addOptionalParameter(new Tlv(SmppConstants.TAG_MSG_STATE,
                                        new byte[] { SmppConstants.STATE_UNDELIVERABLE }));
        for (int i = 0; i < numEvents; i++) {
            objectUnderTest.firePduRequestReceived(ds);
        }

        assertTrue(countDownLatch.await(1, TimeUnit.SECONDS));
    }

    @Test
    public void testOtherNotificationsAreNotPublished() throws Exception {
        final int numEvents = 3;

        final CountDownLatch countDownLatch = new CountDownLatch(numEvents);

        final Observable<? super SmppInboundEvent> eventObserver = inboundMessageBus;

        eventObserver.subscribe(event -> {
            countDownLatch.countDown();
        });

        final DataSm ds = new DataSm();
        for (int i = 0; i < numEvents; i++) {
            objectUnderTest.firePduReceived(ds);
        }

        assertFalse(countDownLatch.await(1, TimeUnit.SECONDS));
    }

    @Test
    public void testPduRequestTimesArePublished() throws Exception {
        final int numEvents = 20;

        final CountDownLatch countDownLatch = new CountDownLatch(numEvents);

        final Observable<? super SmppInboundEvent> eventObserver = inboundMessageBus;

        eventObserver.subscribe(event -> {
            if (event instanceof SubmitSmTimeout) {
                countDownLatch.countDown();
            }
        });

        final SubmitSm ss = new SubmitSm();
        for (int i = 0; i < numEvents; i++) {
            objectUnderTest.firePduRequestExpired(ss);
        }

        assertTrue(countDownLatch.await(1, TimeUnit.SECONDS));
    }

    @Test
    public void testOtherExpiriesAreNotPublished() throws Exception {
        final int numEvents = 3;

        final CountDownLatch countDownLatch = new CountDownLatch(numEvents);

        final Observable<? super SmppInboundEvent> eventObserver = inboundMessageBus;

        eventObserver.subscribe(event -> {
            countDownLatch.countDown();
        });

        final DataSm ds = new DataSm();
        for (int i = 0; i < numEvents; i++) {
            objectUnderTest.firePduRequestExpired(ds);
        }

        assertFalse(countDownLatch.await(1, TimeUnit.SECONDS));
    }

    @Test
    public void testThrottledResponseReturnedWhenRateIsExceeded() {
        when(mockOutBoundRateLimitingTokenBucket.acquirePermit(anyLong(), anyObject())).thenReturn(false);

        final DataSm ds = new DataSm();
        final PduResponse pduResponse = objectUnderTest.firePduRequestReceived(ds);

        assertEquals(SmppConstants.STATUS_THROTTLED, pduResponse.getCommandStatus());
    }
}
