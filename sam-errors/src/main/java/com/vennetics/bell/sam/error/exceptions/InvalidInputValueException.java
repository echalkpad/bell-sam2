package com.vennetics.bell.sam.error.exceptions;

import com.vennetics.bell.sam.core.exception.SpecificServiceException;
import com.vennetics.bell.sam.error.ServiceErrorMessageType;

/**
 * Exception to throw when an input value is deemed to be invalid.
 */
public class InvalidInputValueException extends SpecificServiceException {

    private static final long serialVersionUID = 6141214224019977407L;

    public InvalidInputValueException(final String invalidValue) {
        super(ServiceErrorMessageType.INVALID_INPUT_VALUE, invalidValue);
    }

}
