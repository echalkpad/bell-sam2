package com.vennetics.bell.sam.netapi.sms.enabler.notification;

import com.vennetics.bell.sam.adapters.messaging.smpp.ISmppAdapter;
import com.vennetics.bell.sam.sms.common.dao.SmsMessage;
import com.vennetics.bell.sam.sms.common.dao.SmsMessageRepository;
import com.vennetics.bell.sam.sms.common.notification.AbstractSendSmsNotificationHandlerService;
import com.vennetics.bell.sam.sms.common.notification.AbstractSendNotificationCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestOperations;

/**
 * NetAPI specific class for handling notifications received from the lib-smpp module.
 */
@Service
public class NetAPISendSmsNotificationHandlerService
                extends AbstractSendSmsNotificationHandlerService {

    @Autowired
    public NetAPISendSmsNotificationHandlerService(final SmsMessageRepository messageRepository,
                                                   final ISmppAdapter smppAdapter,
                                                   final RestOperations restOperations) {
        super(messageRepository, smppAdapter, restOperations);
    }

    @Override
    protected AbstractSendNotificationCommand createCommand(final SmsMessage smsMessage) {
        return new NetAPISendNotificationCommand(smsMessage, getRestTemplate());
    }
}
