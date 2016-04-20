package com.vennetics.bell.sam.adapters.sdm.ldap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.vennetics.bell.sam.adapters.ldap.SpringPooledLdapContextSource;

/**
 * Wrapper class to configure the Sdm LDAP context
 */
@Service
public class SdmContextSource extends SpringPooledLdapContextSource {

    @Autowired
    public SdmContextSource(@Value("${com.vennetics.bell.sam.adapters.sdm.ldap.adapters.sdm.pool.connectTimeout}") final String ldapConnectTimeout,
                            @Value("${com.vennetics.bell.sam.adapters.sdm.ldap.adapters.sdm.pool.loggingEnabled}") final String requestLoggingEnabled,
                            @Value("${com.vennetics.bell.sam.adapters.sdm.ldap.adapters.sdm.pool.base}") final String base,
                            @Value("${com.vennetics.bell.sam.adapters.sdm.ldap.adapters.sdm.pool.userDn}") final String userDn,
                            @Value("${com.vennetics.bell.sam.adapters.sdm.ldap.adapters.sdm.pool.password}") final String password,
                            @Value("${com.vennetics.bell.sam.adapters.sdm.ldap.adapters.sdm.pool.urls}") final String suppliedUrls) {
        super(base, userDn, password, suppliedUrls, ldapConnectTimeout, requestLoggingEnabled);
    }


}
