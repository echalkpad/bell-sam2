package com.vennetics.bell.sam.sms.common.notification;

import com.vennetics.bell.sam.adapters.messaging.smpp.DeliverySuccessfulNotification;
import com.vennetics.bell.sam.adapters.messaging.smpp.ISmppAdapter;
import com.vennetics.bell.sam.adapters.messaging.smpp.SmppInboundEvent;
import com.vennetics.bell.sam.adapters.messaging.smpp.SmppSendEvent;
import com.vennetics.bell.sam.adapters.messaging.smpp.SmppSendMessageIdEvent;
import com.vennetics.bell.sam.adapters.messaging.smpp.SubmitSmResponse;
import com.vennetics.bell.sam.sms.common.dao.SmsDeliveryStatus;
import com.vennetics.bell.sam.sms.common.dao.SmsMessage;
import com.vennetics.bell.sam.sms.common.dao.SmsMessageRepository;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestOperations;
import rx.Observable;
import rx.Subscriber;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractSendSmsNotificationHandlerService {

    private static final Logger logger = LoggerFactory.getLogger(AbstractSendSmsNotificationHandlerService.class);

    private final SmsMessageRepository messageRepository;
    private final ISmppAdapter smppAdapter;
    private Observable<SmppSendEvent> eventObserver;

    private RestOperations restTemplate;

    public AbstractSendSmsNotificationHandlerService(final SmsMessageRepository messageRepository,
                                                     final ISmppAdapter smppAdapter,
                                                     final RestOperations restTemplate) {
        this.messageRepository = messageRepository;
        this.smppAdapter = smppAdapter;
        this.restTemplate = restTemplate;
    }

    @PostConstruct
    public void subscribeForEvents() {
        eventObserver = smppAdapter.getFilteredEventBus(SmppSendEvent.class);
        eventObserver.subscribe(new NotificationEventHandler());
    }

    protected abstract AbstractSendNotificationCommand createCommand(final SmsMessage smsMessage);

    protected RestOperations getRestTemplate() {
        return restTemplate;
    }

    private final class NotificationEventHandler extends Subscriber<SmppSendEvent> {

        @Override
        public void onNext(final SmppSendEvent event) {
            logger.debug(" >>> AbstractSendSmsNotificationHandlerService#onNext({})", event);
            final List<SmsMessage> existingMessages = getSmsMessages(event);
            logger.debug(" >> affected message {}", existingMessages);
            final SmsDeliveryStatus deliveryStatus;
            deliveryStatus = getSmsDeliveryStatus(event);
            for (final SmsMessage smsMessage : existingMessages) {
                smsMessage.setDeliveryStatus(deliveryStatus);
                smsMessage.setMessageId(getMessageId(event));
                logger.debug("Saving: {}", smsMessage);
                messageRepository.save(smsMessage);
                if (checkIfClientShouldBeNotified(deliveryStatus, smsMessage)) {
                    createCommand(smsMessage).observe().subscribe(data -> {
                        final ResponseEntity<String> responseEntity = data;
                        logger.debug("Response code: {}, Response Body: {}",
                                     responseEntity.getStatusCode(),
                                     responseEntity.getBody());
                    }, error -> logger.error("Error when attempting to send notification!", error));
                }
            }
        }

        private List<SmsMessage> getSmsMessages(final SmppInboundEvent event) {
            logger.debug("Finding messages for event: {}", event);
            final List<SmsMessage> existingMessages = new ArrayList<>();
            if (event instanceof SmppSendMessageIdEvent
                            && !(event instanceof SubmitSmResponse)) {
                logger.debug("Finding by message id");
                existingMessages.addAll(messageRepository.findByMessageId(getMessageId(event)));
            } else {
                logger.debug("Finding by sequence number");
                existingMessages.addAll(messageRepository.findBySequenceNumber(event.getSequenceNumber()));
            }
            return existingMessages;
        }

        private String getMessageId(final SmppInboundEvent event) {
            String result = null;
            if (event instanceof SmppSendMessageIdEvent) {
                result = ((SmppSendMessageIdEvent) event).getMessageId();
            }
            logger.debug(" <<< getMessageId: {}", result);
            return result;
        }

        private SmsDeliveryStatus getSmsDeliveryStatus(final SmppInboundEvent event) {
            SmsDeliveryStatus deliveryStatus;
            if (event instanceof SubmitSmResponse) {
                deliveryStatus = SmsDeliveryStatus.DELIVERED_TO_NETWORK;
            } else if (event instanceof DeliverySuccessfulNotification) {
                deliveryStatus = SmsDeliveryStatus.DELIVERED_TO_TERMINAL;
            } else {
                deliveryStatus = SmsDeliveryStatus.DELIVERY_IMPOSSIBLE;
            }
            return deliveryStatus;
        }

        private boolean checkIfClientShouldBeNotified(final SmsDeliveryStatus deliveryStatus,
                                                      final SmsMessage smsMessage) {
            return deliveryStatus.isNotifiable(checkIfDeliveryReceiptUrlDefined(smsMessage));
        }

        private boolean checkIfDeliveryReceiptUrlDefined(final SmsMessage smsMessage) {
            return smsMessage.getSmsCallbackData() != null
                            && StringUtils.isNotEmpty(smsMessage.getSmsCallbackData()
                                                                .getDeliveryReceiptUrl());
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
