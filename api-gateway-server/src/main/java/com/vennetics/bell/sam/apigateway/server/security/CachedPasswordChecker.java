package com.vennetics.bell.sam.apigateway.server.security;

import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

/**
 * Wraps checks made against the PasswordEncoder. Primarily here to speed up
 * Bcrypt checks by maintaining a short-lived internal cache. For APIs we don't
 * want the BCrypt "Feature" that makes it slow. But we do want the secure
 * encoding it offers.
 * <P>
 * Keeping this primitive using google-cache. In future we'll probably move to
 * HazelCast.
 */
public class CachedPasswordChecker implements ICachedPasswordChecker {

    private static final Logger logger = LoggerFactory.getLogger(CachedPasswordChecker.class);

    private static final int DEFAULT_CACHE_MAX_SIZE = 1000;

    private static final int DEFAULT_CACHE_TIMEOUT = 5;

    private Cache<CharSequence, String> cache;

    private final PasswordEncoder passwordEncoder;

    public CachedPasswordChecker(final PasswordEncoder passwordEncoder) {
        super();
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    public void init() {
        cache = CacheBuilder.newBuilder()
                            .maximumSize(DEFAULT_CACHE_MAX_SIZE)
                            .expireAfterWrite(DEFAULT_CACHE_TIMEOUT, TimeUnit.MINUTES)
                            .build();
    }

    @PreDestroy
    public void destroy() {
        cache.cleanUp();
    }

    @Override
    public boolean matches(final CharSequence rawPassword, final String encodedPassword) {

        final String cachedEncodedPassword = cache.getIfPresent(rawPassword);

        if (!StringUtils.isBlank(cachedEncodedPassword)
                        && cachedEncodedPassword.equals(encodedPassword)) {
            // Cache hit and success

            logger.debug("Cache hit on password check");
            return true;
        }

        logger.debug("Cache miss on password check");

        final boolean result = passwordEncoder.matches(rawPassword, encodedPassword);

        if (result) {
            // Cache successful check
            cache.put(rawPassword, encodedPassword);
        } else {
            cache.invalidate(rawPassword);
        }

        logger.debug("Password check result: {}", result);

        return result;
    }
}
