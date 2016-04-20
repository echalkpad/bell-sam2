package com.vennetics.bell.sam.subscriber.thirdparty.service.autoconfigure;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

@RefreshScope
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "sdm.service")
public class SdmServiceProperties {

    private List<String> validOus;

    public void setValidOus(final List<String> validOus) {
        this.validOus = validOus;
    }

    public List<String> getValidOus() {
        if (validOus == null) {
            validOus = new ArrayList<>();
        }
        return validOus;
    }
}
