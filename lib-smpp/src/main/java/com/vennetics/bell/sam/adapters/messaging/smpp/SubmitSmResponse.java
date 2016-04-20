package com.vennetics.bell.sam.adapters.messaging.smpp;

/**
 * The response for a successfully received submit_sm operation.
 */
public class SubmitSmResponse extends SmppSendMessageIdEvent {

    public SubmitSmResponse(final String messageId) {
        super(messageId);
    }
}
