package com.vennetics.bell.sam.adapters.messaging.smpp.support.commands;

import com.cloudhopper.smpp.SmppConstants;
import com.cloudhopper.smpp.pdu.SubmitSm;
import com.cloudhopper.smpp.tlv.Tlv;
import com.vennetics.bell.sam.adapters.messaging.smpp.support.address.IAddressFormatter;
import com.vennetics.bell.sam.adapters.messaging.smpp.support.connection.ISmppConnectionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Hystrix command for submitting a binary message on a smppClient.
 */
public class SendBinarySubmitSmRequestCommand extends AbstractSubmitSmRequestCommand {

    private static final Logger logger = LoggerFactory.getLogger(SendBinarySubmitSmRequestCommand.class);

    private static final String COMMAND_NAME_KEY = "adapters.smpp.submitBinarySm";

    private static final String COMMAND_GROUP_KEY = "smppConnection";

    private final byte[] message;

    public SendBinarySubmitSmRequestCommand(final ISmppConnectionHandler connectionHandler,
                                            final String senderAddress,
                                            final String destinationAddress,
                                            final byte[] message,
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
        logger.debug(" >>> buildSubmitSmRequest ");
        final SubmitSm submitSm = new SubmitSm();
        submitSm.setDataCoding(SmppConstants.DATA_CODING_8BIT);
        submitSm.setReferenceObject(getDestinationAddress());
        // add delivery receipt
        submitSm.setRegisteredDelivery(SmppConstants.REGISTERED_DELIVERY_SMSC_RECEIPT_REQUESTED);
        addFormattedAddresses(submitSm);
        addMessagePayload(submitSm);
        logger.debug(" <<< buildSubmitSmRequest: {}", submitSm);
        return submitSm;
    }

    private void addMessagePayload(final SubmitSm submitSm) {
        logger.debug("Adding message with size: {}", message.length);
        Tlv tlv = new Tlv(SmppConstants.TAG_MESSAGE_PAYLOAD, message);
        submitSm.addOptionalParameter(tlv);
    }

}
