package com.vennetics.bell.sam.adapters.ldap;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapOperations;
import org.springframework.ldap.query.LdapQuery;

/**
 * Provides common logic to LDAP search commands.
 *
 * @param <R>
 *            the command result type
 */
public abstract class AbstractLdapSearchCommand<R> extends AbstractLdapTemplateCommand<List<R>> {

    private static final Logger logger = LoggerFactory.getLogger(AbstractLdapSearchCommand.class);

    @SuppressWarnings("rawtypes")
    private static final List EMPTY_RESULT = new ArrayList<>(0);

    private final LdapQuery ldapQuery;



    private final AttributesMapper<R> attributesMapper;

    /**
     * Constructor.
     *
     * @param commandGroupKey
     *            hystrix command group key.
     * @param commandNameKey
     *            hystrix command name key.
     * @param ldapTemplate
     *            the spring ldapTemplate
     * @param searchBase
     *            the base to search on the tree
     * @param attribute
     *            the attribute to search on the tree
     * @param value
     *            the value to apply to the search
     * @param searchScope
     *            the scope of the search
     */
    public AbstractLdapSearchCommand(final String commandGroupKey,
                                     final String commandNameKey,
                                     final LdapOperations ldapTemplate,
                                     final LdapQuery ldapQuery,
                                     final AttributesMapper<R> attributesMapper) {
        super(commandGroupKey, commandNameKey, ldapTemplate);
        this.ldapQuery = ldapQuery;
        this.attributesMapper = attributesMapper;
    }

    /**
     * Search the LDAP directory.
     *
     * @return the LDAP result or propagate any exceptions
     */
    @SuppressWarnings({ "unchecked" })
    protected List<R> search() {

        logger.debug("Querying ldapQuery:{} ", ldapQuery);
        try {
            final List<R> result = getLdapTemplate().search(ldapQuery,
                                                            attributesMapper);

            if (result == null) {
                return EMPTY_RESULT;
            }

            return result;

        } catch (final RuntimeException e) {

            return handleLdapRuntimeException(e, "search [query [" + ldapQuery + "]");

        }
    }
}
