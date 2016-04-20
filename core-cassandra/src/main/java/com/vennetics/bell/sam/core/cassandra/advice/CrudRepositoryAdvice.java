package com.vennetics.bell.sam.core.cassandra.advice;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import com.vennetics.bell.sam.core.cassandra.commands.ProceedInHystrixCommand;

/**
 * Advice that wraps calls with Hystrix Commands.
 */
@Aspect
@Component
public class CrudRepositoryAdvice {

    /**
     * Executed for any method in a CrudRepository
     */
    @Pointcut("execution(* org.springframework.data.repository.CrudRepository.*(..))")
    public void inCrudRepo() {
    }

    @Around("inCrudRepo()")
    public Object proceedInHystrixCommand(final ProceedingJoinPoint pjp) {

        return new ProceedInHystrixCommand(pjp).execute();

    }

}
