package com.vennetics.bell.sam.core.errors;

import com.vennetics.bell.sam.core.errors.model.ErrorDescription;

/**
 * Provides a lookup mechanism for error types. Returns the error as defined in
 * the relevant external definition file.
 * 
 */
@FunctionalInterface
public interface IErrorDictionary {

    /**
     * Return the Error definition corresponding to the rest error type.
     * 
     * @param type
     *            the error type
     * @return the matching error definition.
     */
    ErrorDescription getError(Enum<?> type);
}
