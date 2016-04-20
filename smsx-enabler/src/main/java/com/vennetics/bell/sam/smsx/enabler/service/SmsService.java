package com.vennetics.bell.sam.smsx.enabler.service;

import com.google.common.base.Charsets;
import com.vennetics.bell.sam.adapters.messaging.smpp.ISmppAdapter;
import com.vennetics.bell.sam.adapters.messaging.smpp.SequenceNumberPair;
import com.vennetics.bell.sam.model.smsx.DeliveryStatusResponse;
import com.vennetics.bell.sam.model.smsx.MessageResponse;
import com.vennetics.bell.sam.sms.common.address.Address;
import com.vennetics.bell.sam.sms.common.address.IAddressNormalizer;
import com.vennetics.bell.sam.sms.common.dao.SmsCallbackData;
import com.vennetics.bell.sam.sms.common.dao.SmsDeliveryStatus;
import com.vennetics.bell.sam.sms.common.dao.SmsMessage;
import com.vennetics.bell.sam.sms.common.dao.SmsMessageRepository;
import com.vennetics.bell.sam.smsx.enabler.SendSmsRequest;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class SmsService implements ISmsService {

    private static final Logger logger = LoggerFactory.getLogger(SmsService.class);

    private ISmppAdapter smppAdapter;

    private SmsMessageRepository smsMessageRepository;

    private IAddressNormalizer addressNormalizer;

    @Autowired
    public SmsService(final ISmppAdapter smppAdapter,
                      final SmsMessageRepository smsMessageRepository,
                      final IAddressNormalizer addressNormalizer) {
        this.smppAdapter = smppAdapter;
        this.smsMessageRepository = smsMessageRepository;
        this.addressNormalizer = addressNormalizer;
    }

    @Override
    public Observable<Void> sendSmsMessage(final UUID externalRequestId,
                                           final SendSmsRequest outboundMessage) {
        final List<Address> normalizedDestinations = normalizeAddresses(outboundMessage.getAddresses());
        if (StringUtils.isNotBlank(outboundMessage.getMessage())) {
            processSmsTextRequest(externalRequestId, outboundMessage, normalizedDestinations);
        }
        return Observable.just(null);
    }

    @Override
    public Observable<DeliveryStatusResponse> getDeliveryStatus(final String messageId) {
        final DeliveryStatusResponse response = new DeliveryStatusResponse();
        try {
            final UUID externalMessageId = UUID.fromString(messageId);
            List<SmsMessage> messages = smsMessageRepository.findByExternalRequestId(
                            externalMessageId);
            for (final SmsMessage message : messages) {
                final MessageResponse messageResponse = new MessageResponse();
                messageResponse.setAddress(message.getDestinationAddress());
                messageResponse.setStatus(message.getDeliveryStatus().value());
                response.getMessageResponses().add(messageResponse);
            }

        } catch (final IllegalArgumentException iae) {
            logger.error("{} could not be parsed as UUID", messageId, iae);
        }
        return Observable.just(response);

    }

    private void processSmsTextRequest(final UUID externalRequestId,
                                       final SendSmsRequest outboundMessage,
                                       final List<Address> destinationAddresses) {
        final SendSmsResultsHandler resultHandler = new SendSmsResultsHandler(externalRequestId,
                                                                              outboundMessage,
                                                                              destinationAddresses);
        for (final Address destinationAddress : destinationAddresses) {
            final Observable<SequenceNumberPair> observable = smppAdapter.submitSmText(
                            outboundMessage.getSenderAddress(),
                            destinationAddress.getE164Address(),
                            outboundMessage.getMessage())
                                                                         .onErrorResumeNext(new SendSmsErrorHandler(
                                                                                         externalRequestId,
                                                                                         outboundMessage,
                                                                                         destinationAddress
                                                                                                         .getSuppliedAddress()));
            observable.subscribe(resultHandler);
        }
    }

    private List<Address> normalizeAddresses(final List<String> suppliedAddresses) {
        final List<Address> result = new ArrayList<>();
        for (final String suppliedAddress : suppliedAddresses) {
            final Address preNormalizedAddress = new Address();
            preNormalizedAddress.setSuppliedAddress(suppliedAddress);
            result.add(addressNormalizer.normalizeToE164Address(preNormalizedAddress));
        }
        return result;
    }

    private void persistMessageRecord(final UUID externalRequestId,
                                      final SendSmsRequest outboundMessage,
                                      final String destinationAddress,
                                      final SmsDeliveryStatus deliveryStatus,
                                      final int sequenceNumber) {
        final SmsMessage smsMessage = new SmsMessage();
        smsMessage.setBinary(false);
        smsMessage.setContent(outboundMessage.getMessage().getBytes(Charsets.UTF_8));
        if (StringUtils.isNotBlank(outboundMessage.getNotifyUrl())) {
            final SmsCallbackData smsCallbackData = new SmsCallbackData();
            smsCallbackData.setCallbackData(outboundMessage.getCorrelator());
            smsCallbackData.setDeliveryReceiptUrl(outboundMessage.getNotifyUrl());
            smsMessage.setSmsCallbackData(smsCallbackData);
        }
        smsMessage.setDeliveryStatus(deliveryStatus);
        smsMessage.setDestinationAddress(destinationAddress);
        smsMessage.setExternalRequestId(externalRequestId);
        smsMessage.setSequenceNumber(sequenceNumber);
        smsMessage.setSourceAddress(outboundMessage.getSenderAddress());
        logger.debug(" Saving: {}", smsMessage);
        smsMessageRepository.save(smsMessage);
    }

    private final class SendSmsResultsHandler extends Subscriber<SequenceNumberPair> {
        private final UUID externalSequenceId;
        private final SendSmsRequest outboundMessage;
        private final List<Address> normalizedAddresses;

        private SendSmsResultsHandler(final UUID externalSequenceId,
                                      final SendSmsRequest outboundMessage,
                                      final List<Address> normalizedAddresses) {
            this.externalSequenceId = externalSequenceId;
            this.outboundMessage = outboundMessage;
            this.normalizedAddresses = normalizedAddresses;
        }

        @Override
        public void onNext(final SequenceNumberPair item) {
            if (item != null && StringUtils.isNotEmpty(item.getDestinationAddress())) {
                persistMessageRecord(externalSequenceId,
                                     outboundMessage,
                                     getAddressToPersist(item),
                                     SmsDeliveryStatus.MESSAGE_WAITING,
                                     item.getSmppSequenceNumber());
            }
        }

        private String getAddressToPersist(final SequenceNumberPair item) {
            for (final Address address : normalizedAddresses) {
                if (StringUtils.equals(item.getDestinationAddress(), address.getE164Address())) {
                    return address.getSuppliedAddress();
                }
            }
            return item.getDestinationAddress();
        }

        @Override
        public void onError(final Throwable error) {
            logger.error("Error encountered while attempting to send SMS.", error);
        }

        @Override
        public void onCompleted() {
            logger.debug("Sequence complete.");
        }
    }

    private final class SendSmsErrorHandler
                    implements Func1<Throwable, Observable<SequenceNumberPair>> {
        private final UUID externalSequenceId;
        private final String destinationAddress;
        private final SendSmsRequest outboundMessage;

        private SendSmsErrorHandler(final UUID externalSequenceId,
                                    final SendSmsRequest outboundMessage,
                                    final String destinationAddress) {
            this.externalSequenceId = externalSequenceId;
            this.outboundMessage = outboundMessage;
            this.destinationAddress = destinationAddress;
        }

        @Override
        public Observable<SequenceNumberPair> call(final Throwable e) {
            logger.error("Exception received: {}/{}", e.getMessage(), e);
            persistMessageRecord(externalSequenceId,
                                 outboundMessage,
                                 destinationAddress,
                                 SmsDeliveryStatus.DELIVERY_IMPOSSIBLE,
                                 0);

            return Observable.just(new SequenceNumberPair());
        }
    }
}
