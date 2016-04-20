package com.vennetics.bell.sam.subscriber.thirdparty.gateway.adapter;

import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.vennetics.bell.sam.model.subscriber.thirdparty.FeatureCodes;
import com.vennetics.bell.sam.model.subscriber.thirdparty.SearchFilterType;
import com.vennetics.bell.sam.model.subscriber.thirdparty.SubscriberProfile;
import com.vennetics.bell.sam.subscriber.thirdparty.gateway.adapter.commands.GetSubscriberProfileCommand;
import com.vennetics.bell.sam.subscriber.thirdparty.gateway.adapter.commands.HasFeatureCodesCommand;

/**
 * Default implementation of {@link ISubscriberThirdPartyAdapter}.
 */

@Service
@EnableHystrix
public class SubscriberThirdPartyAdapter implements ISubscriberThirdPartyAdapter {

    private static final Logger logger = LoggerFactory.getLogger(SubscriberThirdPartyAdapter.class);

    private RestTemplate subscriberThirdPartyRestTemplate;

    @Autowired
    private SubscriberThirdPartyConfiguration subxConfiguration;

    @PostConstruct
    public void init() {
        subscriberThirdPartyRestTemplate = subxConfiguration.subscriberThirdPartyRestTemplate();
    }

    @Override
    public ResponseEntity<SubscriberProfile> getSubscriberProfile(final String serviceIdentifier,
                                                                  final SearchFilterType searchFilter,
                                                                  final String filterValue) {
        logger.debug("getSubscriberProfile searchFilter:{} filterValue:{}",
                     searchFilter,
                     filterValue);
        return new GetSubscriberProfileCommand(
                                               serviceIdentifier,
                                               searchFilter.toString(),
                                               filterValue,
                                               subscriberThirdPartyRestTemplate).execute();
    }

    @Override
    public ResponseEntity<FeatureCodes> hasFeatureCodes(final String serviceIdentifier,
                                                         final String mdn,
                                                         final String affiliateId,
                                                         final List<String> featureCodes) {
        logger.debug("hasFeatureCodes mdn:{} affiliateId:{} featureCodes:{}",
                     mdn,
                     affiliateId,
                     featureCodes);
        return new HasFeatureCodesCommand(
                                          serviceIdentifier,
                                          mdn,
                                          affiliateId,
                                          featureCodes,
                                          subscriberThirdPartyRestTemplate).execute();
    }
}
