package com.vennetics.bell.sam.adapters.messaging.smpp;

/**
 * An inbound message event from the SMS-C which has a message id generated by
 * the SMS-C
 */
public abstract class SmppSendMessageIdEvent extends SmppSendEvent {

    private final String messageId;

    public SmppSendMessageIdEvent(final String messageId) {
        this.messageId = messageId;
    }

    public String getMessageId() {
        return messageId;
    }

    @Override
    public String toString() {
        return "SmppSendMessageIdEvent [messageId=" + messageId + "]";
    }
}
