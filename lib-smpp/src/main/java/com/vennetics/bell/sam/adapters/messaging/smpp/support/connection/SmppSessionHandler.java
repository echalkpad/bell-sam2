package com.vennetics.bell.sam.adapters.messaging.smpp.support.connection;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cloudhopper.smpp.SmppConstants;
import com.cloudhopper.smpp.impl.DefaultSmppSessionHandler;
import com.cloudhopper.smpp.pdu.DeliverSm;
import com.cloudhopper.smpp.pdu.Pdu;
import com.cloudhopper.smpp.pdu.PduRequest;
import com.cloudhopper.smpp.pdu.PduResponse;
import com.cloudhopper.smpp.pdu.SubmitSmResp;
import com.cloudhopper.smpp.tlv.Tlv;
import com.cloudhopper.smpp.tlv.TlvConvertException;
import com.cloudhopper.smpp.type.Address;
import com.google.common.base.Charsets;
import com.vennetics.bell.sam.adapters.messaging.smpp.DeliverSmNotification;
import com.vennetics.bell.sam.adapters.messaging.smpp.DeliveryFailedNotification;
import com.vennetics.bell.sam.adapters.messaging.smpp.DeliverySuccessfulNotification;
import com.vennetics.bell.sam.adapters.messaging.smpp.SmppInboundEvent;
import com.vennetics.bell.sam.adapters.messaging.smpp.SubmitSmFailed;
import com.vennetics.bell.sam.adapters.messaging.smpp.SubmitSmResponse;
import com.vennetics.bell.sam.adapters.messaging.smpp.SubmitSmTimeout;
import com.vennetics.bell.sam.adapters.messaging.smpp.throttle.ITokenBucket;

import rx.subjects.Subject;

/**
 * This component is responsible for handling events from the SMSC
 *
 * @author markcorkery
 */
@Component
public class SmppSessionHandler extends DefaultSmppSessionHandler {
    private static final Logger logger = LoggerFactory.getLogger(SmppSessionHandler.class);
    private Subject<? super SmppInboundEvent, ? super SmppInboundEvent> inboundMessageBus;

    private ITokenBucket inBoundRateLimitingTokenBucket;

    private long throttlingRequestTimeoutMilliseconds;

    @Autowired
    public SmppSessionHandler(final Subject<? super SmppInboundEvent, ? super SmppInboundEvent> inboundMessageBus,
                              final ITokenBucket inBoundRateLimitingTokenBucket) {
        super();
        this.inboundMessageBus = inboundMessageBus;
        this.inBoundRateLimitingTokenBucket = inBoundRateLimitingTokenBucket;
    }

    @Override
    public void firePduRequestExpired(final PduRequest pduRequest) {
        logger.warn("PDU request expired: " + pduRequest);
        if (pduRequest.getCommandId() == SmppConstants.CMD_ID_SUBMIT_SM) {
            inboundMessageBus.onNext(new SubmitSmTimeout());
        }
    }

    @Override
    public PduResponse firePduRequestReceived(final PduRequest pduRequest) {
        logger.debug(" >>> firePduRequestReceived({})", pduRequest);

        if (inBoundRateIsExceeded()) {
            return throttlingRateExceededBackToSMSC(pduRequest);
        }

        if (pduRequest.getCommandId() == SmppConstants.CMD_ID_DELIVER_SM) {
            final DeliverSm deliverSm = (DeliverSm) pduRequest;
            final int sequenceNumber = pduRequest.getSequenceNumber();
            final PduResponse response = pduRequest.createResponse();
            if (isDeliveryReceipt(deliverSm)) {
                try {
                    handleDeliveryReceipt(deliverSm, sequenceNumber);
                } catch (final TlvConvertException tce) {
                    logger.error("Issue encountered handling the delivery receipt", tce);
                    response.setResultMessage(tce.getMessage());
                    response.setCommandStatus(SmppConstants.STATUS_UNKNOWNERR);
                }
            } else {
                handleMoMessage(deliverSm, sequenceNumber);
            }
            return response;
        }

        return super.firePduRequestReceived(pduRequest);

    }

