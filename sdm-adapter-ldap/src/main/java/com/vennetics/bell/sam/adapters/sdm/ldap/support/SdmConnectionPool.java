package com.vennetics.bell.sam.adapters.sdm.ldap.support;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.ldap.pool.factory.PoolingContextSource;
import org.springframework.ldap.pool.validation.DirContextValidator;
import org.springframework.stereotype.Service;

/**
 * Extends Spring {@link PoolingContextSource} to read properties and configure
 * it.
 */
@Service
public class SdmConnectionPool extends PoolingContextSource {

    @Value("${com.vennetics.bell.sam.adapters.sdm.ldap.adapters.sdm.pool.maxActive}")
    private int maxActive;
    @Value("${com.vennetics.bell.sam.adapters.sdm.ldap.adapters.sdm.pool.maxTotal}")
    private int maxTotal;
    @Value("${com.vennetics.bell.sam.adapters.sdm.ldap.adapters.sdm.pool.maxIdle}")
    private int maxIdle;
    @Value("${com.vennetics.bell.sam.adapters.sdm.ldap.adapters.sdm.pool.minIdle}")
    private int minIdle;
    @Value("${com.vennetics.bell.sam.adapters.sdm.ldap.adapters.sdm.pool.maxWait}")
    private int maxWait;
    @Value("${com.vennetics.bell.sam.adapters.sdm.ldap.adapters.sdm.pool.whenExhaustedAction}")
    private byte whenExhaustedAction;
    @Value("${com.vennetics.bell.sam.adapters.sdm.ldap.adapters.sdm.pool.testOnBorrow}")
    private boolean testOnBorrow;
    @Value("${com.vennetics.bell.sam.adapters.sdm.ldap.adapters.sdm.pool.testOnReturn}")
    private boolean testOnReturn;
    @Value("${com.vennetics.bell.sam.adapters.sdm.ldap.adapters.sdm.pool.testWhileIdle}")
    private boolean testWhileIdle;
    @Value("${com.vennetics.bell.sam.adapters.sdm.ldap.adapters.sdm.pool.timeBetweenEvictionRunsMillis}")
    private int timeBetweenEvictionRunsMillis;
    @Value("${com.vennetics.bell.sam.adapters.sdm.ldap.adapters.sdm.pool.numTestsPerEvictionRun}")
    private int numTestsPerEvictionRun;
    @Value("${com.vennetics.bell.sam.adapters.sdm.ldap.adapters.sdm.pool.minEvictableIdleTimeMillis}")
    private int minEvictableIdleTimeMillis;

    @Autowired(required = true)
    public SdmConnectionPool(@Qualifier("sdmContextSource") final LdapContextSource ldapContextSource,
                             @Qualifier("sdmContextValidator") final DirContextValidator sdmContextValidator) {
        super();
        super.setContextSource(ldapContextSource);
        super.setDirContextValidator(sdmContextValidator);

    }

    @PostConstruct
    public void init() {
        super.setMaxTotal(maxTotal);
        super.setMaxActive(maxActive);
        super.setMaxIdle(maxIdle);
        super.setMaxWait(maxWait);
        super.setMinIdle(minIdle);
        super.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
        super.setWhenExhaustedAction(whenExhaustedAction);
        super.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
        super.setNumTestsPerEvictionRun(numTestsPerEvictionRun);
        super.setTestOnBorrow(testOnBorrow);
        super.setTestOnReturn(testOnReturn);
        super.setTestWhileIdle(testWhileIdle);

        logger.debug("Initialised {} with context: {} and validator: {}",
                     this,
                     getContextSource(),
                     getDirContextValidator());
    }


    @Override
    public String toString() {
        return "SdmConnectionPool [maxActive=" + maxActive + ", maxTotal=" + maxTotal + ", maxIdle="
                        + maxIdle + ", minIdle=" + minIdle + ", maxWait=" + maxWait
                        + ", whenExhaustedAction=" + whenExhaustedAction + ", testOnBorrow="
                        + testOnBorrow + ", testOnReturn=" + testOnReturn + ", testWhileIdle="
                        + testWhileIdle + ", timeBetweenEvictionRunsMillis="
                        + timeBetweenEvictionRunsMillis + ", numTestsPerEvictionRun="
                        + numTestsPerEvictionRun + ", minEvictableIdleTimeMillis="
                        + minEvictableIdleTimeMillis + "]";
    }

}
