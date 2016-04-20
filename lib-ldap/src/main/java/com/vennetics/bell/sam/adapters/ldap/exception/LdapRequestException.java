package com.vennetics.bell.sam.adapters.ldap.exception;

import com.vennetics.bell.sam.core.exception.SpecificServiceException;

/**
 * For non-networking related LDAP request errors.
 */
public class LdapRequestException extends SpecificServiceException {

    private static final long serialVersionUID = 6141214224019977407L;

    /**
     * With cause constructor
     *
     * @param cause
     *            the cause.
     */
    public LdapRequestException(final Throwable cause) {
        super(cause, LibLdapErrorTypes.LDAP_REQUEST_ERROR);
    }

    /**
     * No cause constructor.
     */
    public LdapRequestException() {
        super(LibLdapErrorTypes.LDAP_REQUEST_ERROR);
    }

}
