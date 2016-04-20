package com.vennetics.bell.sam.netapi;

import com.vennetics.bell.sam.core.CoreErrorsConfig;
import com.vennetics.bell.sam.error.SamErrorsConfig;
import com.vennetics.bell.sam.error.adapters.IErrorAdapter;
import com.vennetics.bell.sam.error.adapters.NetapiErrorAdapter;
import com.vennetics.bell.sam.error.mapper.IExceptionTypeMapper;
import com.vennetics.bell.sam.netapi.config.NetApiAdaptersConfig;
import com.vennetics.bell.sam.netapi.sms.ISmsService;
import com.vennetics.bell.sam.netapi.sms.SmsService;
import com.vennetics.bell.sam.rest.config.RestConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;

import static org.mockito.Mockito.mock;

/**
 * Dummy mainline to define configuration for web integration testing.
 * Replace SmsService bean with a mock. TODO SJ Determine if there is a better way of doing this...
 */

@SpringBootApplication
@ComponentScan(basePackages = { "com.vennetics.bell.sam.netapi" },
                excludeFilters = {
                                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = SmsService.class),
                                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = NetApiAdaptersConfig.class) })
@Import({ SamErrorsConfig.class, RestConfig.class, CoreErrorsConfig.class })
public class TestConfiguration {

    public static void main(final String[] args) {
        SpringApplication.run(TestConfiguration.class, args);
    }

    @Bean(name = "netApiSmsService")
    public ISmsService smsService() {
        return mock(ISmsService.class);
    }

    @Bean
    public IExceptionTypeMapper exceptionTypeMapper() {
        return mock(IExceptionTypeMapper.class);
    }

    @Bean(name = "netapiErrorAdapter")
    public IErrorAdapter errorAdapter() {
        return mock(NetapiErrorAdapter.class);
    }
}
