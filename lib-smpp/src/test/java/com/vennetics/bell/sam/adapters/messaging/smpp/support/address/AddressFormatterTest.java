package com.vennetics.bell.sam.adapters.messaging.smpp.support.address;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.cloudhopper.smpp.SmppConstants;
import com.cloudhopper.smpp.type.Address;
import com.vennetics.bell.sam.adapters.messaging.smpp.support.autoconfigure.SmppProperties;
import com.vennetics.bell.sam.adapters.messaging.smpp.support.autoconfigure.SmppProperties.AddressAttributes;

public class AddressFormatterTest {

    private static final String SHORT_CODE_ADDRESS = "123456";
    private static final String CA_FIXED_LINE_E164 = "+19999999999";
    private static final String CA_FIXED_LINE_INTERNATIONAL = "+19999999999";
    private static final String CA_FIXED_LINE_NATIONAL = "19999999999";

    // Object under test
    private AddressFormatter objectUnderTest;

    // Concrete objects
    private SmppProperties smppProperties;

    private AddressAttributes sourceAddressAttributes;

    private AddressAttributes destinationAddressAttributes;

    @Before
    public void setUp() throws Exception {
        objectUnderTest = new AddressFormatter();
        smppProperties = new SmppProperties();
        sourceAddressAttributes = new AddressAttributes();
        sourceAddressAttributes.setTon(SmppConstants.TON_NETWORK);
        sourceAddressAttributes.setNpi(SmppConstants.NPI_UNKNOWN);
        destinationAddressAttributes = new AddressAttributes();
        destinationAddressAttributes.setTon(SmppConstants.TON_NATIONAL);
        destinationAddressAttributes.setNpi(SmppConstants.NPI_E164);
        smppProperties.setSourceAddress(sourceAddressAttributes);
        smppProperties.setDestinationAddress(destinationAddressAttributes);
        ReflectionTestUtils.setField(objectUnderTest, "smppProperties", smppProperties);
    }

    /**
     * Validate that number is unaltered if TON and NPI indicate it is a short
     * code
     */
    @Test
    public void shouldReturnUnmodifiedShortCode() {
        final Address result = objectUnderTest.formatSourceAddress(SHORT_CODE_ADDRESS);
        assertNotNull(result);
        assertEquals(result.getAddress(), SHORT_CODE_ADDRESS);
        assertEquals(result.getTon(), sourceAddressAttributes.getTon());
        assertEquals(result.getNpi(), sourceAddressAttributes.getNpi());
    }

    /**
     * Validate that number is unaltered if NPI is unknown
     */
    @Test
    public void shouldReturnUnmodifiedIfNpiUnknown() {
        destinationAddressAttributes.setNpi(SmppConstants.NPI_UNKNOWN);
        assertEquals(objectUnderTest.formatDestinationAddress(CA_FIXED_LINE_INTERNATIONAL)
                                    .getAddress(),
                     CA_FIXED_LINE_INTERNATIONAL);

        assertEquals(objectUnderTest.formatDestinationAddress(CA_FIXED_LINE_NATIONAL).getAddress(),
                     CA_FIXED_LINE_NATIONAL);
    }

    /**
     * Validate that number is returned without "+" prefix
     */
    @Test
    public void shouldNormalizeToNational() {
        assertEquals(objectUnderTest.formatDestinationAddress(CA_FIXED_LINE_E164).getAddress(),
                     CA_FIXED_LINE_NATIONAL);
    }

    /**
     * Validate that number is returned with "+" prefix
     */
    @Test
    public void shouldNormalizeToInternational() {
        destinationAddressAttributes.setTon(SmppConstants.TON_INTERNATIONAL);
        assertEquals(objectUnderTest.formatDestinationAddress(CA_FIXED_LINE_E164).getAddress(),
                     CA_FIXED_LINE_INTERNATIONAL);
    }

}
