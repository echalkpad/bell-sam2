package com.vennetics.bell.sam.sdm.adapter.api;

import java.util.List;
import java.util.Set;

import rx.Observable;

/**
 * Interface for SDM Ldap calls
 */
public interface ISdmLdapDao {

    /**
     * Query LDAP attributes where an SDM indexed attribute equals the supplied
     * value e.g. attrs by MDN, IMSI, SubID etc.
     *
     * <B>Note: in normal operation this will return a single account but Bell
     * have informed us it is possible to get multiple for an incorrectly
     * provisioned user. This dao delegates how this situation should be handled
     * to the upper tier. </B>
     *
     * @param filter
     *            the filter to apply on the query
     * @param attributes
     *            the attributes to query.
     * @return an observable list of accounts. Usually only one.
     */
    Observable<List<SdmAccount>> getSubscriberProfile(final SdmQueryFilter filter,
                                                final Set<String> attributes);

    /**
     * Query LDAP attributes where an SDM indexed attribute equals the supplied
     * value e.g. attrs by MDN, IMSI, SubID etc.
     *
     * <B>Note: in normal operation this will return a single account but Bell
     * have informed us it is possible to get multiple for an incorrectly
     * provisioned user. This dao delegates how this situation should be handled
     * to the upper tier. </B>
     *
     * @param filters
     *            the filters to apply on the query
     * @param attributes
     *            the attributes to query.
     * @return an observable list of accounts. Usually only one.
     */
    Observable<List<SdmAccount>> getSubscriberProfile(Set<SdmQueryFilter> filters,
                                                      final Set<String> attributes);

}
