package com.vennetics.bell.sam.sms.common.notification;

import com.google.common.base.Charsets;
import com.vennetics.bell.sam.adapters.messaging.smpp.DeliverSmNotification;
import com.vennetics.bell.sam.adapters.messaging.smpp.ISmppAdapter;
import com.vennetics.bell.sam.sms.common.dao.SmsNotification;
import com.vennetics.bell.sam.sms.common.dao.SmsNotificationRepository;
import com.vennetics.bell.sam.sms.common.dao.SmsNotificationState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;
import rx.Subscriber;

import javax.annotation.PostConstruct;

public abstract class AbstractReceiveSmsNotificationHandlerService {

    private static final Logger logger = LoggerFactory.getLogger(
                    AbstractReceiveSmsNotificationHandlerService.class);

    private final SmsNotificationRepository notificationRepository;
    private final ISmppAdapter smppAdapter;
    private Observable<DeliverSmNotification> eventObserver;

    public AbstractReceiveSmsNotificationHandlerService(final SmsNotificationRepository notificationRepository,
                                                        final ISmppAdapter smppAdapter) {
        this.notificationRepository = notificationRepository;
        this.smppAdapter = smppAdapter;
    }

    @PostConstruct
    public void subscribeForEvents() {
        eventObserver = smppAdapter.getFilteredEventBus(DeliverSmNotification.class);
        eventObserver.subscribe(new NotificationEventHandler());
    }

    private final class NotificationEventHandler extends Subscriber<DeliverSmNotification> {

        @Override
        public void onNext(final DeliverSmNotification event) {
            logger.debug(" >>> AbstractReceiveSmsNotificationHandlerService#onNext({})", event);
            final SmsNotification smsNotification = new SmsNotification();
            smsNotification.setBinary(false);
            smsNotification.setContent(event.getMessage().getBytes(Charsets.UTF_8));
            smsNotification.setDestinationAddress(event.getDestAddress());
            smsNotification.setSourceAddress(event.getSourceAddress());
            smsNotification.setNotificationState(SmsNotificationState.RECEIVED_FROM_NETWORK);
            notificationRepository.save(smsNotification);
        }

        @Override
        public void onError(final Throwable error) {
            logger.error("Error encountered while attempting to send notification.", error);
        }

        @Override
        public void onCompleted() {
            logger.debug("Sequence complete.");
        }
    }
}
