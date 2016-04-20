package com.vennetics.bell.sam.core.exception;

/**
 * Exception thrown when a server error condition occurs. Generally these will
 * map to 5xx errors on the API and will trigger fallbacks and circuit breaker
 * to be triggered.
 * <P>
 * Typically this also implies the client request was accepted but it can happen
 * prior to that e.g. if an internal problem caused the parsing of the request
 * to fail.
 */

public abstract class ServiceAffectingException extends ApiException {

    private static final long serialVersionUID = -902918828317928527L;

    public ServiceAffectingException(final Enum<?> messageCode, final String... messageParams) {
        super(messageCode, messageParams);

    }

    public ServiceAffectingException(final Enum<?> messageCode) {
        super(messageCode);

    }

    public ServiceAffectingException(final Throwable e,
                                     final Enum<?> messageCode,
                                     final String... messageParams) {
        super(e, messageCode, messageParams);

    }

    public ServiceAffectingException(final Throwable e, final Enum<?> messageCode) {
        super(e, messageCode);

    }
}
