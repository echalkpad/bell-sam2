package com.vennetics.bell.sam.sms.common.address;

/**
 * Address normalizing interface
 *
 */
@FunctionalInterface
public interface IAddressNormalizer {

    /**
     * Normalizes an address to E.164 format
     *
     * @param address
     *            The number prior to normalization
     * @return The normalized number
     */
    Address normalizeToE164Address(final Address address);

}
