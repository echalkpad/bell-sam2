package com.vennetics.bell.sam.smsx.enabler.notification;

import com.vennetics.bell.sam.sms.common.dao.SmsCallbackData;
import com.vennetics.bell.sam.sms.common.dao.SmsMessage;
import com.vennetics.bell.sam.sms.common.notification.AbstractSendNotificationCommand;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestOperations;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * A HystrixCommand to notify a TPA about message delivery status
 */

public class SmsXSendNotificationCommand extends AbstractSendNotificationCommand {

    /**
     * Sole constructor.
     *
     * @param smsMessage Details of the message to notify against
     * @param template   The {@link RestOperations} to be used to address the service.
     */
    public SmsXSendNotificationCommand(final SmsMessage smsMessage, final RestOperations template) {
        super("SmsCallbackData", "smsx.enabler.sendNotification", smsMessage, template);
    }

    @Override protected HttpEntity<?> buildHttpEntity() {
        final SmsCallbackData smsCallbackData = getSmsMessage().getSmsCallbackData();
        final HttpHeaders headers = new HttpHeaders();
        formatHeadersForClient(headers, smsCallbackData.getNotificationFormat());
        final DeliveryStatus request = new DeliveryStatus();
        final Message message = new Message();
        message.setAddress(getSmsMessage().getDestinationAddress());
        message.setStatus(getSmsMessage().getDeliveryStatus().value());
        request.getMessages().add(message);
        return new HttpEntity<>(request, headers);

    }

    @Override protected String buildNotificationEndpoint() {
        final SmsCallbackData smsCallbackData = getSmsMessage().getSmsCallbackData();
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(smsCallbackData.getDeliveryReceiptUrl());
        if (StringUtils.isNotBlank(smsCallbackData.getCallbackData())) {
            uriBuilder.queryParam("correlator", smsCallbackData.getCallbackData());
        }
        return uriBuilder.toUriString();
    }

}
