package com.vennetics.bell.sam.adapters.messaging.smpp.support.address;

import com.cloudhopper.smpp.type.Address;

/**
 * Address formatting interface. Takes as input an E.164 address and formats it
 * according to TON and NPI values.
 *
 * @author markcorkery
 */
public interface IAddressFormatter {

    /**
     * Normalizes a source address
     *
     * @param address
     *            The E.164 number prior to normalization
     * @return The normalized number
     */
    Address formatSourceAddress(final String address);

    /**
     * Normalizes a destination address
     *
     * @param address
     *            The E.164number prior to normalization
     * @return The normalized number
     */
    Address formatDestinationAddress(final String address);

}
