package com.vennetics.bell.sam.subscriber.thirdparty.service.exceptions;

import java.util.Set;

import com.vennetics.bell.sam.sdm.adapter.api.SdmQueryFilter;

/**
 * Thrown if a subscriber cannot be found.
 */
public class SubscriberNotFoundException extends AbstractSubQueryException {

    private static final long serialVersionUID = 6141214224019977407L;

    public SubscriberNotFoundException(final Set<SdmQueryFilter> filters) {
        super(SubscriberThirdPartySdmErrorTypes.SVC_SUBSCRIBER_NOT_FOUND, filters);
    }
}
