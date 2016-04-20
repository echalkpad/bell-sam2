package com.vennetics.bell.sam.core.cassandra.exceptions;

import com.vennetics.bell.sam.core.exception.PlatformFailedException;

/**
 * Runtime Exception to be thrown if ProceedInHystrixCommand#run fails.
 */
public class CassandraDataAccessException extends PlatformFailedException {

    public CassandraDataAccessException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
