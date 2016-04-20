package com.vennetics.microservices.common.core.gateway.adapter.command;

import org.springframework.web.client.HttpClientErrorException;

import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.vennetics.bell.sam.core.commands.hystrix.AbstractHystrixCommand;
import com.vennetics.bell.sam.core.commands.hystrix.IApiCommand;

/**
 * Provides functionality common to all hystrix used by the API Gateway to forward requests to internal services.
 * Copied from JBox.
 * <p/>
 * @param <R>
 *      the return type of the command
 */
public abstract class AbstractHystrixGatewayCommand<R> extends AbstractHystrixCommand<R> implements
                                                                                    IApiCommand {

    public AbstractHystrixGatewayCommand(final String commandGroupKey, final String commandNameKey) {
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey(commandGroupKey))
                    .andCommandKey(HystrixCommandKey.Factory.asKey(commandNameKey)));
    }

    @Override
    protected final R run() {
        // Default implementation catches and translates HttpClientErrorException to
        // HystrixBadRequestException
        try {
            return runCommand();
        } catch (final HttpClientErrorException e) {

            throw createHystrixBadRequestException("Gateway command failed for " + getCommandKey().name(), e);
        }
    }

    /**
     * Implementations of this should execute the command.
     * <P>
     * They should not catch {@link HttpClientErrorException} unless custom logic is
     * needed in the command.
     *
     * @return
     */
    protected abstract R runCommand();

}