    private boolean inBoundRateIsExceeded() {
        if (!inBoundRateLimitingTokenBucket.acquirePermit(throttlingRequestTimeoutMilliseconds,
                                                          TimeUnit.MILLISECONDS)) {
            logger.error("Inbound message rate [{} messages in {} milliseconds] exceeded",
                         inBoundRateLimitingTokenBucket.getMaximumTasksPerInterval(),
                         inBoundRateLimitingTokenBucket.getIntervalMilliseconds());
            return true;
        }

        return false;
    }

    private static PduResponse throttlingRateExceededBackToSMSC(final PduRequest pduRequest) {
        logger.error("Sending throttling error to SMSC");
        
        final PduResponse response = pduRequest.createResponse();
        response.setResultMessage("Inbound message rate exceeded");
        response.setCommandStatus(SmppConstants.STATUS_THROTTLED);
        
        return response;
    }

    @Override
    public boolean firePduReceived(final Pdu pdu) {
        logger.debug(" >>> firePduReceived({})", pdu);
        if (pdu.getCommandId() == SmppConstants.CMD_ID_SUBMIT_SM_RESP) {
            if (pdu.getCommandStatus() != SmppConstants.STATUS_OK) {
                broadcastEvent(new SubmitSmFailed(), pdu.getSequenceNumber());
            } else {
                broadcastEvent(new SubmitSmResponse(((SubmitSmResp) pdu).getMessageId()),
                               pdu.getSequenceNumber());
            }
        } else if (pdu.getCommandId() == SmppConstants.CMD_ID_GENERIC_NACK) {
            broadcastEvent(new SubmitSmFailed(), pdu.getSequenceNumber());
        } else {
            logger.debug("PDU received but no action taken here: {}", pdu);
        }
        return super.firePduReceived(pdu);
    }

    /**
     * Checks to see if this is a delivery receipt by validating the ESM Class
     * of the {@link DeliverSm}
     *
     * @param deliverSm
     *            Check this to see whether the ESM class indicates it is a
     *            delivery receipt.
     * @return <code>true></code> if this is a delivery receipt, otherwise
     *         <code>false</code>.
     */
    private static boolean isDeliveryReceipt(final DeliverSm deliverSm) {
        if (deliverSm.getEsmClass() == SmppConstants.ESM_CLASS_MT_SMSC_DELIVERY_RECEIPT) {
            return true;
        }
        return false;
    }

    /**
     * Handles this message as a delivery receipt
     *
     * @param dlr
     * @param sequenceNumber
     */
    private void handleDeliveryReceipt(final DeliverSm dlr,
                                       final int sequenceNumber) throws TlvConvertException {
        Tlv tlvReceiptId = dlr.getOptionalParameter(SmppConstants.TAG_RECEIPTED_MSG_ID);
        Tlv tlvState = dlr.getOptionalParameter(SmppConstants.TAG_MSG_STATE);
        final String outboundSequenceId = tlvReceiptId.getValueAsString();
        if (SmppConstants.STATE_DELIVERED == tlvState.getValueAsByte()) {
            broadcastEvent(new DeliverySuccessfulNotification(outboundSequenceId), sequenceNumber);
        } else {
            broadcastEvent(new DeliveryFailedNotification(outboundSequenceId), sequenceNumber);
        }
    }

    /**
     * Handles this message as an MO (mobile originating/inbound) message
     *
     * @param moMessage
     * @param sequenceNumber
     */
    private void handleMoMessage(final DeliverSm moMessage, final int sequenceNumber) {
        final Address sourceAddress = moMessage.getSourceAddress();
        final Address destAddress = moMessage.getDestAddress();
        final byte[] shortMessage = moMessage.getShortMessage();
        broadcastEvent(new DeliverSmNotification(new String(shortMessage, Charsets.ISO_8859_1),
                                                 sourceAddress.getAddress(),
                                                 destAddress.getAddress()),
                       sequenceNumber);
    }

    protected void broadcastEvent(final SmppInboundEvent event, final int sequenceNumber) {
        event.setSequenceNumber(sequenceNumber);
        logger.debug("broadcastEvent:({})", event);
        inboundMessageBus.onNext(event);
    }

}
