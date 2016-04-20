package com.vennetics.bell.sam.adapters.messaging.smpp.support.commands;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.cloudhopper.commons.util.windowing.WindowFuture;
import com.cloudhopper.smpp.pdu.PduRequest;
import com.cloudhopper.smpp.pdu.PduResponse;
import com.cloudhopper.smpp.pdu.SubmitSm;
import com.cloudhopper.smpp.type.RecoverablePduException;
import com.cloudhopper.smpp.type.SmppChannelException;
import com.cloudhopper.smpp.type.SmppTimeoutException;
import com.cloudhopper.smpp.type.UnrecoverablePduException;
import com.vennetics.bell.sam.adapters.messaging.smpp.SequenceNumberPair;
import com.vennetics.bell.sam.adapters.messaging.smpp.exception.SendSmppException;
import com.vennetics.bell.sam.adapters.messaging.smpp.support.address.IAddressFormatter;
import com.vennetics.bell.sam.adapters.messaging.smpp.support.connection.ISmppConnectionHandler;
import com.vennetics.bell.sam.adapters.messaging.smpp.throttle.ITokenBucket;
import com.vennetics.bell.sam.core.commands.hystrix.AbstractHystrixApiCommand;
import com.vennetics.bell.sam.error.exceptions.InvalidInputValueException;

/**
 * Created on 09/02/2016.
 */
public abstract class AbstractSubmitSmRequestCommand extends
                                                     AbstractHystrixApiCommand<SequenceNumberPair> {

    private static final Logger logger = LoggerFactory.getLogger(AbstractSubmitSmRequestCommand.class);

    private final ISmppConnectionHandler connectionHandler;

    private final String destinationAddress;

    private final String senderAddress;

    private final IAddressFormatter addressFormatter;

    @Autowired
    private long submitMessageTimeoutMilliseconds;

    public AbstractSubmitSmRequestCommand(final String commandGroupKey,
                                          final String commandNameKey,
                                          final ISmppConnectionHandler connectionHandler,
                                          final String senderAddress,
                                          final String destinationAddress,
                                          final IAddressFormatter addressNormalizer) {
        super(commandGroupKey, commandNameKey);
        this.connectionHandler = connectionHandler;
        this.senderAddress = senderAddress;
        this.destinationAddress = destinationAddress;
        this.addressFormatter = addressNormalizer;
    }

    @Override
    protected SequenceNumberPair runCommand() {
        final SubmitSm submitSm = buildSubmitSmRequest();
        WindowFuture<Integer, PduRequest, PduResponse> response = submitRequest(submitSm);
        final SequenceNumberPair result = createSequenceNumber(response, submitSm);
        logger.debug("Result : {}", result);
        return result;
    }

    private static SequenceNumberPair createSequenceNumber(final WindowFuture<Integer, PduRequest, PduResponse> response,
                                                           final SubmitSm submitSm) {
        logger.debug(" >>> createSequenceNumber: {} / {}", response, submitSm);
        final int sequenceNumber = response.getRequest().getSequenceNumber();
        final SequenceNumberPair result = new SequenceNumberPair();
        result.setDestinationAddress(submitSm.getDestAddress().getAddress());
        result.setSmppSequenceNumber(sequenceNumber);
        logger.debug(" <<< createSequenceNumber: {}/{}",
                     result.getDestinationAddress(),
                     result.getSmppSequenceNumber());
        return result;
    }

    private WindowFuture<Integer, PduRequest, PduResponse> submitRequest(final SubmitSm submitSm) {
        logger.debug(" >>> submitRequest: {}", submitSm);

        checkThrottlingRate(submitSm);

        WindowFuture<Integer, PduRequest, PduResponse> response;
        try {
            response = connectionHandler.getSmppSession().sendRequestPdu(submitSm,
                                                                         submitMessageTimeoutMilliseconds,
                                                                         false);
        } catch (final RecoverablePduException rde) {
            logger.error("Error with sending message", rde);
            throw new InvalidInputValueException(String.valueOf(submitSm.getShortMessage()));
        } catch (final UnrecoverablePduException | SmppTimeoutException | SmppChannelException
                        | InterruptedException ex) {
            logger.error("Send failed!: {}", ex.getMessage());
            throw new SendSmppException(submitSm.getDestAddress().getAddress(), ex);
        }
        logger.debug(" <<< submitRequest: {}", response);
        return response;
    }

    private void checkThrottlingRate(final SubmitSm submitSm) {
        ITokenBucket tokenBucket = connectionHandler.getRateLimitingTokenBucket();

        if (!tokenBucket.acquirePermit(connectionHandler.getOutBoundThrottlingRequestTimeoutMilliseconds(),
                                       TimeUnit.MILLISECONDS)) {

            long messages = tokenBucket.getMaximumTasksPerInterval();
            long time = tokenBucket.getIntervalMilliseconds();

            logger.error("Could not send message due to rate [{} messages per {} milliseconds] exceeded.",
                         messages,
                         time);

            throw new SendSmppException(submitSm.getDestAddress().getAddress());
        }
    }

    protected void addFormattedAddresses(final SubmitSm submitSm) {
        submitSm.setDestAddress(addressFormatter.formatDestinationAddress(destinationAddress));
        submitSm.setSourceAddress(addressFormatter.formatSourceAddress(senderAddress));
    }

    protected abstract SubmitSm buildSubmitSmRequest();

    public String getDestinationAddress() {
        return destinationAddress;
    }

}
