package com.vennetics.bell.sam.core.exception;

/**
 * Base class for any exception that will result in the API returning a PolicyException with a specific
 * message id to the client.
 */

public abstract class SpecificPolicyException extends PolicyException {

    private static final long serialVersionUID = 5065966114670563829L;

    public SpecificPolicyException(final Throwable cause,
                                   final Enum<?> messageCode,
                                   final String... messageParams) {
        super(cause, messageCode, messageParams);
    }

    public SpecificPolicyException(final Enum<?> messageCode, final String... messageParams) {
        super(messageCode, messageParams);
    }

    public SpecificPolicyException(final Enum<?> messageCode) {
        super(messageCode);
    }

    @Override
    public final boolean isSpecificException() {
        return true;
    }
}
