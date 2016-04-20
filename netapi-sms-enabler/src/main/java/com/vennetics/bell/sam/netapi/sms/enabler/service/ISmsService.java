package com.vennetics.bell.sam.netapi.sms.enabler.service;

import generated.oma.xml.rest.netapi.sms._1.OutboundSMSMessageRequest;
import rx.Observable;

import java.util.UUID;

@FunctionalInterface
public interface ISmsService {

    /**
     * Submits a binary message request.
     * 
     * @param externalRequestId
     *            A key that identifies the request to external systems
     * @param senderAddress
     *            The address of the sender
     * @param binaryMessage
     *            The binary message to be sent.
     * @return An {@link OutboundSMSMessageRequest} defining the delivery
     *         status.
     */
    Observable<OutboundSMSMessageRequest> sendSmsMessage(UUID externalRequestId,
                                                         String senderAddress,
                                                         OutboundSMSMessageRequest binaryMessage);

}
