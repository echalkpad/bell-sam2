package com.vennetics.bell.sam.subscriber.thirdparty.service;

import static org.mockito.Mockito.mock;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.vennetics.bell.sam.sdm.adapter.api.ISdmLdapDao;
import com.vennetics.bell.sam.subscriber.thirdparty.service.exceptions.SubscriberThirdPartySdmErrorTestConfig;

@Configuration
@EnableWebMvc
@Import({ SubscriberThirdPartyControllerConfig.class,
        SubscriberThirdPartySdmErrorTestConfig.class })
@ComponentScan("com.vennetics.bell.sam.rest.config")
public class SubscriberThirdPartyControllerTestConfig {

    @Bean
    public ISdmLdapDao sdmLdapDap() {
        return mock(ISdmLdapDao.class);
    }

    @Bean
    public ISubscriberThirdPartyService subscriberThirdPartyService() {

        return mock(ISubscriberThirdPartyService.class);
    }

}
