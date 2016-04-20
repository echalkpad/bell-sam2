package com.vennetics.bell.sam.core.exception;

/**
 * Exception thrown when a client error condition occurs. Generally these will
 * map to 4xx errors on the API and will not trigger fallbacks or circuit
 * breaker to be triggered.
 */

public abstract class BadRequestException extends ApiException {

    private static final long serialVersionUID = 5065966114610563829L;

    public BadRequestException(final Enum<?> messageCode, final String... messageParams) {
        super(messageCode, messageParams);
    }

    public BadRequestException(final Enum<?> messageCode) {
        super(messageCode);
    }

    public BadRequestException(final Throwable e,
                               final Enum<?> messageCode,
                               final String... messageParams) {
        super(e, messageCode, messageParams);
    }

    public BadRequestException(final Throwable e, final Enum<?> messageCode) {
        super(e, messageCode);
    }

    /**
     * Indicates whether this exception is reported to the client with a specific message id,
     * or whether it is reported using a general message id (SVC0001/POL0001), in which case the
     * message id defined in the {@link com.vennetics.bell.sam.core.errors.model.ErrorDescription} is
     * returned as the first variable message argument.
     * Override in the base classes for specific service/policy exceptions.
     */
    public boolean isSpecificException() {
        return false;
    }

    /**
     * Override this method to define the message id to be used when reporting non-specific
     * exceptions to the client (for example, POL0001 or SVC0001).
     */
    public String getNonSpecificMessageId() {
        return null;
    }
}
