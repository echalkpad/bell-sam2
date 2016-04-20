package com.vennetics.bell.sam.adapters.sdm.stub;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Spring bean configuration for real SDM adapter.
 */
@Configuration
@ComponentScan("com.vennetics.bell.sam.adapters.sdm.stub")
@Profile("!production")
@SuppressWarnings("squid:S2094") // Suppressing empty interface warning in
                                 // SonarQube
public class SdmStubConfig {

}
