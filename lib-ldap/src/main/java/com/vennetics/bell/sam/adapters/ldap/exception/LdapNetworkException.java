package com.vennetics.bell.sam.adapters.ldap.exception;

import com.vennetics.bell.sam.core.exception.SpecificServiceException;

/**
 * For LDAP networking errors
 */
public class LdapNetworkException extends SpecificServiceException {

    private static final long serialVersionUID = 6141214224019977407L;

    public LdapNetworkException(final Throwable cause) {
        super(cause, LibLdapErrorTypes.LDAP_COMMS_ERROR);
    }

}
