package com.vennetics.bell.sam.error.adapters;

import com.vennetics.bell.sam.error.mapper.MappedError;

/**
 * Adapts {@link MappedError}s to standard exception entities.
 */
@FunctionalInterface
public interface IErrorAdapter {

    /**
     * Adapts a {@link MappedError}
     * @param error
     *      the error to be adapted.
     * @return
     *      the entity containing the adapted error.
     */
    Object adapt(MappedError error);
}
