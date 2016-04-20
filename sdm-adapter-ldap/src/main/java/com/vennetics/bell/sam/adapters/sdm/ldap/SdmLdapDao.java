package com.vennetics.bell.sam.adapters.sdm.ldap;

import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.ldap.core.LdapOperations;
import org.springframework.stereotype.Service;

import com.vennetics.bell.sam.adapters.sdm.ldap.commands.SdmAccountQueryCommand;
import com.vennetics.bell.sam.sdm.adapter.api.ISdmLdapDao;
import com.vennetics.bell.sam.sdm.adapter.api.SdmAccount;
import com.vennetics.bell.sam.sdm.adapter.api.SdmQueryFilter;

import rx.Observable;

@Service
@Profile("production")
public class SdmLdapDao implements ISdmLdapDao {

    private static final Logger logger = LoggerFactory.getLogger(SdmLdapDao.class);

    private final LdapOperations ldapTemplate;

    @Autowired(required = true)
    public SdmLdapDao(final LdapOperations ldapTemplate) {
        super();
        this.ldapTemplate = ldapTemplate;
    }

    @PostConstruct
    public void init() {
        logger.info("Initialising production SdmLdapDao");
    }

    @Override
    public Observable<List<SdmAccount>> getSubscriberProfile(final SdmQueryFilter queryFilter,
                                                             final Set<String> attributes) {

        return new SdmAccountQueryCommand(ldapTemplate, queryFilter, attributes).toObservable();
    }

    @Override
    public Observable<List<SdmAccount>> getSubscriberProfile(final Set<SdmQueryFilter> filters,
                                                             final Set<String> attributes) {

        return new SdmAccountQueryCommand(ldapTemplate, filters, attributes).toObservable();
    }

}
