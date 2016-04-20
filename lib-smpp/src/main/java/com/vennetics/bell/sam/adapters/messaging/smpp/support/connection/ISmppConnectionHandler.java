package com.vennetics.bell.sam.adapters.messaging.smpp.support.connection;

import com.cloudhopper.smpp.SmppSession;
import com.vennetics.bell.sam.adapters.messaging.smpp.throttle.ITokenBucket;

/**
 * Interface definition for SmppConnection handling.
 */
public interface ISmppConnectionHandler {

    /**
     * Fetch the session that is bound to the SMSC
     *
     * @return The active session.
     */
    SmppSession getSmppSession();

    /**
     * Polls the health of the connection:
     * <p>
     * If the smppClient is not bound to the SMSC then it will attempt to
     * rebind.
     * <p>
     * If the smppClient is bound then it will send the enquire link in order to
     * maintain the connection.
     * <p>
     * If the bind is in a closing state, then it will unbind the smppClient
     * from the SMSC.
     */
    void pollAndReconnect();
    
    ITokenBucket getRateLimitingTokenBucket();

    long getOutBoundThrottlingRequestTimeoutMilliseconds();

}
