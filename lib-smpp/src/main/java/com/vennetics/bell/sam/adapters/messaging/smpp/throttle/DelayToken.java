package com.vennetics.bell.sam.adapters.messaging.smpp.throttle;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * Delayed token that may be used in throttle implementations.
 * <P>
 * Delay is calculated based on the delta between the create time, current time
 * and the delay specified on construction.
 * <P>
 * If the time since construction has exceeded the delay supplied this token
 * will return a zero or negative delay indicating it is available again.
 */
public final class DelayToken implements Delayed {

    private static final int LESS_THAN = -1;
    private static final int EQUAL = 0;
    private static final int MORE_THAN = 1;

    private final long delay;

    private final TimeUnit delayTimeUnit;

    private final long createTime;

    public DelayToken(final long delay, final TimeUnit delayTimeUnit) {
        super();
        this.delay = delay;
        this.delayTimeUnit = delayTimeUnit;
        createTime = System.currentTimeMillis();
    }

    @Override
    public int compareTo(final Delayed arg0) {

        if (this.getDelay(delayTimeUnit) == arg0.getDelay(delayTimeUnit)) {
            return EQUAL;
        }
        if (this.getDelay(delayTimeUnit) > arg0.getDelay(delayTimeUnit)) {
            return MORE_THAN;
        }

        return LESS_THAN;
    }

    @Override
    public long getDelay(final TimeUnit unit) {
        return unit.convert(createTime - System.currentTimeMillis() + delay, TimeUnit.MILLISECONDS);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (delay ^ delay >>> 32);
        result = prime * result + (delayTimeUnit == null ? 0 : delayTimeUnit.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof DelayToken)) {
            return false;
        }
        final DelayToken other = (DelayToken) obj;
        if (delay != other.delay) {
            return false;
        }
        if (delayTimeUnit != other.delayTimeUnit) {
            return false;
        }
        return true;
    }

}
