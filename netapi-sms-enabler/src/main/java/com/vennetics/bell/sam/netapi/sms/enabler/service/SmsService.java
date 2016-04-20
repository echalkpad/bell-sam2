package com.vennetics.bell.sam.netapi.sms.enabler.service;

import com.vennetics.bell.sam.adapters.messaging.smpp.ISmppAdapter;
import com.vennetics.bell.sam.adapters.messaging.smpp.SequenceNumberPair;
import com.vennetics.bell.sam.sms.common.address.Address;
import com.vennetics.bell.sam.sms.common.address.IAddressNormalizer;
import com.vennetics.bell.sam.sms.common.dao.NotificationFormat;
import com.vennetics.bell.sam.sms.common.dao.SmsCallbackData;
import com.vennetics.bell.sam.sms.common.dao.SmsDeliveryStatus;
import com.vennetics.bell.sam.sms.common.dao.SmsMessage;
import com.vennetics.bell.sam.sms.common.dao.SmsMessageRepository;
import generated.oma.xml.rest.netapi.common._1.CallbackReference;
import generated.oma.xml.rest.netapi.sms._1.DeliveryInfo;
import generated.oma.xml.rest.netapi.sms._1.DeliveryInfoList;
import generated.oma.xml.rest.netapi.sms._1.DeliveryStatus;
import generated.oma.xml.rest.netapi.sms._1.OutboundSMSMessageRequest;
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
    public Observable<OutboundSMSMessageRequest> sendSmsMessage(final UUID externalRequestId,
                                                                final String senderAddress,
                                                                final OutboundSMSMessageRequest smsMessageRequest) {
        logger.debug(">>> sendBinaryMessage({}, {})" + smsMessageRequest,
                     smsMessageRequest.getAddresses());

        final List<Address> normalizedDestinations = normalizeAddresses(smsMessageRequest.getAddresses());

        if (smsMessageRequest.getOutboundSMSBinaryMessage() != null) {
            final List<Observable<SequenceNumberPair>> observables = processSmsBinaryRequest(externalRequestId,
                                                                                             smsMessageRequest,
                                                                                             normalizedDestinations);
            Observable.merge(observables)
                      .subscribe(new SendSmsResultsHandler(externalRequestId, smsMessageRequest));

        }
        populateWithDefaultDeliveryInfoList(smsMessageRequest);
        return Observable.just(smsMessageRequest);
    }

    private List<Observable<SequenceNumberPair>> processSmsBinaryRequest(final UUID externalRequestId,
                                                                         final OutboundSMSMessageRequest message,
                                                                         final List<Address> destinationAddresses) {
        final List<Observable<SequenceNumberPair>> observables = new ArrayList<>(destinationAddresses.size());
        for (final Address destinationAddress : destinationAddresses) {
            final Observable<SequenceNumberPair> observable = smppAdapter.submitSmBinary(message.getSenderAddress(),
                                                                                         destinationAddress.getE164Address(),
                                                                                         message.getOutboundSMSBinaryMessage()
                                                                                                .getMessage())
                                                                         .onErrorResumeNext(new SendSmsErrorHandler(externalRequestId,
                                                                                                                    message,
                                                                                                                    destinationAddress.getE164Address()));
            observables.add(observable);
        }
        return observables;
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

    /**
     * Build up the {@link DeliveryInfoList} object based on input from
     * {@link OutboundSMSMessageRequest}
     *
     * @param smsMessageRequest
     * @return
     */
    private static void populateWithDefaultDeliveryInfoList(final OutboundSMSMessageRequest smsMessageRequest) {
        final DeliveryInfoList deliveryInfoList = new DeliveryInfoList();

        for (final String destinationAddress : smsMessageRequest.getAddresses()) {
            final DeliveryStatus deliveryStatus = DeliveryStatus.MESSAGE_WAITING;
            addDeliveryInfoToList(deliveryInfoList, deliveryStatus, destinationAddress);
        }
        smsMessageRequest.setDeliveryInfoList(deliveryInfoList);
    }

    private void persistMessageRecord(final UUID externalRequestId,
                                      final String senderAddress,
                                      final OutboundSMSMessageRequest smsMessageRequest,
                                      final String destinationAddress,
                                      final DeliveryStatus deliveryStatus,
                                      final int sequenceNumber) {
        final SmsMessage smsMessage = new SmsMessage();
        smsMessage.setBinary(true);
        smsMessage.setClientCorrelator(smsMessageRequest.getClientCorrelator());
        smsMessage.setContent(smsMessageRequest.getOutboundSMSBinaryMessage() != null
                        ? smsMessageRequest.getOutboundSMSBinaryMessage().getMessage() : null);
        if (smsMessageRequest.getReceiptRequest() != null) {
            final CallbackReference receiptRequest = smsMessageRequest.getReceiptRequest();
            final SmsCallbackData smsCallbackData = new SmsCallbackData();
            smsCallbackData.setCallbackData(receiptRequest.getCallbackData());
            smsCallbackData.setDeliveryReceiptUrl(receiptRequest.getNotifyURL());
            smsCallbackData.setNotificationFormat(receiptRequest.getNotificationFormat() == null
                            ? null
                            : NotificationFormat.valueOf(receiptRequest.getNotificationFormat()
                                                                       .toString()));
            smsMessage.setSmsCallbackData(smsCallbackData);
        }
        smsMessage.setDeliveryStatus(SmsDeliveryStatus.fromValue(deliveryStatus.value()));
        smsMessage.setDestinationAddress(destinationAddress);
        smsMessage.setExternalRequestId(externalRequestId);
        smsMessage.setSequenceNumber(sequenceNumber);
        smsMessage.setSourceAddress(senderAddress);
        logger.debug(" Saving: {}", smsMessage);
        smsMessageRepository.save(smsMessage);
    }

    private static void addDeliveryInfoToList(final DeliveryInfoList deliveryInfoList,
                                              final DeliveryStatus deliveryStatus,
                                              final String destinationAddress) {
        final DeliveryInfo deliveryInfo = new DeliveryInfo();
        deliveryInfo.setAddress(destinationAddress);
        deliveryInfo.setDeliveryStatus(deliveryStatus);
        deliveryInfoList.getDeliveryInfos().add(deliveryInfo);
    }

    private final class SendSmsResultsHandler extends Subscriber<SequenceNumberPair> {
        private UUID externalSequenceId;
        private OutboundSMSMessageRequest smsMessageRequest;

        private SendSmsResultsHandler(final UUID externalSequenceId,
                                      final OutboundSMSMessageRequest smsMessageRequest) {
            this.externalSequenceId = externalSequenceId;
            this.smsMessageRequest = smsMessageRequest;
        }

        @Override
        public void onNext(final SequenceNumberPair item) {
            if (item != null && StringUtils.isNotEmpty(item.getDestinationAddress())) {
                persistMessageRecord(externalSequenceId,
                                     smsMessageRequest.getSenderAddress(),
                                     smsMessageRequest,
                                     item.getDestinationAddress(),
                                     DeliveryStatus.MESSAGE_WAITING,
                                     item.getSmppSequenceNumber());
            }
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

    private final class SendSmsErrorHandler implements
                                            Func1<Throwable, Observable<SequenceNumberPair>> {
        private UUID externalSequenceId;
        private OutboundSMSMessageRequest smsMessageRequest;
        private String destinationAddress;

        private SendSmsErrorHandler(final UUID externalSequenceId,
                                    final OutboundSMSMessageRequest smsMessageRequest,
                                    final String destinationAddress) {
            this.externalSequenceId = externalSequenceId;
            this.smsMessageRequest = smsMessageRequest;
            this.destinationAddress = destinationAddress;
        }

        @Override
        public Observable<SequenceNumberPair> call(final Throwable e) {
            logger.error("Exception received: {}/{}", e.getMessage(), e);
            persistMessageRecord(externalSequenceId,
                                 smsMessageRequest.getSenderAddress(),
                                 smsMessageRequest,
                                 destinationAddress,
                                 DeliveryStatus.DELIVERY_IMPOSSIBLE,
                                 0);
            return Observable.just(new SequenceNumberPair());
        }
    }
}
