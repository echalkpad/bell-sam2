package com.vennetics.bell.sam.registry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * Temporary implementation of {@link IServiceRegistry}, currently delivering hard-coded values.
 */

@Service
public class ServiceRegistry implements IServiceRegistry {

    private static final Logger LOG = LoggerFactory.getLogger(ServiceRegistry.class);

    @Override
    public List<String> servicesForProtocol(final String protocolIdentifier) {
        LOG.debug(">>> servicesForProtocol {}", protocolIdentifier);
        if ("subscriber-thirdparty-sdm-server".equalsIgnoreCase(protocolIdentifier)) {
            return Arrays.asList("subscriber-thirdparty-sdm-server");
        }

        return Arrays.asList("unknown-protocol-" + protocolIdentifier);
    }
}
