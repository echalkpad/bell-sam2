package com.vennetics.bell.sam.netapi.sms.enabler.notification;

import com.vennetics.bell.sam.sms.common.dao.SmsMessage;
import com.vennetics.bell.sam.sms.common.dao.SmsCallbackData;
import com.vennetics.bell.sam.sms.common.notification.AbstractSendNotificationCommand;
import generated.oma.xml.rest.netapi.sms._1.DeliveryInfo;
import generated.oma.xml.rest.netapi.sms._1.DeliveryInfoNotification;
import generated.oma.xml.rest.netapi.sms._1.DeliveryStatus;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestOperations;

/**
 * A HystrixCommand to notify a TPA about message delivery status
 */

public class NetAPISendNotificationCommand extends AbstractSendNotificationCommand {

    /**
     * Sole constructor.
     *
     * @param smsMessage Details of the message to notify against
     * @param template   The {@link RestOperations} to be used to address the service.
     */
    public NetAPISendNotificationCommand(final SmsMessage smsMessage, final RestOperations template) {
        super("SmsCallbackData", "netapi.sms.enabler.sendNotification", smsMessage, template);
    }

    /**
     * Builds up the HttpEntity as a NetAPI {@link DeliveryInfoNotification} object
     *
     * @return
     */
    @Override protected HttpEntity<?> buildHttpEntity() {
        final SmsCallbackData smsCallbackData = getSmsMessage().getSmsCallbackData();
        final HttpHeaders headers = new HttpHeaders();
        formatHeadersForClient(headers, smsCallbackData.getNotificationFormat());
        final DeliveryInfoNotification request = new DeliveryInfoNotification();
        request.setCallbackData(smsCallbackData.getCallbackData());
        final DeliveryInfo deliveryInfo = new DeliveryInfo();
        deliveryInfo.setAddress(getSmsMessage().getDestinationAddress());
        deliveryInfo.setDeliveryStatus(DeliveryStatus.fromValue(getSmsMessage().getDeliveryStatus().value()));
        request.getDeliveryInfos().add(deliveryInfo);
        return new HttpEntity<>(request, headers);

    }

    @Override protected String buildNotificationEndpoint() {
        return getSmsMessage().getSmsCallbackData().getDeliveryReceiptUrl();
    }

}
