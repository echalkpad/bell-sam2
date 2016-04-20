package com.vennetics.bell.sam.sms.common.address;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberFormat;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;
import com.vennetics.bell.sam.error.exceptions.InvalidAddressException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * An implementation of the AddressNormalizer that utilizes Google's
 * libphonenumber to do the heavy lifting
 */
@Component
public class AddressNormalizer implements IAddressNormalizer {

    private static final Logger LOG = LoggerFactory.getLogger(AddressNormalizer.class);

    @Autowired
    private PhoneNumberUtil phoneNumberUtil;

    @Autowired
    private AddressProperties addressProperties;

    @Override
    public Address normalizeToE164Address(final Address address) {
        return buildE164Address(address);
    }

    /**
     * Creates a new address in E.164 format based on the supplied address
     *
     * @param address Address to be formatted
     * @return Address formatted in E.164 format
     */
    private Address buildE164Address(final Address address) {
        final PhoneNumber phoneNumber = createDefaultRegionPhoneNumber(address);
        address.setE164Address(phoneNumberUtil.format(phoneNumber, PhoneNumberFormat.E164));
        address.setRejected(false);
        return address;
    }

    /**
     * Attempts to create a {@link PhoneNumber} based on the supplied address
     * and the default region injected via {@link AddressProperties}
     *
     * @param address The address to be used to create the {@link PhoneNumber}
     * @return A {@link PhoneNumber} object that can be used to normalize as
     * needed.
     */
    private PhoneNumber createDefaultRegionPhoneNumber(final Address address) {
        final String suppliedAddress = address.getSuppliedAddress();
        try {
            return phoneNumberUtil.parse(suppliedAddress, addressProperties.getDefaultRegion());
        } catch (final NumberParseException npe) {
            LOG.error(String.format("Error parsing number: %s", suppliedAddress), npe);
            address.setRejected(true);
            throw new InvalidAddressException(suppliedAddress);
        }
    }

}
