package com.vennetics.bell.sam.apigateway.server.security;

/**
 * Caches password checking for performance.
 */
public interface ICachedPasswordChecker {

    boolean matches(CharSequence rawPassword, String encodedPassword);

}
