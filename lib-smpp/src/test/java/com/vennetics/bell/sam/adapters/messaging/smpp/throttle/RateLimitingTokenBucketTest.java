package com.vennetics.bell.sam.adapters.messaging.smpp.throttle;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.TimeUnit;

import org.junit.Test;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

public class RateLimitingTokenBucketTest {

    @Test
    public void checkInitialised() {
        final RateLimitingTokenBucket bucket = new RateLimitingTokenBucket("testShouldTimeoutIfNoTokensAvailable",
                                                                           10,
                                                                           1000);

        assertEquals(10, bucket.getMaximumTasksPerInterval());
        assertEquals(1000, bucket.getIntervalMilliseconds());
    }

    @Test
    public void testShouldTimeoutIfNoTokensAvailable() {

        final RateLimitingTokenBucket bucket = new RateLimitingTokenBucket("testShouldTimeoutIfNoTokensAvailable",
                                                                           10,
                                                                           1000);

        for (int i = 0; i < 10; i++) {
            assertTrue(bucket.acquirePermit());
        }

        assertFalse(bucket.acquirePermit(1, TimeUnit.MILLISECONDS));

        bucket.shutdown();
    }

    @Test
    public void testEnforcesMaxRate() {

        final RateLimitingTokenBucket bucket = new RateLimitingTokenBucket("testEnforcesMaxRate",
                                                                           30,
                                                                           1000);

        for (int i = 0; i < 300; i++) {
            final Monitor jamonMonitor = MonitorFactory.start("testEnforcesMaxRate");
            try {
                assertTrue(bucket.acquirePermit());
            } finally {
                jamonMonitor.stop();
            }
        }

        final Monitor jamonMonitor = MonitorFactory.getMonitor("testEnforcesMaxRate", "ms.");

        final double tps = jamonMonitor.getHits()
                        * 1000
                        / (jamonMonitor.getLastAccess().getTime() - jamonMonitor.getFirstAccess()
                                        .getTime());

        // MAX Active will be 1 as single threaded
        assertEquals(1.0, jamonMonitor.getMaxActive(), 0.0);

        // TPS should be within 2 of limit
        assertEquals(30.0, tps, 2.0);
        assertTrue(tps <= 30.0);

        bucket.shutdown();
        jamonMonitor.reset();
    }

    @Test (expected = IllegalArgumentException.class)
    public void zeroArgumentForMaxTasksThrowsException() {
        new RateLimitingTokenBucket("testEnforcesMaxRate",
                                    0,
                                    1000);
    }

    @Test (expected = IllegalArgumentException.class)
    public void zeroArgumentForIntervalThrowsException() {
        new RateLimitingTokenBucket("testEnforcesMaxRate",
                                    1000,
                                    0);
    }
}
