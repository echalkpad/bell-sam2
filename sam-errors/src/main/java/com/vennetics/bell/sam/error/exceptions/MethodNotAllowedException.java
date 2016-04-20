package com.vennetics.bell.sam.error.exceptions;

import com.vennetics.bell.sam.core.exception.BadRequestException;
import com.vennetics.bell.sam.error.RestErrorMessageType;

/**
 *  SAM Exception Method not allowed code not implemented exception
 */
public class MethodNotAllowedException extends BadRequestException {

    private static final long serialVersionUID = 7915498579296681934L;

    /**
     * Constructor defining codeNotImplemented.
     *
     * @param codeNotImplemented the method that has not been implemented.
     */
    public MethodNotAllowedException(final String codeNotImplemented) {
        super(RestErrorMessageType.NOT_IMPLEMENTED, codeNotImplemented);
    }

}
