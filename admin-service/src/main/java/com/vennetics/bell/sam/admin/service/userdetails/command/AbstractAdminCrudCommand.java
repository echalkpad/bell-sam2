package com.vennetics.bell.sam.admin.service.userdetails.command;

import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.vennetics.bell.sam.core.commands.hystrix.AbstractHystrixCommand;

/**
 * Common logic for accessing admin service crud repos using hystrix.
 *
 * @param <R>
 */
public abstract class AbstractAdminCrudCommand<R> extends AbstractHystrixCommand<R> {

    public AbstractAdminCrudCommand(final String commandKey) {
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("AdminServiceCrud"))
                    .andCommandKey(HystrixCommandKey.Factory.asKey(commandKey)));

   }

}
