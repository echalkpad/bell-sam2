package com.vennetics.bell.sam.error;

import com.vennetics.bell.sam.core.errors.ErrorTypeSet;

/**
 * Core Keys into the message properties file for localising error messages.
 * Each value represents a unique error message and should be used within a
 * common service exception.
 */
@ErrorTypeSet("/common-service-error-messages.yml")
public enum ServiceErrorMessageType {

    SERVICE_ERROR,

    POLICY_ERROR,

    NO_VALID_ADDRESS_ERROR,

    INVALID_INPUT_VALUE;

}
