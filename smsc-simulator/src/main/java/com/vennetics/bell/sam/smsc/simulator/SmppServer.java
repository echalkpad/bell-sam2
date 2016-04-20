package com.vennetics.bell.sam.smsc.simulator;

import com.cloudhopper.smpp.SmppServerConfiguration;
import com.cloudhopper.smpp.SmppServerHandler;
import com.cloudhopper.smpp.impl.DefaultSmppServer;
import com.cloudhopper.smpp.type.SmppChannelException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Runs our SMSC Server, pulling in configuration to modify server behaviour.
 *
 * @author markcorkery
 */
@Component
public class SmppServer implements CommandLineRunner {

    private static final Logger LOG = LoggerFactory.getLogger(SmppServer.class);

    @Autowired
    private SmppServerHandler smppServerHandler;

    @Value("${smpp.port:2776}")
    private Integer port;

    @Value("${smpp.maxConnectionSize:10}")
    private Integer maxConnectionSize;

    @Value("${smpp.nonBlockingSocketsEnabled:true}")
    private Boolean nonBlockingSocketsEnabled;

    @Value("${smpp.defaultRequestExpiryTimeout:30000}")
    private Integer defaultRequestExpiryTimeout;

    @Value("${smpp.defaultWindowMonitorInterval:15000}")
    private Integer defaultWindowMonitorInterval;

    @Value("${smpp.defaultWindowSize:5}")
    private Integer defaultWindowSize;

    @Value("${smpp.defaultSessionCountersEnabled:true}")
    private Boolean defaultSessionCountersEnabled;

    @Value("${smpp.jmxEnabled:true}")
    private Boolean jmxEnabled;

    @Override
    public void run(final String... args) throws SmppChannelException {
        final ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
        final ScheduledThreadPoolExecutor monitorExecutor = (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(
                        1,
                        new ThreadFactory() {
                            private final AtomicInteger sequence = new AtomicInteger(0);

                            @Override
                            public Thread newThread(final Runnable runnable) {
                                final Thread t = new Thread(runnable);
                                t.setName("SmppServerSessionWindowMonitorPool-"
                                                          + sequence.getAndIncrement());
                                return t;
                            }
                        });

        // create a server configuration
        final SmppServerConfiguration configuration = new SmppServerConfiguration();
        configuration.setPort(port);
        configuration.setMaxConnectionSize(maxConnectionSize);
        configuration.setNonBlockingSocketsEnabled(nonBlockingSocketsEnabled);
        configuration.setDefaultRequestExpiryTimeout(defaultRequestExpiryTimeout);
        configuration.setDefaultWindowMonitorInterval(defaultWindowMonitorInterval);
        configuration.setDefaultWindowSize(defaultWindowSize);
        configuration.setDefaultWindowWaitTimeout(defaultRequestExpiryTimeout);
        configuration.setDefaultSessionCountersEnabled(defaultSessionCountersEnabled);
        configuration.setJmxEnabled(jmxEnabled);

        // Create a server
        final DefaultSmppServer smppServer = new DefaultSmppServer(configuration,
                                                                   smppServerHandler,
                                                                   executor,
                                                                   monitorExecutor);

        // Start up the server
        LOG.info("Starting SMPP server...");
        smppServer.start();
        LOG.info("SMPP server started");
    }

}
