package com.vennetics.bell.sam.adapters.ldap.exception;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class LdapRequestErrorTest {

    @Test
    public void shouldUseCorrectEnum() {

        assertEquals(LibLdapErrorTypes.LDAP_REQUEST_ERROR,
                     new LdapRequestException().getErrorMessageType());
    }

}
