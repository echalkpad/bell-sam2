package com.vennetics.bell.sam.sms.common.dao;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class SmsDeliveryStatusTest {

    @Test
    public void testIsNotifiableReturnsFalseIfReceiptingUrlNotSupplied() throws Exception {
        assertFalse(SmsDeliveryStatus.DELIVERY_IMPOSSIBLE.isNotifiable(false));
        assertFalse(SmsDeliveryStatus.DELIVERED_TO_NETWORK.isNotifiable(false));
        assertFalse(SmsDeliveryStatus.DELIVERED_TO_TERMINAL.isNotifiable(false));
        assertFalse(SmsDeliveryStatus.DELIVERY_UNCERTAIN.isNotifiable(false));
        assertFalse(SmsDeliveryStatus.MESSAGE_WAITING.isNotifiable(false));
    }

    @Test
    public void testIsNotifiableReturnsTrueIfReceiptingUrlSuppliedAndInEndStates() throws
                                                                                   Exception {
        assertTrue(SmsDeliveryStatus.DELIVERY_IMPOSSIBLE.isNotifiable(true));
        assertTrue(SmsDeliveryStatus.DELIVERED_TO_TERMINAL.isNotifiable(true));
    }

    @Test
    public void testIsNotifiableReturnsFalseIfReceiptingUrlSuppliedAndInOtherStates() throws
                                                                                      Exception {
        assertFalse(SmsDeliveryStatus.DELIVERED_TO_NETWORK.isNotifiable(true));
        assertFalse(SmsDeliveryStatus.DELIVERY_UNCERTAIN.isNotifiable(true));
        assertFalse(SmsDeliveryStatus.MESSAGE_WAITING.isNotifiable(true));
    }

    @Test
    public void testFromValueMatchesAsExpected() throws Exception {
        assertSame(SmsDeliveryStatus.DELIVERED_TO_TERMINAL, SmsDeliveryStatus.fromValue("DeliveredToTerminal"));
        assertSame(SmsDeliveryStatus.DELIVERY_IMPOSSIBLE, SmsDeliveryStatus.fromValue("DeliveryImpossible"));
        assertSame(SmsDeliveryStatus.MESSAGE_WAITING, SmsDeliveryStatus.fromValue("MessageWaiting"));
        assertSame(SmsDeliveryStatus.DELIVERED_TO_NETWORK, SmsDeliveryStatus.fromValue("DeliveredToNetwork"));
        assertSame(SmsDeliveryStatus.DELIVERY_UNCERTAIN, SmsDeliveryStatus.fromValue("DeliveryUncertain"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testExceptionThrownForUnmatchedValue() throws Exception {
        SmsDeliveryStatus.fromValue("Ted");
    }
}
