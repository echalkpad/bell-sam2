package com.vennetics.bell.sam.error.exceptions;

import com.vennetics.bell.sam.core.exception.ServiceException;
import com.vennetics.bell.sam.error.ServiceErrorMessageType;

/**
 * When a general service error occurs
 */
public class UnspecificServiceError extends ServiceException {

    private static final long serialVersionUID = 6141214224019977407L;

    public UnspecificServiceError(final String errorCode) {
        super(ServiceErrorMessageType.SERVICE_ERROR, errorCode);
    }

}
