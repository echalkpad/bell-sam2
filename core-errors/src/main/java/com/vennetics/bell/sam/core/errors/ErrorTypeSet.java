package com.vennetics.bell.sam.core.errors;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to annotate an enum that contains a set of error/exception types, defining the name of the file containing
 * the definitions of the errors.
 */

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ErrorTypeSet {

    /**
     * Defines the name of the file containing the associated error set.
     */
    String value();
}
