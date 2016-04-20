package com.vennetics.bell.sam.adapters.ldap.exception;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class LdapRequestNetworkErrorTest {

    @Test
    public void shouldUseCorrectEnum() {

        assertEquals(LibLdapErrorTypes.LDAP_COMMS_ERROR,
                     new LdapNetworkException(null).getErrorMessageType());
    }

}
