package com.vennetics.bell.sam.adapters.messaging.smpp.throttle;

import static org.junit.Assert.assertTrue;

import java.util.concurrent.TimeUnit;

import org.junit.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

public class DelayTokenTest {

    @Test
    public void testEquals() {
        EqualsVerifier.forClass(DelayToken.class).verify();
    }

    @Test
    public void testCompare() {
        final DelayToken delayToken1 = new DelayToken(10, TimeUnit.MILLISECONDS);
        final DelayToken delayToken2 = new DelayToken(100, TimeUnit.MILLISECONDS);
        final DelayToken delayToken3 = new DelayToken(100, TimeUnit.MILLISECONDS);

        assertTrue(delayToken1.compareTo(delayToken2) < 0);

        assertTrue(delayToken2.compareTo(delayToken1) > 0);

        assertTrue(delayToken2.compareTo(delayToken3) == 0);

        EqualsVerifier.forClass(DelayToken.class).verify();
    }

}
