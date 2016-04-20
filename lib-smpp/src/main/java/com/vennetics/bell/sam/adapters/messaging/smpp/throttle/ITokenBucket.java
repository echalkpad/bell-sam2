package com.vennetics.bell.sam.adapters.messaging.smpp.throttle;

import java.util.concurrent.TimeUnit;

/**
 * Provides tokens to applications. Implementations can apply restrictions such
 * as maximum rate or maximum total available.
 */
public interface ITokenBucket {

    /**
     * Wait indefinitely for a token.
     *
     * @return true if token acquired
     */
    boolean acquirePermit();

    /**
     * Wait a specified period for a token.
     *
     * @param timeout
     * @param unit
     * @return true if token acquired
     */
    boolean acquirePermit(long timeout, TimeUnit unit);

    /**
     * Shutdown the bucket.
     */
    void shutdown();

    long getIntervalMilliseconds();

    int getMaximumTasksPerInterval();

}
