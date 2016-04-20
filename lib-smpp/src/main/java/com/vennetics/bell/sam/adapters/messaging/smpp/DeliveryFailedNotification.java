package com.vennetics.bell.sam.adapters.messaging.smpp;

/**
 * Indicates that a delivery receipt has been received which showed message was
 * not delivered.
 */
public class DeliveryFailedNotification extends SmppSendMessageIdEvent {

    public DeliveryFailedNotification(final String messageId) {
        super(messageId);
    }
}
