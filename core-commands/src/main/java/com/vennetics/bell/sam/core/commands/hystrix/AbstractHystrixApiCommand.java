package com.vennetics.bell.sam.core.commands.hystrix;

import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.HystrixCommandProperties.ExecutionIsolationStrategy;
import com.vennetics.bell.sam.core.exception.BadRequestException;
/**
 * Provides functionality common to all sam/vennetics hystrix API commands. Copied from JBox.
 * <P>
 * Main role is default handling of {@link BadRequestException}
 *
 * @param <R>
 *            the return type of the command
 */
public abstract class AbstractHystrixApiCommand<R> extends AbstractHystrixCommand<R> implements
                                                                                    IApiCommand {

    public AbstractHystrixApiCommand(final String commandGroupKey, final String commandNameKey) {
        // API commands are sepmaphore by default unless over-ridden by
        // configuration
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey(commandGroupKey))
                    .andCommandKey(HystrixCommandKey.Factory.asKey(commandNameKey))
                    .andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
                                                                          .withExecutionTimeoutEnabled(false)
                                                                          .withExecutionIsolationStrategy(ExecutionIsolationStrategy.SEMAPHORE)));
    }

    @Override
    protected final R run() {
        // Default implementation catches and translates ClientException to
        // HystrixBadRequestException
        try {
            return runCommand();
        } catch (final BadRequestException e) {

            throw createHystrixBadRequestException("API command failed for "
                            + getCommandKey().name(), e);
        }
    }

    /**
     * Implementations of this should execute the API invocation.
     * <P>
     * They should not catch {@link BadRequestException} unless custom logic is
     * needed in the command.
     *
     * @return
     */
    protected abstract R runCommand();

}
