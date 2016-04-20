package com.vennetics.bell.sam.subscriber.thirdparty.service;

import com.vennetics.bell.sam.model.subscriber.thirdparty.FeatureCodes;
import com.vennetics.bell.sam.model.subscriber.thirdparty.SearchFilterType;
import com.vennetics.bell.sam.model.subscriber.thirdparty.SubscriberProfile;
import rx.Observable;

import java.util.List;

/**
 * Service interface for Sub-X.
 */
public interface ISubscriberThirdPartyService {

    /**
     * Query the profile
     *
     * @param filterAttribute the attribute to query on
     * @param filterValue     the filter value
     * @return the subscriber profile
     */
    Observable<SubscriberProfile> getSubscriberProfile(SearchFilterType filterAttribute,
                                                       String filterValue);

    /**
     * Query to see if subscriber identified by mdn, and associated with the affliate as identified by
     * the affilateId has the features codes, in the supplied List, active.
     *
     * @param mdn           Subscriber identifier
     * @param affliateId    Affiliate Identifier
     * @param featureCodes  Feature code to check if active
     * @return A non-null instance.
     */
    Observable<FeatureCodes> hasFeatureCodes(String mdn,
                                             String affliateId,
                                             List<String> featureCodes);

}
