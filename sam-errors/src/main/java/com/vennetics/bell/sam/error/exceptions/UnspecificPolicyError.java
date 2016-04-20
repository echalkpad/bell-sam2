package com.vennetics.bell.sam.error.exceptions;

import com.vennetics.bell.sam.core.exception.PolicyException;
import com.vennetics.bell.sam.error.ServiceErrorMessageType;

/**
 * When a general policy error occurs
 */
public class UnspecificPolicyError extends PolicyException {

    private static final long serialVersionUID = 6141214224019977407L;

    public UnspecificPolicyError(final String errorCode) {
        super(ServiceErrorMessageType.POLICY_ERROR, errorCode);
    }

}
