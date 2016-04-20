package com.vennetics.bell.sam.adapters.ldap;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ldap.core.support.LdapContextSource;

/**
 * Context source for getting connections to the LDAP server.Disables JVM native
 * connection pooling so Spring overlay can be used.
 */
public class SpringPooledLdapContextSource extends LdapContextSource {

    private static final Logger logger = LoggerFactory.getLogger(SpringPooledLdapContextSource.class);

    private final String base;


    private final String suppliedUrls;

    private final String ldapConnectTimeout;

    private final String requestLoggingEnabled;

    // System.out needed for LDAP trace. Can't use SLF4J
    @SuppressWarnings({ "squid:S106" })
    public SpringPooledLdapContextSource(final String base,
                                         final String userDn,
                                         final String password,
                                         final String suppliedUrls,
                                         final String ldapConnectTimeout,
                                         final String requestLoggingEnabled) {
        super();

        this.base = base;
        this.suppliedUrls = suppliedUrls;
        this.ldapConnectTimeout = ldapConnectTimeout;
        this.requestLoggingEnabled = requestLoggingEnabled;

        logger.debug("Creating LDAP context: {}", this);

        setPooled(false);

        String[] urls;
        if (suppliedUrls.contains(",")) {
            urls = suppliedUrls.split(",");
        } else {
            urls = new String[] { suppliedUrls };
        }

        setUrls(urls);

        setBase(base);

        if (!StringUtils.isBlank(userDn)) {
            setUserDn(userDn);
        }
        if (!StringUtils.isBlank(password)) {
            setPassword(password);
        }

        // Set JVM pool to false so that Spring can manage the connection
        // pooling.
        final Map<String, Object> baseEnv = new HashMap<>();
        baseEnv.put("com.sun.jndi.ldap.connect.pool", "false");
        baseEnv.put("com.sun.jndi.ldap.connect.timeout", ldapConnectTimeout);

        // For debugging LDAP protocol. Not typically enabled.
        if (Boolean.getBoolean(requestLoggingEnabled)) {
            baseEnv.put("com.sun.jndi.ldap.trace.ber", System.out);
        }

        setBaseEnvironmentProperties(baseEnv);

        logger.debug("Created LDAP context: {}", this);
    }


    @Override
    public String toString() {
        return "SpringPooledLdapContextSource [base=" + base + ", userDn=" + getUserDn()
                        + ", password=" + getPassword() + ", urls=" + suppliedUrls
                        + ", ldapConnectTimeout="
                        + ldapConnectTimeout + ", requestLoggingEnabled=" + requestLoggingEnabled
                        + "]";
    }

}
