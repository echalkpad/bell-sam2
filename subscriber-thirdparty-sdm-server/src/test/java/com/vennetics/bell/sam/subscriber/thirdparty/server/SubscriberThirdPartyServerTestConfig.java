package com.vennetics.bell.sam.subscriber.thirdparty.server;

import com.vennetics.bell.sam.sdm.adapter.api.ISdmLdapDao;
import com.vennetics.bell.sam.subscriber.thirdparty.service.ISubscriberThirdPartyService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import static org.mockito.Mockito.mock;

@Configuration
@ComponentScan({ "com.vennetics.bell.sam.rest.config", "com.vennetics.bell.sam.core.errors",
                "com.vennetics.bell.sam.error" })
public class SubscriberThirdPartyServerTestConfig {

    @Bean
    public ISdmLdapDao sdmLdapDap() {
        return mock(ISdmLdapDao.class);
    }

    @Bean
    public ISubscriberThirdPartyService subscriberThirdPartyService() {

        return mock(ISubscriberThirdPartyService.class);
    }

}
