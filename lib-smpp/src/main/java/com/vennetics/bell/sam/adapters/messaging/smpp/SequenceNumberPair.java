package com.vennetics.bell.sam.adapters.messaging.smpp;

public class SequenceNumberPair {

    private String destinationAddress;

    private int smppSequenceNumber;

    public String getDestinationAddress() {
        return destinationAddress;
    }

    public void setDestinationAddress(final String destinationAddress) {
        this.destinationAddress = destinationAddress;
    }

    public int getSmppSequenceNumber() {
        return smppSequenceNumber;
    }

    public void setSmppSequenceNumber(final int smppSequenceNumber) {
        this.smppSequenceNumber = smppSequenceNumber;
    }

}
