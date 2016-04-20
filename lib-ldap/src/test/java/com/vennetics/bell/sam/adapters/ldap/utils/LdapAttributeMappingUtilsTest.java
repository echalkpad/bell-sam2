package com.vennetics.bell.sam.adapters.ldap.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttributes;

import org.junit.Test;

import com.vennetics.bell.sam.adapters.ldap.exception.LdapRequestException;

public class LdapAttributeMappingUtilsTest {

    @Test
    public void shouldReturnSingleValueGet() throws NamingException {

        final Attributes attrs = new BasicAttributes("id", "value");

        final String result = LdapAttributeMappingUtils.get("id", attrs);

        assertEquals("value", result);

    }

    @Test
    public void shouldReturnNullWhenValueIsNull() throws NamingException {

        final Attributes attrs = new BasicAttributes("id", null);

        final String result = LdapAttributeMappingUtils.get("id", attrs);

        assertNull(result);

    }

    @Test
    public void shouldReturnNullWhenNoMatchFoundOnGet() throws NamingException {

        final Attributes attrs = new BasicAttributes();

        final String result = LdapAttributeMappingUtils.get("id", attrs);

        assertNull(result);

    }

    @Test
    public void shouldReturnMatchForGetAll() throws NamingException {

        final Attributes attrs = new BasicAttributes("id", "value");

        final Set<String> result = LdapAttributeMappingUtils.getAll("id", attrs);

        assertTrue(result.contains("value"));

    }

    @Test
    public void shouldReturnEmptySetWhenNoMatchFoundOnGetAll() throws NamingException {

        final Attributes attrs = new BasicAttributes();

        final Set<String> result = LdapAttributeMappingUtils.getAll("id", attrs);

        assertTrue(result.isEmpty());

    }

    @Test
    public void shouldNotThrowErrorOnMandatoryCheckWhenValueIsNull() throws NamingException {

        final Attributes attrs = new BasicAttributes("id", null);

        final String result = LdapAttributeMappingUtils.getOrRejectIfNull("id", attrs);

        assertNull(result);

    }

    @Test(expected = LdapRequestException.class)
    public void shouldThrowErrorOnMandatoryCheckWhenNoMatchFoundOnGet() throws NamingException {

        final Attributes attrs = new BasicAttributes();

        LdapAttributeMappingUtils.getOrRejectIfNull("id", attrs);

    }

}
