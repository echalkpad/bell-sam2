package com.vennetics.bell.sam.sms.common.address;

import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.vennetics.bell.sam.error.exceptions.InvalidAddressException;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class AddressNormalizerTest {

    private static final String CA_FIXED_LINE_NO_SPACES = "+19999999999";
    private static final String CA_FIXED_LINE_NO_SPACES_TEL_PREFIX = "tel:9999999999";
    private static final String CA_FIXED_LINE_INTERNATIONAL = "+19999999999";

    private static final String IE_FIXED_LINE_SPACES = "+353 42 941 9669";
    private static final String IE_FIXED_LINE_NO_SPACES = "+353429419669";
    private static final String IE_FIXED_LINE_NO_SPACES_NO_PLUS = "353429419669";
    private static final String IE_FIXED_LINE_NO_SPACES_TEL_PREFIX = "tel:0429419669";
    private static final String IE_FIXED_LINE_INTERNATIONAL = "+353429419669";

    private static final String UK_FIXED_LINE_SPACES = "+ 44 28 9082 1030";
    private static final String UK_FIXED_LINE_NO_SPACES = "+442890821030";
    private static final String UK_FIXED_LINE_NO_SPACES_NO_PLUS = "442890821030";
    private static final String UK_FIXED_LINE_NO_SPACES_TEL_PREFIX = "tel:02890821030";
    private static final String UK_FIXED_LINE_INTERNATIONAL = "+442890821030";

    private static final String UK_MOBILE_LINE_NO_SPACES_TEL_PREFIX = "tel:447841104177";
    private static final String UK_MOBILE_LINE_SPACES = "+ 44 78 4110 4177";
    private static final String UK_MOBILE_LINE_INTERNATIONAL = "+447841104177";

    // Object under test
    private AddressNormalizer objectUnderTest;

    // Concrete objects
    private AddressProperties addressProperties;

    private PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();

    private Address suppliedAddress = new Address();

    @Before
    public void setUp() throws Exception {
        objectUnderTest = new AddressNormalizer();
        addressProperties = new AddressProperties();
        addressProperties.setDefaultRegion("CA");
        ReflectionTestUtils.setField(objectUnderTest, "addressProperties", addressProperties);
        ReflectionTestUtils.setField(objectUnderTest, "phoneNumberUtil", phoneNumberUtil);
    }

    /**
     * Validate that number is returned without "+" prefix
     */
    @Test
    public void shouldNormalizeToNational() {
        suppliedAddress.setSuppliedAddress(CA_FIXED_LINE_NO_SPACES);
        assertEquals(objectUnderTest.normalizeToE164Address(suppliedAddress).getE164Address(),
                     CA_FIXED_LINE_INTERNATIONAL);
        suppliedAddress.setSuppliedAddress(CA_FIXED_LINE_NO_SPACES_TEL_PREFIX);
        assertEquals(objectUnderTest.normalizeToE164Address(suppliedAddress).getE164Address(),
                     CA_FIXED_LINE_INTERNATIONAL);
        addressProperties.setDefaultRegion("IE");
        suppliedAddress.setSuppliedAddress(IE_FIXED_LINE_NO_SPACES);
        assertEquals(objectUnderTest.normalizeToE164Address(suppliedAddress).getE164Address(),
                     IE_FIXED_LINE_INTERNATIONAL);
        suppliedAddress.setSuppliedAddress(IE_FIXED_LINE_SPACES);
        assertEquals(objectUnderTest.normalizeToE164Address(suppliedAddress).getE164Address(),
                     IE_FIXED_LINE_INTERNATIONAL);
        suppliedAddress.setSuppliedAddress(IE_FIXED_LINE_NO_SPACES_NO_PLUS);
        assertEquals(objectUnderTest.normalizeToE164Address(suppliedAddress).getE164Address(),
                     IE_FIXED_LINE_INTERNATIONAL);
        suppliedAddress.setSuppliedAddress(IE_FIXED_LINE_NO_SPACES_TEL_PREFIX);
        assertEquals(objectUnderTest.normalizeToE164Address(suppliedAddress).getE164Address(),
                     IE_FIXED_LINE_INTERNATIONAL);
        addressProperties.setDefaultRegion("GB");
        suppliedAddress.setSuppliedAddress(UK_FIXED_LINE_NO_SPACES);
        assertEquals(objectUnderTest.normalizeToE164Address(suppliedAddress).getE164Address(),
                     UK_FIXED_LINE_INTERNATIONAL);
        suppliedAddress.setSuppliedAddress(UK_FIXED_LINE_SPACES);
        assertEquals(objectUnderTest.normalizeToE164Address(suppliedAddress).getE164Address(),
                     UK_FIXED_LINE_INTERNATIONAL);
        suppliedAddress.setSuppliedAddress(UK_FIXED_LINE_NO_SPACES_NO_PLUS);
        assertEquals(objectUnderTest.normalizeToE164Address(suppliedAddress).getE164Address(),
                     UK_FIXED_LINE_INTERNATIONAL);
        suppliedAddress.setSuppliedAddress(UK_FIXED_LINE_NO_SPACES_TEL_PREFIX);
        assertEquals(objectUnderTest.normalizeToE164Address(suppliedAddress).getE164Address(),
                     UK_FIXED_LINE_INTERNATIONAL);
        suppliedAddress.setSuppliedAddress(UK_MOBILE_LINE_NO_SPACES_TEL_PREFIX);
        assertEquals(objectUnderTest.normalizeToE164Address(suppliedAddress).getE164Address(),
                     UK_MOBILE_LINE_INTERNATIONAL);
        suppliedAddress.setSuppliedAddress(UK_MOBILE_LINE_SPACES);
        assertEquals(objectUnderTest.normalizeToE164Address(suppliedAddress).getE164Address(),
                     UK_MOBILE_LINE_INTERNATIONAL);
    }

    @Test
    public void shouldThrowExceptionIfInvalidNumberSupplied() {
        final Address invalidAddress = new Address();
        invalidAddress.setSuppliedAddress("a:1223232:bc");
        try {
            objectUnderTest.normalizeToE164Address(invalidAddress);
            fail("Address should have been flagged as an error");
        } catch (final InvalidAddressException iae) {
            assertNotNull(iae);
            assertTrue(invalidAddress.isRejected());
        }
    }
}
