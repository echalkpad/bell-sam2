package com.vennetics.bell.sam.subscriber.thirdparty.service.exceptions;

import com.vennetics.bell.sam.core.exception.SpecificPolicyException;

/**
 * When a received filter type does not match the allowed format
 */
public class InvalidFilterTypeException extends SpecificPolicyException {

    private static final long serialVersionUID = 6141214224019977407L;

    public InvalidFilterTypeException(final Throwable cause, final String invalidFilterType) {
        super(cause, SubscriberThirdPartySdmErrorTypes.POL_INVALID_FILTER_TYPE, invalidFilterType);
    }

    public InvalidFilterTypeException(final String invalidFilterType) {
        super(SubscriberThirdPartySdmErrorTypes.POL_INVALID_FILTER_TYPE, invalidFilterType);
    }

}
