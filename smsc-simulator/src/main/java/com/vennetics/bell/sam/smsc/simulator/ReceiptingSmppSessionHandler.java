package com.vennetics.bell.sam.smsc.simulator;

import com.cloudhopper.commons.charset.CharsetUtil;
import com.cloudhopper.commons.util.windowing.WindowFuture;
import com.cloudhopper.smpp.SmppConstants;
import com.cloudhopper.smpp.SmppSession;
import com.cloudhopper.smpp.impl.DefaultSmppSessionHandler;
import com.cloudhopper.smpp.pdu.DeliverSm;
import com.cloudhopper.smpp.pdu.DeliverSmResp;
import com.cloudhopper.smpp.pdu.PduRequest;
import com.cloudhopper.smpp.pdu.PduResponse;
import com.cloudhopper.smpp.pdu.SubmitSm;
import com.cloudhopper.smpp.pdu.SubmitSmResp;
import com.cloudhopper.smpp.tlv.Tlv;
import com.cloudhopper.smpp.tlv.TlvConvertException;
import com.cloudhopper.smpp.type.SmppInvalidArgumentException;
import com.cloudhopper.smpp.util.DeliveryReceipt;
import com.cloudhopper.smpp.util.TlvUtil;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.ref.WeakReference;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Pattern;

/**
 * Handles events received on an SmppSession. It responds with delivery
 * receipts.
 *
 * @author markcorkery
 */
public class ReceiptingSmppSessionHandler extends DefaultSmppSessionHandler {

    private static final Logger logger = LoggerFactory.getLogger(ReceiptingSmppSessionHandler.class);

    private WeakReference<SmppSession> sessionRef;

    private ScheduledExecutorService executor;

    private AtomicLong messageId = new AtomicLong();

    private Random rand;

    private Pattern pattern = null;

    public ReceiptingSmppSessionHandler(final SmppSession session, final String addressRange) {
        this.sessionRef = new WeakReference<>(session);
        executor = Executors.newScheduledThreadPool(1);
        rand = new Random();
        if (StringUtils.isNotBlank(addressRange)) {
            pattern = Pattern.compile(addressRange);
        }
    }

    @Override
    public PduResponse firePduRequestReceived(final PduRequest pduRequest) {
        SmppSession session = sessionRef.get();
        final PduResponse response = pduRequest.createResponse();
        if (pduRequest.getCommandId() == SmppConstants.CMD_ID_SUBMIT_SM) {
            delay();
            SubmitSm mt = (SubmitSm) pduRequest;
            ((SubmitSmResp) response).setMessageId(String.valueOf(messageId.incrementAndGet()));
            if (mt.getRegisteredDelivery()
                            == SmppConstants.REGISTERED_DELIVERY_SMSC_RECEIPT_REQUESTED) {
                logger.error("Receipting Requested");
                final Receiptor fireReceipt = new Receiptor(session,
                                                            mt,
                                                            String.valueOf(messageId.get()));
                executor.schedule(fireReceipt, 1, TimeUnit.SECONDS);
            }
            if (doesDestinationMatchMoPattern(mt)) {
                logger.error("Mo Message Requested");
                final Sender fireReceipt = new Sender(session, mt);
                executor.schedule(fireReceipt, 2, TimeUnit.SECONDS);
            }
        }

        return response;
    }

    /**
     * Check to see if the destination number matches the address range pattern
     *
     * @param mt
     * @return <code>true</code> if patterm match is found, otherwise <code>false</code> is
     * returned
     */
    private boolean doesDestinationMatchMoPattern(final SubmitSm mt) {
        return pattern != null && pattern.matcher(mt.getDestAddress().getAddress()).find();
    }

    /**
     * Random delay generator to simulate traffic delays from real SMS-C
     */
    private void delay() {
        try {
            Thread.sleep((int) ((Math.abs(2 + rand.nextGaussian() * 15)) * 100));
        } catch (InterruptedException e) {
            logger.error("Error attempting random delay", e);
        }
    }

    public static class PduDispatcher {

        private final SmppSession session;

        public PduDispatcher(final SmppSession session) {
            this.session = session;
        }

