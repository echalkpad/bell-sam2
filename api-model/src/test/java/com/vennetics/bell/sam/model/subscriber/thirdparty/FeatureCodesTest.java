package com.vennetics.bell.sam.model.subscriber.thirdparty;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

/**
 * The test class for the FeatureCodes POJO
 * 
 */
public class FeatureCodesTest {

    private List<Boolean> dummyCodes;
    private List<Boolean> dummyCodes2;

    @Before
    public void setUp() {
        // Create test data
        dummyCodes = new ArrayList<>();
        dummyCodes.add(Boolean.FALSE);
        dummyCodes.add(Boolean.TRUE);

        dummyCodes2 = new ArrayList<>();
        dummyCodes2.add(Boolean.FALSE);
    }

    @Test
    public void testFeatureCodesPojo() throws JsonProcessingException {

        // No arg constructor
        final FeatureCodes noArgFeatureCodes = new FeatureCodes();
        assertNotNull(noArgFeatureCodes.getCodesPresent());
        assertEquals(noArgFeatureCodes.getCodesPresent().size(), 0);

        // All arg constructor
        final FeatureCodes featureCodes = new FeatureCodes(dummyCodes);
        assertNotNull("The FeatureCodes returned unexpected values",
                      featureCodes.getCodesPresent());
        assertEquals("The FeatureCodes returned unexpected values",
                     dummyCodes,
                     featureCodes.getCodesPresent());
    }

    @Test
    public void testToString() {
        final FeatureCodes featureCodes = new FeatureCodes(dummyCodes);
        assertEquals(featureCodes.toString(), "FeatureCodes{codesPresent=[false, true]}");
    }

    @Test
    public void testsEquals() {
        EqualsVerifier.forClass(FeatureCodes.class).suppress(Warning.NONFINAL_FIELDS).verify();
    }

    @Test
    public void testHashcode() {
        final FeatureCodes featureCodes1 = new FeatureCodes(dummyCodes);
        final FeatureCodes featureCodes2 = new FeatureCodes(dummyCodes);
        final FeatureCodes featureCodes3 = new FeatureCodes(null);
        final FeatureCodes featureCodes4 = new FeatureCodes(dummyCodes2);

        // Different Class
        assertFalse(featureCodes1.hashCode() == new Attribute("", "").hashCode());

        // Different attribute contents
        assertFalse(featureCodes1.hashCode() == featureCodes3.hashCode());
        assertFalse(featureCodes1.hashCode() == featureCodes4.hashCode());

        // Successful Comparison
        assertTrue(featureCodes1.hashCode() == featureCodes1.hashCode());
        assertTrue(featureCodes1.hashCode() == featureCodes2.hashCode());
    }

}
