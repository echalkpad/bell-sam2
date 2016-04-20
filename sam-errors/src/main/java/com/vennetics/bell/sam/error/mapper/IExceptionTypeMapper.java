package com.vennetics.bell.sam.error.mapper;

import javax.servlet.http.HttpServletRequest;

/**
 * Maps exceptions that can be thrown by the service layers into
 * a normalised representation that can be adapted to exceptions for use in HTTP response payloads.
 */

public interface IExceptionTypeMapper {

    /**
     * Map a {@link Throwable}.
     * @param error
     *      the error to be mapped
     * @return
     *      a {@link MappedError} containing the useful attributes from <tt>error</tt>, supplemented with details
     *      from any relevant metadata.
     */
    MappedError mapException(Throwable error);

    /**
     * Map a {@link Throwable}.
     * @param error
     *      the error to be mapped
     * @param request
     *      the HTTP request being executed when the exception was thrown.
     * @return
     *      a {@link MappedError} containing the useful attributes from <tt>error</tt>, supplemented with details
     *      from any relevant metadata and the <tt>request</tt>.
     */
    MappedError mapException(Throwable error, HttpServletRequest request);
}
