package com.vennetics.bell.sam.adapters.sdm.stub;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.vennetics.bell.sam.sdm.adapter.api.SdmAccount;

/**
 * A HystrixCommand to perform the SDM Ldap I/O
 *
 */
public class GetSubscriberProfileCommand extends HystrixCommand<List<SdmAccount>> {

    private static final Logger logger = LoggerFactory.getLogger(GetSubscriberProfileCommand.class);


    /**
     * GetSubscriberProfileCommand constructor
     *
     * @param searchFilter
     * @param filterValue
     * @param attributes
     */
    public GetSubscriberProfileCommand() {
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("SubscriberThirdParty"))
                    .andCommandKey(HystrixCommandKey.Factory.asKey("GetSubscriberProfile")));

    }

    /**
     * This method will perform an SDM Ldap call
     *
     * @return SdmAccount
     */
    @Override
    protected List<SdmAccount> run() {
        logger.debug("Running Hystrix wrapped subscriberprofile command to return a populated SdmAccount");
        // Pre canned testing data for Sprint 2
        final Set<String> eamLanguageAttributes = new HashSet<>();
        eamLanguageAttributes.add("E");

        final Map<String, Set<String>> result = new HashMap<>();
        result.put("eamLanguage", eamLanguageAttributes);

        return Arrays.asList(new SdmAccount(result));
    }
}
