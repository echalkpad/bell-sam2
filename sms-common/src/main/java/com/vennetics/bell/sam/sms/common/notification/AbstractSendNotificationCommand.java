package com.vennetics.bell.sam.sms.common.notification;

import com.vennetics.bell.sam.core.commands.hystrix.AbstractHystrixDisabledCircuitBreakerCommand;
import com.vennetics.bell.sam.sms.common.dao.NotificationFormat;
import com.vennetics.bell.sam.sms.common.dao.SmsCallbackData;
import com.vennetics.bell.sam.sms.common.dao.SmsMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestOperations;

import java.util.Arrays;

/**
 * A HystrixCommand to notify a TPA about message delivery status
 */

public abstract class AbstractSendNotificationCommand
                extends AbstractHystrixDisabledCircuitBreakerCommand<ResponseEntity<String>> {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractSendNotificationCommand.class);

    private final RestOperations template;

    private final SmsMessage smsMessage;

    /**
     * Sole constructor.
     *
     * @param smsMessage Details of the message to notify against
     * @param template   The {@link RestOperations} to be used to address the service.
     */
    public AbstractSendNotificationCommand(final String commandGroupKey,
                                           final String commandNameKey,
                                           final SmsMessage smsMessage,
                                           final RestOperations template) {
        super(commandGroupKey, commandNameKey);
        this.smsMessage = smsMessage;
        this.template = template;
    }

    @Override protected ResponseEntity<String> runCommand() {
        LOG.debug("SendNotificationCommand smsMessage:{}", smsMessage);
        final SmsCallbackData smsCallbackData = smsMessage.getSmsCallbackData();
        final HttpHeaders headers = new HttpHeaders();
        formatHeadersForClient(headers, smsCallbackData.getNotificationFormat());
        final HttpEntity<?> entity = buildHttpEntity();
        final ResponseEntity<String> response = template.postForEntity(buildNotificationEndpoint(),
                                                                       entity,
                                                                       String.class);
        LOG.debug("SendNotificationCommand result {}", response.getStatusCode());
        return response;
    }

    protected abstract HttpEntity buildHttpEntity();

    protected abstract String buildNotificationEndpoint();

    protected SmsMessage getSmsMessage() {
        return smsMessage;
    }

    protected static void formatHeadersForClient(final HttpHeaders headers,
                                                 final NotificationFormat notificationFormat) {
        if (NotificationFormat.JSON.equals(notificationFormat)) {
            headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON_UTF8));
            headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        } else {
            headers.setAccept(Arrays.asList(MediaType.APPLICATION_XML));
            headers.setContentType(MediaType.APPLICATION_XML);
        }
    }
}
