package com.vennetics.bell.sam.error.exceptions;

import com.vennetics.bell.sam.core.exception.SpecificServiceException;
import com.vennetics.bell.sam.error.ServiceErrorMessageType;

/**
 * Exception to throw when a supplied address is deemed to be invalid.
 */
public class InvalidAddressException extends SpecificServiceException {

    private static final long serialVersionUID = -1592626405800091209L;

    public InvalidAddressException(final String address) {
        super(ServiceErrorMessageType.NO_VALID_ADDRESS_ERROR, address);
    }

}
