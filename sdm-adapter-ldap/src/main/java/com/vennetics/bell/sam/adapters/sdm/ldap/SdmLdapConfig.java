package com.vennetics.bell.sam.adapters.sdm.ldap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.ldap.core.LdapOperations;
import org.springframework.ldap.core.LdapTemplate;

import com.vennetics.bell.sam.adapters.sdm.ldap.support.SdmConnectionPool;

/**
 * Spring bean configuration for real SDM adapter.
 */
@Configuration
@ComponentScan("com.vennetics.bell.sam.adapters.sdm.ldap")
@Profile("production")
public class SdmLdapConfig {

    private static final Logger logger = LoggerFactory.getLogger(SdmLdapConfig.class);

    @Autowired(required = true)
    private SdmConnectionPool connectionPool;

    @Bean
    LdapOperations sdmLdapOperations() {

        final LdapTemplate ldapTemplate = new LdapTemplate(connectionPool);
        logger.debug("Created {}", ldapTemplate);
        return ldapTemplate;
    }

}
