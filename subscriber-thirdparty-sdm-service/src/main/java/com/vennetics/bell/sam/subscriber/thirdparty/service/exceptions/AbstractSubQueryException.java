package com.vennetics.bell.sam.subscriber.thirdparty.service.exceptions;

import java.util.Set;
import java.util.stream.Stream;

import com.vennetics.bell.sam.core.exception.SpecificServiceException;
import com.vennetics.bell.sam.sdm.adapter.api.SdmQueryFilter;

public abstract class AbstractSubQueryException extends SpecificServiceException {

    private static final long serialVersionUID = -5269573084813331052L;

    public AbstractSubQueryException(final Enum<?> messageCode, final Set<SdmQueryFilter> filters) {
        super(messageCode,
              filters.stream()
                     .flatMap(filter -> Stream.of(filter.getFilterAttribute(),
                                                  filter.getFilterValue()))
                     .toArray(String[]::new));
    }

}
