package com.vennetics.bell.sam.smsx.enabler.notification;

import com.vennetics.bell.sam.adapters.messaging.smpp.ISmppAdapter;
import com.vennetics.bell.sam.sms.common.dao.SmsMessage;
import com.vennetics.bell.sam.sms.common.dao.SmsMessageRepository;
import com.vennetics.bell.sam.sms.common.notification.AbstractSendSmsNotificationHandlerService;
import com.vennetics.bell.sam.sms.common.notification.AbstractSendNotificationCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestOperations;

/**
 * Created on 16/02/2016.
 */
@Component
public class SmsXSendSmsNotificationHandlerService
                extends AbstractSendSmsNotificationHandlerService {

    @Autowired
    public SmsXSendSmsNotificationHandlerService(final SmsMessageRepository messageRepository,
                                                 final ISmppAdapter smppAdapter,
                                                 final RestOperations restTemplate) {
        super(messageRepository, smppAdapter, restTemplate);
    }

    @Override
    protected AbstractSendNotificationCommand createCommand(final SmsMessage smsMessage) {
        return new SmsXSendNotificationCommand(smsMessage, getRestTemplate());
    }
}
