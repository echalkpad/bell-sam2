package com.vennetics.microservices.common.core.soap.gateway.adapter.errors.exceptions;

import com.vennetics.bell.sam.core.exception.ServiceAffectingException;
import com.vennetics.microservices.common.core.soap.gateway.adapter.errors.ParlayxAdaptersErrorMessageType;

/**
 * Thrown when a gateway adapter fails to identify the service instance from the URL.
 */

public class InvalidServiceIndentifierException extends ServiceAffectingException {

    private static final long serialVersionUID = -902918828314328527L;

    public InvalidServiceIndentifierException(final String url) {
        super(ParlayxAdaptersErrorMessageType.INVALID_SERVICE_IDENTIFIER, url);
    }
}
