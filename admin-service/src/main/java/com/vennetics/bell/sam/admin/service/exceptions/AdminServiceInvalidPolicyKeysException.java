package com.vennetics.bell.sam.admin.service.exceptions;

import com.vennetics.bell.sam.core.exception.BadRequestException;

/**
 *  Admin Service an this is not a valid key for this policy exception
 */
public class AdminServiceInvalidPolicyKeysException extends BadRequestException {

    private static final long serialVersionUID = -7692548103254897857L;

    /**
     * Constructor defining invalid keys.
     *
     * @param keys the keys that are not valid.
     */
    public AdminServiceInvalidPolicyKeysException(final String keys) {
        super(AdminServiceErrorTypes.INVALID_KEYS, keys);
    }
}
