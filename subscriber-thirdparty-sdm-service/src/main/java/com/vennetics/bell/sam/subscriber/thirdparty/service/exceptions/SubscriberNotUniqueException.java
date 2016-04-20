package com.vennetics.bell.sam.subscriber.thirdparty.service.exceptions;

import java.util.Set;

import com.vennetics.bell.sam.sdm.adapter.api.SdmQueryFilter;

/**
 * Thrown if a subscriber cannot be uniquely identified in a request.
 */
public class SubscriberNotUniqueException extends AbstractSubQueryException {

    private static final long serialVersionUID = 6141214224019977407L;

    public SubscriberNotUniqueException(final Set<SdmQueryFilter> filters) {
        super(SubscriberThirdPartySdmErrorTypes.SVC_SUBSCRIBER_NOT_UNIQUE, filters);
    }

}
