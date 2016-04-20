package com.vennetics.bell.sam.adapters.ldap.exception;

import com.vennetics.bell.sam.core.errors.ErrorTypeSet;

/**
 * Enum defining core ldap exception types.
 */
@ErrorTypeSet("/lib-ldap-error-messages.yml")
public enum LibLdapErrorTypes {

    LDAP_REQUEST_ERROR,

    LDAP_COMMS_ERROR;
}
