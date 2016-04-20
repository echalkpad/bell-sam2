package com.vennetics.bell.sam.admin.service.exceptions;

import com.vennetics.bell.sam.core.errors.ErrorTypeSet;

/**
 * Enum defining core Admin Service exception types.
 */
@ErrorTypeSet("/admin-service-error-messages.yml")
public enum AdminServiceErrorTypes {
    INVALID_DATA, ID_NOT_FOUND, ID_IN_USE, INVALID_KEYS, INVALID_POLICY_FOR_SERVICE, TYPES_NOT_FOUND
}
