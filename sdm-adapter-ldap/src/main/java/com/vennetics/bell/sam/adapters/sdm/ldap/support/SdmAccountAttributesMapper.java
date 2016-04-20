package com.vennetics.bell.sam.adapters.sdm.ldap.support;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ldap.core.AttributesMapper;

import com.vennetics.bell.sam.adapters.ldap.exception.LdapRequestException;
import com.vennetics.bell.sam.adapters.ldap.utils.LdapAttributeMappingUtils;
import com.vennetics.bell.sam.sdm.adapter.api.SdmAccount;

/**
 * Maps a Spring LDAP response into a {@link MobilityLdapProfile}
 */
public class SdmAccountAttributesMapper implements AttributesMapper<SdmAccount> {

    private static final Logger logger = LoggerFactory.getLogger(SdmAccountAttributesMapper.class);

    private final Set<String> attributes;

    public SdmAccountAttributesMapper(final Set<String> attributes) {
        super();
        this.attributes = attributes;
    }

    @Override
    public SdmAccount mapFromAttributes(final Attributes attrs) throws NamingException {

        final Map<String, Set<String>> accountDetails = new HashMap<>();

        if (attributes != null && !attributes.isEmpty()) {
            logger.debug("Mapping attributes: {}", attributes);
            attributes.stream().forEach(attributeName -> {

                try {
                    final Set<String> attributeValues = getAttributes(attrs, attributeName);

                    accountDetails.put(attributeName, attributeValues);
                } catch (final NamingException e) {
                    // Do we want to continue or throw error here?
                    throw new LdapRequestException(e);
                }

            });
        } else {

            logger.debug("Mapping all found attributes.");

            // Map all
            final NamingEnumeration<? extends Attribute> allAttrs = attrs.getAll();

            while (allAttrs.hasMore()) {
                final Attribute attribute = allAttrs.next();

                final Set<String> attributeValues = getAttributes(attrs, attribute.getID());

                accountDetails.put(attribute.getID(), attributeValues);

            }
        }

        final SdmAccount result = new SdmAccount(accountDetails);
        logger.debug("Maped to {}", result);

        return result;
    }

    /**
     * Protected method for test over-ride and exception testing.
     *
     * @param attrs
     * @param attributeName
     * @return the set of attribute values.
     * @throws NamingException
     */
    protected Set<String> getAttributes(final Attributes attrs,
                                        final String attributeName) throws NamingException {
        return LdapAttributeMappingUtils.getAll(attributeName, attrs);

    }
}
