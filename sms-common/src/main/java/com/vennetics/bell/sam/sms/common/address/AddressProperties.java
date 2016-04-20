package com.vennetics.bell.sam.sms.common.address;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Created on 08/02/2016.
 */
@Configuration
@ConfigurationProperties(prefix = "address")
public class AddressProperties {

    private String defaultRegion;

    public String getDefaultRegion() {
        return defaultRegion;
    }

    public void setDefaultRegion(final String defaultRegion) {
        this.defaultRegion = defaultRegion;
    }

}
