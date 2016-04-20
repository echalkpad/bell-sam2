package com.vennetics.bell.sam.adapters.messaging.smpp.support.address;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cloudhopper.smpp.SmppConstants;
import com.cloudhopper.smpp.type.Address;
import com.vennetics.bell.sam.adapters.messaging.smpp.support.autoconfigure.SmppProperties;
import com.vennetics.bell.sam.adapters.messaging.smpp.support.autoconfigure.SmppProperties.AddressAttributes;

/**
 * An implementation of the AddressFormatter that utilizes Google's
 * libphonenumber to do the heavy lifting
 *
 * @author markcorkery
 */
@Component
public class AddressFormatter implements IAddressFormatter {

    @Autowired
    private SmppProperties smppProperties;

    public static final String INTERNATIONAL_PREFIX = "+";

    @Override
    public Address formatSourceAddress(final String address) {
        return populateAddressObject(address, smppProperties.getSourceAddress());
    }

    @Override
    public Address formatDestinationAddress(final String address) {
        return populateAddressObject(address, smppProperties.getDestinationAddress());
    }

    /**
     * Create and populate an instance of {@link Address}
     *
     * @param address
     *            The unformatted number supplied to the service
     * @param addressAttributes
     *            The configuration for the address.
     * @return A non-null {@link Address}
     */
    private static Address populateAddressObject(final String address,
                                                 final AddressAttributes addressAttributes) {
        final Address normalizedAddress = new Address();
        normalizedAddress.setTon(addressAttributes.getTon());
        normalizedAddress.setNpi(addressAttributes.getNpi());
        normalizedAddress.setAddress(normalize(address,
                                               addressAttributes.getTon(),
                                               addressAttributes.getNpi()));
        return normalizedAddress;
    }

    /**
     * Use TON and NPI values in order to process/normalize the address.
     *
     * @param address
     *            The number prior to normalization
     * @param ton
     *            The Type of Number
     * @param npi
     *            The Number Plan Indicator
     * @return A normalized address
     */
    private static String normalize(final String address, final byte ton, final byte npi) {
        String result = address;
        if (indicatesShortCode(ton, npi)) {
            result = processShortCode(address);
        } else if (indicatesInternational(ton, npi)) {
            result = processInternationalNumber(address);
        } else if (indicatesNationalNumber(ton, npi)) {
            result = processNationalNumber(address);
        }
        return result;
    }

    /**
     * Builds a short code address (TODO: MCOY - Add validate for min/max
     * length)
     *
     * @param address
     * @return
     */
    private static String processShortCode(final String address) {
        return address;
    }

    /**
     * Builds up a E164 address with no '+' prefix, representing a national
     * phone number
     *
     * @param address
     * @return
     */
    private static String processNationalNumber(final String address) {
        return stripInternationalPrefix(address);
    }

    /**
     * Determine whether or not the <code>ton</code> and <code>npi</code>
     * pairing indicate that the number should be a national number.
     *
     * @param ton
     *            Type of Number
     * @param npi
     *            Number Plan Identifier
     * @return <code>true</code> if the number is a national number,
     *         <code>false</code> otherwise
     */
    private static boolean indicatesNationalNumber(final byte ton, final byte npi) {
        return SmppConstants.TON_NATIONAL == ton && SmppConstants.NPI_E164 == npi;
    }

    /**
     * Builds up a E164 address with a '+' prefix, representing an international
     * phone number
     *
     * @param address
     * @return
     */
    private static String processInternationalNumber(final String address) {
        return address;
    }

    /**
     * Determine whether or not the <code>ton</code> and <code>npi</code>
     * pairing indicate that the number should be an international number.
     *
     * @param ton
     *            Type of Number
     * @param npi
     *            Number Plan Identifier
     * @return <code>true</code> if the number is an international number,
     *         <code>false</code> otherwise
     */
    private static boolean indicatesInternational(final byte ton, final byte npi) {
        return SmppConstants.TON_INTERNATIONAL == ton && SmppConstants.NPI_E164 == npi;
    }

    /**
     * Determine whether or not the <code>ton</code> and <code>npi</code>
     * pairing indicate that the number should be a short code number.
     *
     * @param ton
     *            Type of Number
     * @param npi
     *            Number Plan Identifier
     * @return <code>true</code> if the number is a short code number,
     *         <code>false</code> otherwise
     */
    private static boolean indicatesShortCode(final byte ton, final byte npi) {
        return SmppConstants.TON_NETWORK == ton && SmppConstants.NPI_UNKNOWN == npi;
    }

    /**
     * Strips the "+" character if present in the <code>suppliedAddress</code>
     *
     * @param suppliedAddress
     *            Address to be modified.
     * @return A non-null String with all occurrences of "+" removed.
     */
    private static String stripInternationalPrefix(final String suppliedAddress) {
        return suppliedAddress.replace(INTERNATIONAL_PREFIX, StringUtils.EMPTY);
    }
}
