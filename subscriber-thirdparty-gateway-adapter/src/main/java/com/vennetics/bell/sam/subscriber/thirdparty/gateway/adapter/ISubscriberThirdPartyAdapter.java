package com.vennetics.bell.sam.subscriber.thirdparty.gateway.adapter;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.vennetics.bell.sam.model.subscriber.thirdparty.FeatureCodes;
import com.vennetics.bell.sam.model.subscriber.thirdparty.SearchFilterType;
import com.vennetics.bell.sam.model.subscriber.thirdparty.SubscriberProfile;

/**
 * Defines an interface to the Subx REST service, for use by the ParlayX Soap adapter.
 */

public interface ISubscriberThirdPartyAdapter {
    /**
     * Returns the profile for the requested subscriber address.
     * @param serviceIdentifier
     *      identifies the internal service instance to use.
     * @param searchFilter
     *      One of (MDN,SUBID,MEID,UUID,IMSI)
     * @param filterValue
     *      The value of the filter
     * @return
     *      A {@link SubscriberProfile} Observable for the requested subscriber.
     */
    ResponseEntity<SubscriberProfile> getSubscriberProfile(String serviceIdentifier, SearchFilterType searchFilter, String filterValue);

    /**
     * Reports whether a set of feature codes are enabled.
     * @param serviceIdentifier
     *      identifies the internal service instance to use.
     * @param mdn
     *      The mdn of the subscriber for which feature codes are to be checked
     * @param affiliateId
     *      The affiliate for which feature codes are to be checked.
     * @param featureCodes
     *      The feature codes to be checked.
     * @return
     *      A {@link List} of booleans, indicating whether the corresponding feature in <tt>featureCodes</tt>
     *      is enabled.
     */
    ResponseEntity<FeatureCodes> hasFeatureCodes(String serviceIdentifier, String mdn, String affiliateId, List<String> featureCodes);
}
