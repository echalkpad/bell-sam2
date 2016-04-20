package com.vennetics.bell.sam.core.commands.hystrix;

import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.vennetics.bell.sam.core.exception.BadRequestException;

/**
 * Provides functionality common to sam/vennetics hystrix commands that should not cause a circuit break to open on failure.
 * <p>
 * Main role is default handling of {@link BadRequestException}
 *
 * @param <R> the return type of the command
 */
public abstract class AbstractHystrixDisabledCircuitBreakerCommand<R>
                extends AbstractHystrixCommand<R> implements IApiCommand {

    public AbstractHystrixDisabledCircuitBreakerCommand(final String commandGroupKey,
                                                        final String commandNameKey) {
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey(commandGroupKey))
                    .andCommandKey(HystrixCommandKey.Factory.asKey(commandNameKey))
                    .andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
                                                                          .withCircuitBreakerEnabled(
                                                                                          false)));
    }

    @Override protected final R run() {
        // Default implementation catches and translates ClientException to
        // HystrixBadRequestException
        try {
            return runCommand();
        } catch (final BadRequestException e) {

            throw createHystrixBadRequestException("Command failed for " + getCommandKey().name(),
                                                   e);
        }
    }

    /**
     * Implementations of this should execute the  invocation.
     * <p>
     * They should not catch {@link BadRequestException} unless custom logic is
     * needed in the command.
     *
     * @return
     */
    protected abstract R runCommand();

}
