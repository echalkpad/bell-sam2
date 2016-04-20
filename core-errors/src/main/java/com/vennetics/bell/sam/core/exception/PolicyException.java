package com.vennetics.bell.sam.core.exception;

/**
 * Base class for any exception that will result in the API returning a PolicyException to
 * the client.
 */

public abstract class PolicyException extends BadRequestException {

    private static final long serialVersionUID = 5065966114610563829L;
    protected static final String NON_SPECIFIC_POLICY_MESSAGE_ID = "POL0001";

    public PolicyException(final Throwable cause,
                           final Enum<?> messageCode,
                           final String... messageParams) {
        super(cause, messageCode, messageParams);

    }

    public PolicyException(final Enum<?> messageCode, final String... messageParams) {
        super(messageCode, messageParams);

    }

    public PolicyException(final Enum<?> messageCode) {
        super(messageCode);
    }

    public PolicyException(final Throwable e, final Enum<?> messageCode) {
        super(e, messageCode);
    }

    @Override
    public final String getNonSpecificMessageId() {
        return NON_SPECIFIC_POLICY_MESSAGE_ID;
    }
}
