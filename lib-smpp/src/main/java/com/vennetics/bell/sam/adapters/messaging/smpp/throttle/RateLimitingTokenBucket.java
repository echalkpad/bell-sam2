package com.vennetics.bell.sam.adapters.messaging.smpp.throttle;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * Provides rate limited tokens via the {@link #acquirePermit()} operation. Can
 * be used in implementing in-memory throttles.
 * <P>
 * Internally backed using a {@link DelayQueue} which is populated at a fixed
 * rate and will never contain more than the max tasks per interval quota.
 */
public class RateLimitingTokenBucket implements ITokenBucket {

    private static final long INITIAL_DELAY = 0L;

    private final DelayQueue<Delayed> delayTokensQueue;

    private final Object tokenMonitor = new Object();

    private final long intervalMilliseconds;

    private final int maximumTasksPerInterval;

    private final Timer createTokenTimer;

    /**
     * Constructs an in memory rate limiter.
     * 
     * @param a name for this bucket - used in naming timer thread
     * @param maximumTasksPerInterval
     *            the maximum number of tasks to be processed per interval. Must
     *            be > 0
     * @param intervalMilliseconds
     *            the throttling interval measured in milliseconds. Must be > 0
     */
    public RateLimitingTokenBucket(final String name,
                                   final int maximumTasksPerInterval,
                                   final long intervalMilliseconds) {
        checkGreaterThanZero("maximumTasksPerInterval", maximumTasksPerInterval);
        checkGreaterThanZero("intervalMilliseconds", intervalMilliseconds);

        createTokenTimer = new Timer(name + "RateLimitingTokenBucketTimer", true);
        delayTokensQueue = new DelayQueue<Delayed>();

        this.maximumTasksPerInterval = maximumTasksPerInterval;
        this.intervalMilliseconds = intervalMilliseconds;

        initialiseTokens();
        initialiseRateTimer();
    }

    /**
     * Returns a token when available.
     * 
     * @return a token.
     */
    @Override
    public boolean acquirePermit() {
        try {
            delayTokensQueue.take();
            return true;
        } catch (final InterruptedException e) {
            Thread.interrupted();
            return false;
        }
    }

    @Override
    public boolean acquirePermit(final long timeout, final TimeUnit unit) {
        try {
            if (delayTokensQueue.poll(timeout, unit) != null) {
                return true;
            }

            return false;
        } catch (final InterruptedException e) {
            Thread.interrupted();
            return false;
        }
    }

    @Override
    public void shutdown() {
        delayTokensQueue.clear();
        createTokenTimer.cancel();
    }

    @Override
    public long getIntervalMilliseconds() {
        return intervalMilliseconds;
    }

    @Override
    public int getMaximumTasksPerInterval() {
        return maximumTasksPerInterval;
    }
    
    /**
     * Creates the initial set of tokens and adds them to the internal queue.
     * 
     */
    private void initialiseTokens() {

        for (int i = 0; i < maximumTasksPerInterval; i++) {
            delayTokensQueue.add(new DelayToken(0, TimeUnit.MILLISECONDS));
        }

    }

    /**
     * Starts a timer thread that makes tokens available for consumption by the
     * throttle.
     * 
     */
    private void initialiseRateTimer() {

        final long timerPeriod = roundUp(intervalMilliseconds, maximumTasksPerInterval);

        createTokenTimer.scheduleAtFixedRate(new CreateTokenTimerTask(), INITIAL_DELAY, timerPeriod);
    }
    
    private static long roundUp(final long num, final long divisor) {
        final int sign = (num > 0 ? 1 : -1) * (divisor > 0 ? 1 : -1);
        return sign * (Math.abs(num) + Math.abs(divisor) - 1) / Math.abs(divisor);
    }


    private static void checkGreaterThanZero(final String argumentName, final long value) {
        if (value <= 0) {
            throw new IllegalArgumentException(argumentName
                            + " must be greater than 0, supplied value is " + value);
        }
    }

    /**
     * Timer task that adds a token to the queue on execution if the queue is
     * less than max size.
     */
    private class CreateTokenTimerTask extends TimerTask {

        @SuppressWarnings("synthetic-access")
        @Override
        public void run() {
            synchronized (tokenMonitor) {
                if (delayTokensQueue.size() < maximumTasksPerInterval) {
                    // Create a token delayed for the period
                    delayTokensQueue.add(new DelayToken(intervalMilliseconds, TimeUnit.MILLISECONDS));
                }
            }

        }
    }
}
