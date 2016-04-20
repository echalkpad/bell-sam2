package com.vennetics.bell.sam.error.adapters;

import com.vennetics.bell.sam.error.mapper.MappedError;
import generated.oma.xml.rest.netapi.common._1.PolicyException;
import generated.oma.xml.rest.netapi.common._1.RequestError;
import generated.oma.xml.rest.netapi.common._1.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Implements {@link IErrorAdapter}, adapting a {@link MappedError} to an netapi {@link PolicyException} or
 * {@link ServiceException}.
 */

@Service
public class NetapiErrorAdapter implements IErrorAdapter {

    private static final Logger LOG = LoggerFactory.getLogger(NetapiErrorAdapter.class);

    @Override
    public Object adapt(final MappedError error) {
        LOG.debug(">>> adapt {}", error);

        if (error.isPolicyError()) {
            return new RequestError().withPolicyException(mapToPolicyException(error));
        }
        return new RequestError().withServiceException(mapToServiceException(error));
    }

    private static ServiceException mapToServiceException(final MappedError error) {
        return new ServiceException()
                .withMessageId(error.getCode())
                .withText(error.getMessageTemplate())
                .withVariables(error.getMessageVariables());
    }

    private static PolicyException mapToPolicyException(final MappedError error) {
        return new PolicyException()
                .withMessageId(error.getCode())
                .withText(error.getMessageTemplate())
                .withVariables(error.getMessageVariables());
    }
}