        protected void sendRequestPdu(final DeliverSm deliver) {
            try {
                WindowFuture<Integer, PduRequest, PduResponse> future = session.sendRequestPdu(
                                deliver,
                                10000,
                                false);
                if (!future.await()) {
                    logger.error("Failed to receive deliver_sm_resp within specified time");
                } else if (future.isSuccess()) {
                    DeliverSmResp deliverSmResp = (DeliverSmResp) future.getResponse();
                    logger.info("deliver_sm_resp: commandStatus ["
                                                + deliverSmResp.getCommandStatus() + "="
                                                + deliverSmResp.getResultMessage() + "]");
                } else {
                    logger.error("Failed to properly receive deliver_sm_resp: "
                                                 + future.getCause());
                }
            } catch (final Exception ex) {
                logger.info("Send error", ex);
            }
        }

    }

    public static class Sender extends PduDispatcher implements Runnable {

        private final SubmitSm mt;

        public Sender(final SmppSession session, final SubmitSm mt) {
            super(session);
            this.mt = mt;
        }

        @Override
        public void run() {
            logger.error("Attempt to deliver!");
            try {
                sendMoMessage();
            } catch (SmppInvalidArgumentException e) {
                logger.error("Error occured during message sending!", e);
            }
        }

        protected void sendMoMessage() throws SmppInvalidArgumentException {
            logger.error("Message delivery for original sequence number: {}/{}",
                         mt.getSequenceNumber(),
                         Integer.toHexString(mt.getSequenceNumber()));
            DeliverSm deliver = new DeliverSm();
            deliver.setSourceAddress(mt.getSourceAddress());
            deliver.setDestAddress(mt.getDestAddress());
            deliver.setDataCoding(mt.getDataCoding());
            deliver.setShortMessage(mt.getShortMessage());
            deliver.calculateAndSetCommandLength();
            sendRequestPdu(deliver);
        }

    }

    public static class Receiptor extends PduDispatcher implements Runnable {

        private final SubmitSm mt;
        private final String messageId;

        public Receiptor(final SmppSession session, final SubmitSm mt, final String messageId) {
            super(session);
            this.mt = mt;
            this.messageId = messageId;
        }

        @Override
        public void run() {
            logger.error("Attempt to deliver!");
            try {
                sendDeliveryReceipt();
            } catch (SmppInvalidArgumentException | TlvConvertException e) {
                logger.error("Error occured during receipt sending!", e);
            }
        }

        protected void sendDeliveryReceipt() throws
                                             SmppInvalidArgumentException,
                                             TlvConvertException {
            logger.error("Receipted delivery for original sequence number: {}/{}",
                         mt.getSequenceNumber(),
                         Integer.toHexString(mt.getSequenceNumber()));
            DeliverSm deliver = new DeliverSm();
            deliver.setEsmClass(SmppConstants.ESM_CLASS_MT_SMSC_DELIVERY_RECEIPT);
            deliver.setSourceAddress(mt.getSourceAddress());
            deliver.setDestAddress(mt.getDestAddress());
            deliver.setDataCoding(mt.getDataCoding());

            DeliveryReceipt deliveryReceipt = new DeliveryReceipt(Integer.toString(mt.getSequenceNumber()),
                                                                  1,
                                                                  1,
                                                                  new DateTime(),
                                                                  new DateTime(),
                                                                  SmppConstants.STATE_DELIVERED,
                                                                  0,
                                                                  "-");
            String shortMessage = deliveryReceipt.toShortMessage();
            deliver.setShortMessage(CharsetUtil.encode(shortMessage, CharsetUtil.CHARSET_GSM));
            deliver.addOptionalParameter(TlvUtil.createNullTerminatedStringTlv(SmppConstants.TAG_RECEIPTED_MSG_ID,
                                                                               messageId));
            deliver.addOptionalParameter(new Tlv(SmppConstants.TAG_MSG_STATE,
                                                 new byte[] { SmppConstants.STATE_DELIVERED }));

            deliver.calculateAndSetCommandLength();
            sendRequestPdu(deliver);
        }

    }
}
