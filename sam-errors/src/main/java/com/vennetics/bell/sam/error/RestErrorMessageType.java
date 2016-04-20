package com.vennetics.bell.sam.error;

import com.vennetics.bell.sam.core.errors.ErrorTypeSet;

/**
 * Core Keys into the message properties file for localising error messages.
 * Each value represents a unique error message and should be used within a
 * single exception.
 */
@ErrorTypeSet("/sam-errors-messages.yml")
public enum RestErrorMessageType {

    HTTP_401_UNAUTHORIZED,
    RESOURCE_INTERNAL_ERROR,
    RESOURCE_UNAVAILABLE_COMMAND_FAILED,
    RESOURCE_BAD_REQUEST_INVALID_ARGUMENT,
    NOT_IMPLEMENTED;
}
