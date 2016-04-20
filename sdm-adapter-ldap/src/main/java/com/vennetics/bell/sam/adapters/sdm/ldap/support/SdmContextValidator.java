package com.vennetics.bell.sam.adapters.sdm.ldap.support;

import javax.naming.directory.DirContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ldap.pool.DirContextType;
import org.springframework.ldap.pool.validation.DirContextValidator;
import org.springframework.stereotype.Service;

/**
 * This will test the mobility ldap connection by sending a request that should
 * always succeed. If the request fails the context can be considered invalid.
 */
@Service
public class SdmContextValidator implements DirContextValidator {

    private static final Logger logger = LoggerFactory.getLogger(SdmContextValidator.class);

    @Override
    public boolean validateDirContext(final DirContextType dirContextType,
                                      final DirContext dirContext) {

        // Bell need to provide a query to heartbeat the SDM availability
        // and keep connections alive.
        // See confluence wiki
        logger.warn("No implementation for context validation. Defaulting to true");
        return true;
    }

    @Override
    public String toString() {
        return "SdmContextValidator []";
    }

}
