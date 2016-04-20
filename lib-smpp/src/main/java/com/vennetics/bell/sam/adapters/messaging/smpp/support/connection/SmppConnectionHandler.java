package com.vennetics.bell.sam.adapters.messaging.smpp.support.connection;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.cloudhopper.smpp.SmppClient;
import com.cloudhopper.smpp.SmppSession;
import com.cloudhopper.smpp.SmppSessionConfiguration;
import com.cloudhopper.smpp.SmppSessionListener;
import com.cloudhopper.smpp.pdu.EnquireLink;
import com.cloudhopper.smpp.pdu.EnquireLinkResp;
import com.vennetics.bell.sam.adapters.messaging.smpp.throttle.ITokenBucket;

/**
 * Factory class for managing the SMPP connection.
 *
 * @author markcorkery
 */
@Component
public class SmppConnectionHandler implements ISmppConnectionHandler {
    private static final Logger LOG = LoggerFactory.getLogger(SmppConnectionHandler.class);

    @Autowired
    private long enquireLinkTimeoutMilliseconds;
    
    @Autowired
    private long outBoundThrottlingRequestTimeoutMilliseconds;
    
    @Autowired
    private long smppUnbindTimeoutInMilliseconds;

    private final SmppClient smppClient;

    private final SmppSessionConfiguration smppSessionConfiguration;

    private SmppSession session = null;

    private final SmppSessionListener smppSessionHandler;

    private final ITokenBucket rateLimitingTokenBucket;

    @Autowired
    public SmppConnectionHandler(final SmppSessionListener smppSessionListener,
                                 final SmppSessionConfiguration smppSessionConfiguration,
                                 final SmppClient smppClient,
                                 final ITokenBucket outBoundRateLimitingTokenBucket) {
        smppSessionHandler = smppSessionListener;
        this.smppSessionConfiguration = smppSessionConfiguration;
        this.smppClient = smppClient;
        this.rateLimitingTokenBucket = outBoundRateLimitingTokenBucket;
    }

    @PostConstruct
    public void init() {
        sendBindRequest();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vennetics.bell.sam.adapters.messaging.smpp.support.connection.
     * ISmppConnectionHandler#getSmppSession()
     */
    @Override
    public SmppSession getSmppSession() {
        return session;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vennetics.bell.sam.adapters.messaging.smpp.support.connection.
     * ISmppConnectionHandler#pollAndReconnect()
     */
    @Override
    @Scheduled(fixedDelayString = "${smpp.enquireLinkPollingInterval}")
    public void pollAndReconnect() {
        if (session == null || !session.isBound() && !session.isBinding() && !session.isOpen()) {
            sendBindRequest();
        } else if (session.isBound()) {
            sendEnquireLinkRequest();
        } else if (session.isClosed() || session.isUnbinding()) {
            resetSession();
        }
    }

    private void sendBindRequest() {
        try {
            session = smppClient.bind(smppSessionConfiguration, smppSessionHandler);
        } catch (final Exception e) {
            LOG.error("Bind failed: " + e.getMessage(), e);
        }
    }

    private void sendEnquireLinkRequest() {
        try {
            final EnquireLinkResp enquireLinkResp = session.enquireLink(new EnquireLink(),
                                                                        enquireLinkTimeoutMilliseconds);
            LOG.debug("enquireLinkResp: status " + enquireLinkResp.getCommandStatus());
        } catch (final Exception e) {
            LOG.error(e.getMessage(), e);
            resetSession();
        }
    }

    private void resetSession() {
        if (session != null) {
            session.unbind(smppUnbindTimeoutInMilliseconds);
        }
    }

    @Override
    public ITokenBucket getRateLimitingTokenBucket() {
        return rateLimitingTokenBucket;
    }

    @Override
    public long getOutBoundThrottlingRequestTimeoutMilliseconds() {
        return outBoundThrottlingRequestTimeoutMilliseconds;
    }

}
