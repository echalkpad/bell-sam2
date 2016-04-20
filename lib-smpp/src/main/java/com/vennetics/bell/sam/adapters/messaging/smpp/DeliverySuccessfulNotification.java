package com.vennetics.bell.sam.adapters.messaging.smpp;

/**
 * Indicates that a delivery receipt has been received which showed message was
 * delivered.
 */
public class DeliverySuccessfulNotification extends SmppSendMessageIdEvent {

    public DeliverySuccessfulNotification(final String messageId) {
        super(messageId);
    }
}
