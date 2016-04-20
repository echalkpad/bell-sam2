package com.vennetics.microservices.common.api.model.admin.v1.application;

import rx.Observable;

/**
 * Accessor interface for security APIs loading application user details.
 */
public interface IApplicationDetailsService {


    /**
     * Return the application user details if found.
     *
     * @param clientId
     *            the clientId/username
     * @return the details or null
     */
    Observable<ApplicationUserDetails> loadByClientId(String clientId);

}
