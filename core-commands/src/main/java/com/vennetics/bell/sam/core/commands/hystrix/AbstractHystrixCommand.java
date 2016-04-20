package com.vennetics.bell.sam.core.commands.hystrix;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.exception.HystrixBadRequestException;

/**
 * Provides functionality common to all jbox/vennetics hystrix commands.
 * <P>
 * Placeholder for now.
 * 
 * @param <R>
 *            the return type of the command
 */
public abstract class AbstractHystrixCommand<R> extends HystrixCommand<R> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractHystrixCommand.class);

    public AbstractHystrixCommand(final HystrixCommandGroupKey group) {
        super(group);
    }

    public AbstractHystrixCommand(final HystrixCommand.Setter setter) {
        super(setter);
    }

    /**
     * Create a {@link HystrixBadRequestException}. Exceptions of this type will
     * not trigger the opening of a circuit.
     * 
     * @param message
     * @param e
     * @return
     */
    public HystrixBadRequestException createHystrixBadRequestException(final String message,
                                                                       final Throwable e) {

        LOGGER.debug("Translating exception {} to HystrixBadRequestException", e.getClass());
        return new HystrixBadRequestException(message, e);
    }
}
