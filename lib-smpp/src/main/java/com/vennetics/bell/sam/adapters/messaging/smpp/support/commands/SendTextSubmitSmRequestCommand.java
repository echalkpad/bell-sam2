package com.vennetics.bell.sam.adapters.messaging.smpp.support.commands;

import com.cloudhopper.smpp.SmppConstants;
import com.cloudhopper.smpp.pdu.SubmitSm;
import com.cloudhopper.smpp.type.SmppInvalidArgumentException;
import com.google.common.base.Charsets;
import com.netflix.hystrix.exception.HystrixBadRequestException;
import com.vennetics.bell.sam.adapters.messaging.smpp.support.address.IAddressFormatter;
import com.vennetics.bell.sam.adapters.messaging.smpp.support.connection.ISmppConnectionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Hystrix command for submitting a plain text message on a smppClient.
 */
public class SendTextSubmitSmRequestCommand extends AbstractSubmitSmRequestCommand {

    private static final Logger logger = LoggerFactory.getLogger(SendTextSubmitSmRequestCommand.class);

    private static final String COMMAND_NAME_KEY = "adapters.smpp.submitTextSm";

    private static final String COMMAND_GROUP_KEY = "smppConnection";

    private final String message;

    public SendTextSubmitSmRequestCommand(final ISmppConnectionHandler connectionHandler,
                                          final String senderAddress,
                                          final String destinationAddress,
                                          final String message,
                                          final IAddressFormatter addressNormalizer) {
        super(COMMAND_GROUP_KEY,
              COMMAND_NAME_KEY,
              connectionHandler,
              senderAddress,
              destinationAddress,
              addressNormalizer);
        this.message = message;
    }

    @Override
    protected SubmitSm buildSubmitSmRequest() {
        final SubmitSm submitSm = new SubmitSm();
        submitSm.setDataCoding(SmppConstants.DATA_CODING_DEFAULT);
        submitSm.setReferenceObject(getDestinationAddress());
        // add delivery receipt
        submitSm.setRegisteredDelivery(SmppConstants.REGISTERED_DELIVERY_SMSC_RECEIPT_REQUESTED);
        addFormattedAddresses(submitSm);
        addShortMessage(submitSm);
        return submitSm;
    }

    private void addShortMessage(final SubmitSm submitSm) {
        try {
            submitSm.setShortMessage(message.getBytes(Charsets.ISO_8859_1));
        } catch (final SmppInvalidArgumentException siae) {
            logger.error("Setting short message failed!: {}", siae.getMessage());
            throw new HystrixBadRequestException("There was an issue with a supplied message",
                                                 siae);
        }
    }

}
