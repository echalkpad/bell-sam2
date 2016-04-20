package com.vennetics.bell.sam.registry;

import java.util.Collection;

/**
 * Manages access to the registry of enabler services and protocols.
 */

@SuppressWarnings("squid:S1609") // Avoid Sonarqube suggestion about FunctionalInterface. Other methods will follow...
public interface IServiceRegistry {

    /**
     * Deliver names of enablers implementing a given protocol.
     * @param protocolIdentifier
     *      the identity of the protocol for which enablers are to be delivered.
     * @return
     *      a non-null collection of enabler names.
     */
    Collection<String> servicesForProtocol(final String protocolIdentifier);
}
