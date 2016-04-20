package com.vennetics.bell.sam.admin.service.exceptions;

import com.vennetics.bell.sam.core.exception.BadRequestException;

/**
 *  Admin Service entity or entities not found exception.
 */
public class AdminServiceEntityNotFoundException extends BadRequestException {

    private static final long serialVersionUID = -2602933466148547603L;

    /**
     * Constructor defining entityNotFound.
     *
     * @param entityNotFound the id of the entity not found.
     */
    public AdminServiceEntityNotFoundException(final String entityNotFound) {
        super(AdminServiceErrorTypes.ID_NOT_FOUND, entityNotFound);
    }
}
