package com.vennetics.bell.sam.adapters.messaging.smpp.support.autoconfigure;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.cloudhopper.smpp.SmppBindType;
import com.cloudhopper.smpp.SmppClient;
import com.cloudhopper.smpp.SmppSessionConfiguration;
import com.cloudhopper.smpp.impl.DefaultSmppClient;
import com.cloudhopper.smpp.type.Address;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.vennetics.bell.sam.adapters.messaging.smpp.SmppInboundEvent;
import com.vennetics.bell.sam.adapters.messaging.smpp.throttle.ITokenBucket;
import com.vennetics.bell.sam.adapters.messaging.smpp.throttle.RateLimitingTokenBucket;

import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

@Configuration
@EnableScheduling
@ComponentScan(basePackages = { "com.vennetics.bell.sam.adapters.messaging.smpp" })
@EnableConfigurationProperties(SmppProperties.class)
public class SmppConfig {

    @Autowired
    private SmppProperties smppProperties;

    private Subject<SmppInboundEvent, SmppInboundEvent> inboundMessageBus;

    @Bean
    public Subject<SmppInboundEvent, SmppInboundEvent> inboundMessageBus() {
        if (inboundMessageBus == null) {
            inboundMessageBus = new SerializedSubject<>(PublishSubject.create());
        }
        return inboundMessageBus;
    }

    @Bean
    public PhoneNumberUtil phoneNumberUtil() {
        return PhoneNumberUtil.getInstance();
    }

    @Bean
    public SmppSessionConfiguration smppSessionConfiguration() {
        SmppSessionConfiguration smppSessionConfig = new SmppSessionConfiguration();
        smppSessionConfig.setWindowSize(smppProperties.getWindowSize());
        smppSessionConfig.setType(SmppBindType.valueOf(smppProperties.getBindType()));
        smppSessionConfig.setHost(smppProperties.getPrimarySmppConnection().getHost());
        smppSessionConfig.setPort(smppProperties.getPrimarySmppConnection().getPort());
        smppSessionConfig.setConnectTimeout(smppProperties.getPrimarySmppConnection()
                                                          .getConnectionTimeout());
        smppSessionConfig.setSystemId(smppProperties.getSystemId());
        smppSessionConfig.setPassword(smppProperties.getPassword());
        if (StringUtils.isNotBlank(smppProperties.getAddressRange())) {
            final Address addressRange = new Address(smppProperties.getSourceAddress().getTon(),
                                                     smppProperties.getSourceAddress().getNpi(),
                                                     smppProperties.getAddressRange());
            smppSessionConfig.setAddressRange(addressRange);
        }
        smppSessionConfig.getLoggingOptions().setLogBytes(true);
        // to enable monitoring (request expiration)
        smppSessionConfig.setRequestExpiryTimeout(smppProperties.getRequestExpiryTimeout());
        smppSessionConfig.setWindowMonitorInterval(smppProperties.getWindowMonitorInterval());
        return smppSessionConfig;
    }

    @Bean
    public ScheduledExecutorService monitorExecutor() {
        return Executors.newScheduledThreadPool(1, new ThreadFactory() {
            private AtomicInteger sequence = new AtomicInteger(0);

            @Override
            public Thread newThread(final Runnable runnable) {
                final Thread monitorThread = new Thread(runnable);
                monitorThread.setName("SmppSessionWindowMonitorPool-" + sequence.getAndIncrement());
                return monitorThread;
            }
        });
    }

    @Bean
    public SmppClient smppClient() {
        return new DefaultSmppClient(Executors.newCachedThreadPool(),
                                     smppProperties.getMaxConcurrentSessions(),
                                     monitorExecutor());
    }
    
    @Bean
    @Qualifier("outBoundRateLimitingTokenBucket")
    public ITokenBucket outBoundRateLimitingTokenBucket() {
        return new RateLimitingTokenBucket("Outgoing rate limiting token bucket",
                                           smppProperties.getOutBoundThrottlingRate()
                                                         .getMaxTasksPerInterval(),
                                           smppProperties.getOutBoundThrottlingRate()
                                                         .getIntervalInMilliseconds());
    }
    
    @Bean
    @Qualifier("inBoundRateLimitingTokenBucket")
    public ITokenBucket inBoundRateLimitingTokenBucket() {
        return new RateLimitingTokenBucket("Incoming rate limiting token bucket",
                                           smppProperties.getInBoundThrottlingRate()
                                                         .getMaxTasksPerInterval(),
                                           smppProperties.getInBoundThrottlingRate()
                                                         .getIntervalInMilliseconds());
    }
    
    @Bean
    public long submitMessageTimeoutMilliseconds() {
        return smppProperties.getSubmitMessageTimeoutMilliseconds();
    }
    
    @Bean
    public long enquireLinkTimeoutMilliseconds() {
        return smppProperties.getEnquireLinkTimeoutMilliseconds();
    }
    
    @Bean
    public long outBoundThrottlingRequestTimeoutMilliseconds() {
        return smppProperties.getOutBoundThrottlingRate().getThrottlingRequestTimeoutMilliseconds();
    }
    
    @Bean
    public long smppUnbindTimeoutInMilliseconds() {
        return smppProperties.getSmppUnbindTimeoutInMilliseconds();
    }
}
