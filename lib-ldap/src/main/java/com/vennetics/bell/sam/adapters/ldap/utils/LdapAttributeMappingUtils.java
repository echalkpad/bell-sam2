package com.vennetics.bell.sam.adapters.ldap.utils;

import java.util.HashSet;
import java.util.Set;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vennetics.bell.sam.adapters.ldap.exception.LdapRequestException;

/**
 * Utility methods for mapping LDAP attributes.
 */
public final class LdapAttributeMappingUtils {

    private static final Logger logger = LoggerFactory.getLogger(LdapAttributeMappingUtils.class);


    private LdapAttributeMappingUtils() {

    }


    /**
     * Get a mandatory parameter. Will throw an exception if not found.
     *
     * @param name
     * @param attrs
     * @return
     * @throws NamingException
     */
    public static String getOrRejectIfNull(final String name,
                                           final Attributes attrs) throws NamingException {

        final Attribute attr = attrs.get(name);
        if (attr == null) {
            logger.warn("Mandatory [{}] attribute not found.", name);
            throw new LdapRequestException();
        }

        return getStringAttr(name, attr);
    }

    /**
     * Get an optional parameter or return null if not found.
     *
     * @param name
     * @param attrs
     * @return
     * @throws NamingException
     */
    public static String get(final String name, final Attributes attrs) throws NamingException {
        final Attribute attr = attrs.get(name);
        if (attr == null) {
            logger.debug("Optional [{}] attribute not found.", name);
            return null;
        }

        return getStringAttr(name, attr);
    }

    /**
     * Get all values matching the name as a set.
     *
     * @param name
     * @param attrs
     * @return
     * @throws NamingException
     */
    public static Set<String> getAll(final String name,
                                     final Attributes attrs) throws NamingException {
        final Set<String> result = new HashSet<>();

        final Attribute attribute = attrs.get(name);
        if (attribute != null) {
            final NamingEnumeration<?> attributes = attribute.getAll();
            while (attributes.hasMore()) {
                final Object attr = attributes.next();
                result.add((String) attr);
            }
        }

        return result;
    }

    private static String getStringAttr(final String name,
                                        final Attribute attr) throws NamingException {

        final Object attrVal = attr.get();

        logger.debug("Read {}={}", name, attrVal);

        return attrVal != null ? attrVal.toString() : null;
    }

}
