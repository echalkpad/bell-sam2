package com.vennetics.bell.sam.adapters.messaging.smpp;

/**
 * An inbound event from the SMS-C
 */
public abstract class SmppInboundEvent {

    private int sequenceNumber;

    public int getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(final int sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    @Override
    public String toString() {
        return "SmppInboundEvent [sequenceNumber=" + sequenceNumber + "]";
    }

}
