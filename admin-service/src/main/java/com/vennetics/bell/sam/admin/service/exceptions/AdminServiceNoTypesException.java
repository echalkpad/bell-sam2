package com.vennetics.bell.sam.admin.service.exceptions;

import com.vennetics.bell.sam.core.exception.BadRequestException;

/**
 *  Admin Service no types to return exception
 */
public class AdminServiceNoTypesException extends BadRequestException {

    private static final long serialVersionUID = 3050714213276716241L;

    /**
     * Constructor defining the type that does not exist.
     *
     * @param types the kind of types that do not exist.
     */
    public AdminServiceNoTypesException(final String types) {
        super(AdminServiceErrorTypes.TYPES_NOT_FOUND, types);
    }
}
