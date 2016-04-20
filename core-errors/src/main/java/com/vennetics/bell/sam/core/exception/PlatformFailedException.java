package com.vennetics.bell.sam.core.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Thrown when an unrecoverable exception due to a platform issue.
 */

public class PlatformFailedException extends RuntimeException {

    private static final Logger LOG = LoggerFactory.getLogger(PlatformFailedException.class);

    private static final long serialVersionUID = 5065966114610563829L;

    public PlatformFailedException(final String message, final Throwable cause) {
        super(message, cause);
        LOG.error(message, cause);
    }

    public PlatformFailedException(final String message) {
        super(message);
        LOG.error(message);
    }
}
