package com.vennetics.bell.sam.smsx.enabler.notification;

import com.vennetics.bell.sam.adapters.messaging.smpp.ISmppAdapter;
import com.vennetics.bell.sam.sms.common.dao.SmsNotificationRepository;
import com.vennetics.bell.sam.sms.common.notification.AbstractReceiveSmsNotificationHandlerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SmsXReceiveSmsNotificationHandlerService
                extends AbstractReceiveSmsNotificationHandlerService {

    @Autowired
    public SmsXReceiveSmsNotificationHandlerService(final SmsNotificationRepository notificationRepository,
                                                    final ISmppAdapter smppAdapter) {
        super(notificationRepository, smppAdapter);
    }
}
