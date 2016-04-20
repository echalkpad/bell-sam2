package com.vennetics.bell.sam.core.cassandra.commands;

import com.netflix.hystrix.HystrixCommandGroupKey;
import com.vennetics.bell.sam.core.commands.hystrix.AbstractHystrixCommand;
import com.vennetics.bell.sam.core.cassandra.exceptions.CassandraDataAccessException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Hystrix Command to perform the CrudRepository I/O
 */
@SuppressWarnings({"PMD", "squid:S00112"})
public class ProceedInHystrixCommand extends AbstractHystrixCommand<Object> {

    private static final Logger logger = LoggerFactory.getLogger(ProceedInHystrixCommand.class);

    private ProceedingJoinPoint pjp;

    public ProceedInHystrixCommand(final ProceedingJoinPoint pjp) {
        super(HystrixCommandGroupKey.Factory.asKey("proceedInHystrixCommand"));
        this.pjp = pjp;
        logger.debug("Initialised proceedInHystrixCommand");
    }

    @Override
    protected Object run() {

        try {
            logger.debug("Invoking Hystrix proceedInHystrixCommand");
            return pjp.proceed();
        } catch (final Throwable throwable) {
            throw new CassandraDataAccessException("Failed proceeding with advice.", throwable);
        }
    }


}
