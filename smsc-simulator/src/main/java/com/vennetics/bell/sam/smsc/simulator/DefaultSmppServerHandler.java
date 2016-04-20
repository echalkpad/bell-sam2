package com.vennetics.bell.sam.smsc.simulator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.cloudhopper.smpp.SmppServerHandler;
import com.cloudhopper.smpp.SmppServerSession;
import com.cloudhopper.smpp.SmppSessionConfiguration;
import com.cloudhopper.smpp.pdu.BaseBind;
import com.cloudhopper.smpp.pdu.BaseBindResp;
import com.cloudhopper.smpp.type.SmppProcessingException;

/**
 * Handles bind and unbind events for the SMSC.
 * 
 * @author markcorkery
 *
 */
@Component
public class DefaultSmppServerHandler implements SmppServerHandler {

    @Value("${smpp.mo.addressRange:\\d{3}}")
    private String addressRange;

    private static final Logger LOG = LoggerFactory.getLogger(DefaultSmppServerHandler.class);

    @Override
    public void sessionBindRequested(final Long sessionId,
                                     final SmppSessionConfiguration sessionConfiguration,
                                     final BaseBind bindRequest) throws SmppProcessingException {
        sessionConfiguration.setName("Application.SMPP." + sessionConfiguration.getSystemId());
    }

    @Override
    public void sessionCreated(final Long sessionId,
                               final SmppServerSession session,
                               final BaseBindResp preparedBindResponse) throws SmppProcessingException {
        LOG.info("Session created: {}", session);
        session.serverReady(new ReceiptingSmppSessionHandler(session, addressRange));
    }

    @Override
    public void sessionDestroyed(final Long sessionId, final SmppServerSession session) {
        LOG.info("Session destroyed: {}", session);
        // print out final stats
        if (session.hasCounters()) {
            LOG.info(" final session rx-submitSM: {}", session.getCounters().getRxSubmitSM());
        }

        // make sure it's really shutdown
        session.destroy();
    }

}
