package com.vennetics.bell.sam.adapters.sdm.ldap;

import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.vennetics.bell.sam.sdm.adapter.api.SdmAccount;

import nl.jqno.equalsverifier.EqualsVerifier;

/**
 * The test class for the SdmAccount POJO
 *
 * @author aaronwatters
 */
public class SdmAccountTest {

    private Map<String, Set<String>> dummyAttributes;

    private SdmAccount sdmAccount;

    @Before
    public void setUp() {
        // Create test data
        final Set<String> eamLanguageAttributes = new HashSet<>();
        eamLanguageAttributes.add("E");
        eamLanguageAttributes.add("F");
        eamLanguageAttributes.add("G");

        dummyAttributes = new HashMap<>();
        dummyAttributes.put("eamLanguage", eamLanguageAttributes);

        sdmAccount = new SdmAccount(dummyAttributes);
    }

    @Test
    public void testSdmAccountPojo() throws Exception {
        Assert.assertNotNull("The SdmAccount returned unexpected values", sdmAccount);
        Assert.assertEquals("The SdmAccount returned unexpected values",
                            dummyAttributes, sdmAccount.getAttributes());
    }

    @Test
    public void shouldImplementEqualsAndHashCode() {
        EqualsVerifier.forClass(SdmAccount.class).verify();
    }

    @Test
    public void shouldShowAttributesInToString() {
        assertTrue(sdmAccount.toString().contains("eamLanguage"));
        assertTrue(sdmAccount.toString().contains("E"));
    }
}
