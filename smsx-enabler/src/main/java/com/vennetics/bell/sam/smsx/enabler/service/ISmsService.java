package com.vennetics.bell.sam.smsx.enabler.service;

import com.vennetics.bell.sam.model.smsx.DeliveryStatusResponse;
import com.vennetics.bell.sam.smsx.enabler.SendSmsRequest;
import rx.Observable;

import java.util.UUID;

public interface ISmsService {

    /**
     * Submits a text message request.
     *
     * @param externalRequestId A non-null key that identifies the request to external systems
     * @param outboundMessage   A non-null object with details on what is to be sent where.
     * @return
     */
    Observable<Void> sendSmsMessage(UUID externalRequestId, SendSmsRequest outboundMessage);

    /**
     * Queries the Delivery Status of a specified messageId.
     *
     * @param messageId A non-null key that identifies the id to query
     * @return
     */
    Observable<DeliveryStatusResponse> getDeliveryStatus(String messageId);
}
