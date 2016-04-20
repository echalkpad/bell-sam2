package com.vennetics.bell.sam.adapters.messaging.smpp.support;

import org.jboss.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cloudhopper.smpp.SmppConstants;
import com.cloudhopper.smpp.pdu.GenericNack;
import com.cloudhopper.smpp.pdu.Pdu;
import com.cloudhopper.smpp.pdu.PduRequest;
import com.cloudhopper.smpp.pdu.PduResponse;
import com.cloudhopper.smpp.pdu.SubmitSm;
import com.cloudhopper.smpp.pdu.SubmitSmResp;
import com.cloudhopper.smpp.simulator.SmppSimulatorPduProcessor;
import com.cloudhopper.smpp.simulator.SmppSimulatorSessionHandler;

public class SmppEchoSimulatorProcessor implements SmppSimulatorPduProcessor {

    private static final Logger logger = LoggerFactory.getLogger(SmppEchoSimulatorProcessor.class);
    public static final String ADDRESS_THAT_RETURNS_NACK = "+19999999988";

    @Override
    public boolean process(final SmppSimulatorSessionHandler session,
                           final Channel channel,
                           final Pdu pdu) {
        PduResponse response = null;
        if (pdu instanceof SubmitSm) {
            SubmitSm submit = (SubmitSm) pdu;
            logger.error(" >>> Responding to submitsm: " + submit.getDestAddress().getAddress());
            if (submit.getDestAddress().getAddress().equals(ADDRESS_THAT_RETURNS_NACK)) {
                logger.error(" >>> bad address");
                response = (GenericNack) submit.createGenericNack(SmppConstants.STATUS_SUBMITFAIL);
            } else {
                logger.error(" >>> Responding OK to submitsm!");
                response = (SubmitSmResp) submit.createResponse();
            }
        } else {
            logger.error(" >>> Responding to request: {}", pdu);
            response = ((PduRequest) pdu).createResponse();
            logger.error(" <<< response: {}", response);
        }
        session.addPduToWriteOnNextPduReceived(response);
        return true;
    }

}
