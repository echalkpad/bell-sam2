package com.vennetics.bell.sam.logaggregator;

import org.slf4j.Logger;

import org.slf4j.LoggerFactory;
@SuppressWarnings({ "checkstyle:hideutilityclassconstructor", "squid:S1118" })
public class LogAggregatorApplication {
   
    private static final Logger logger = LoggerFactory.getLogger(LogAggregatorApplication.class);

    public static void main(final String[] args) {
        logger.info("Successfully created log aggregator container - exiting.");
    }
}
