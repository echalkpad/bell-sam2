package com.vennetics.bell.sam.adapters.messaging.smpp;

/**
 * Indicates that an inbound (mobile originating) message has been received.
 */
public class DeliverSmNotification extends SmppReceiveEvent {

    private final String message;
    private final String sourceAddress;
    private final String destAddress;

    public DeliverSmNotification(final String message,
                                 final String sourceAddress,
                                 final String destAddress) {
        this.message = message;
        this.sourceAddress = sourceAddress;
        this.destAddress = destAddress;
    }

    public String getMessage() {
        return message;
    }

    public String getSourceAddress() {
        return sourceAddress;
    }

    public String getDestAddress() {
        return destAddress;
    }

}
