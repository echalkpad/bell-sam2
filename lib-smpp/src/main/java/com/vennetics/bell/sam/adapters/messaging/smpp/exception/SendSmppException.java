package com.vennetics.bell.sam.adapters.messaging.smpp.exception;

import com.vennetics.bell.sam.adapters.messaging.smpp.SmppAdapterErrorMessageTypes;
import com.vennetics.bell.sam.core.exception.ServiceAffectingException;

public class SendSmppException extends ServiceAffectingException {

    private static final long serialVersionUID = 3353808240488144914L;

    private final String address;
    
    public SendSmppException(final String address) {
        super(SmppAdapterErrorMessageTypes.SMPP_ERROR_SENDING_OUTBOUND_MESSAGE);
        this.address = address;
    }

    public SendSmppException(final String address, final Throwable cause) {
        super(cause, SmppAdapterErrorMessageTypes.SMPP_ERROR_SENDING_OUTBOUND_MESSAGE, address);
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

}
