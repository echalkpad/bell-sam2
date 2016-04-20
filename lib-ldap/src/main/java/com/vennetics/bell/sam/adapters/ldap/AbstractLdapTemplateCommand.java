package com.vennetics.bell.sam.adapters.ldap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.ldap.CommunicationException;
import org.springframework.ldap.NameNotFoundException;
import org.springframework.ldap.core.LdapOperations;

import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.vennetics.bell.sam.adapters.ldap.exception.LdapNetworkException;
import com.vennetics.bell.sam.core.commands.hystrix.AbstractHystrixCommand;
import com.vennetics.bell.sam.core.exception.BadRequestException;
import com.vennetics.bell.sam.core.exception.PlatformFailedException;

/**
 * Provides common logic to Spring LDAP commands.
 *
 * @param <R>
 *            the LDAP query return type
 */
public abstract class AbstractLdapTemplateCommand<R> extends AbstractHystrixCommand<R> {

    private static final Logger logger = LoggerFactory.getLogger(AbstractLdapTemplateCommand.class);

    private final LdapOperations ldapTemplate;

    /**
     * Constructor.
     *
     * @param commandGroupKey
     *            hystrix command group key.
     * @param commandNameKey
     *            hystrix command name key.
     * @param ldapTemplate
     *            the spring ldapTemplate
     */
    public AbstractLdapTemplateCommand(final String commandGroupKey,
                                       final String commandNameKey,
                                       final LdapOperations ldapTemplate) {
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey(commandGroupKey))
                    .andCommandKey(HystrixCommandKey.Factory.asKey(commandNameKey)));
        this.ldapTemplate = ldapTemplate;
    }

    LdapOperations getLdapTemplate() {
        return ldapTemplate;
    }

    /**
     * Common logic for handling of LDAP runtime exceptions from Spring. Spring
     * has a 1:1 mapping with JNDI specifications which have a defined exception
     * for each status code.
     *
     * @see http://docs.oracle.com/javase/tutorial/jndi/ldap/exceptions.html
     *
     * @param e
     *            the runtime exception.
     * @param queryDetails
     *            details used in fault logging explaining the underlying query.
     *
     * @return a fallback result if appropriate.
     */
    protected R handleLdapRuntimeException(final RuntimeException e, final String queryDetails) {
        if (e instanceof BadRequestException) {
            // rethrow
            throw e;
        } else if (e instanceof NameNotFoundException) {

            // LDAP Status code 32 = No Such Object Exists
            logger.debug("NameNotFoundException for search on query [{}] error: {}",
                         queryDetails,
                         e.getMessage());

            return null;

        } else if (e instanceof CommunicationException) {

            throw new LdapNetworkException(e);

        } else if (e instanceof DataAccessException) {

            // DataAccessException This will not be thrown by Spring LDAP during
            // a query but can be
            // thrown by the data access layer for connection management
            throw new LdapNetworkException(e);
        }

        // No comms fault here - internal error
        throw new PlatformFailedException("Failed LDAP command", e);

    }
}
