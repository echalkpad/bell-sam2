package com.vennetics.bell.sam.error.adapters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.vennetics.bell.sam.error.mapper.MappedError;

/**
 * Subscriber-profile specific IErrorAdapter implementation. Performs no
 * translation of MappedError types.
 */

@Service(BellSubscriberErrorAdapter.SERVICE_NAME)
public class BellSubscriberErrorAdapter implements IErrorAdapter {

    private static final Logger logger = LoggerFactory.getLogger(BellSubscriberErrorAdapter.class);

    public static final String SERVICE_NAME = "bellSubscriberThirdPartyErrorAdapter";

    @Override
    public Object adapt(final MappedError error) {
        logger.debug(">>> adapt {}", error);

        return error;
    }
}
