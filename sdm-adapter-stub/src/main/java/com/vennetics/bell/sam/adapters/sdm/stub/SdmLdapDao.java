package com.vennetics.bell.sam.adapters.sdm.stub;

import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import com.vennetics.bell.sam.sdm.adapter.api.ISdmLdapDao;
import com.vennetics.bell.sam.sdm.adapter.api.SdmAccount;
import com.vennetics.bell.sam.sdm.adapter.api.SdmQueryFilter;

import rx.Observable;

/**
 * Pre-canned implementation of SDM Ldap DAO for development environments.
 *
 */
@Service
@Profile("!production")
public class SdmLdapDao implements ISdmLdapDao {

    private static final Logger logger = LoggerFactory.getLogger(SdmLdapDao.class);

    @PostConstruct
    public void init() {
        logger.info("Initialising pre-canned SdmLdapDao");
    }

    @Override
    public Observable<List<SdmAccount>> getSubscriberProfile(final SdmQueryFilter filter,
                                                             final Set<String> attributes) {

        return new GetSubscriberProfileCommand().observe();
    }

    @Override
    public Observable<List<SdmAccount>> getSubscriberProfile(final Set<SdmQueryFilter> filters,
                                                             final Set<String> attributes) {

        return new GetSubscriberProfileCommand().observe();
    }

}
