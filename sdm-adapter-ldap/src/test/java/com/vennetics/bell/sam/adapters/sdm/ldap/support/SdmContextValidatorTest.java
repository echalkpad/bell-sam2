package com.vennetics.bell.sam.adapters.sdm.ldap.support;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class SdmContextValidatorTest {

    @Test
    public void shouldDoNothingAndReturnTrue() {
        assertTrue(new SdmContextValidator().validateDirContext(null, null));
    }

}
