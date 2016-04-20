package com.vennetics.microservices.common.core.security.jwt;

import java.io.IOException;
import java.util.Date;
import java.util.UUID;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vennetics.bell.sam.core.exception.PlatformFailedException;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;

public class JwtTokenBuilder<T> implements IJwtTokenBuilder<T> {

    // Replace with config
    private static final String KEY = "ubilbewvrivbrwiebvliwurb";

    private final ObjectMapper objectMapper;

    public JwtTokenBuilder(final ObjectMapper objectMapper) {
        super();
        this.objectMapper = objectMapper;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.vennetics.microservices.common.core.security.jwt.IJwtTokenBuilder#
     * createJwtTokenFromSubject(T)
     */
    @Override
    public String createJwtTokenFromSubject(final T subject) {
        String subjectJson;
        try {
            subjectJson = objectMapper.writeValueAsString(subject);
        } catch (final JsonProcessingException e) {
            throw new PlatformFailedException("JWT encoding failed for subject: " + subject, e);
        }

        return Jwts.builder()
                   .setSubject(subjectJson)
                   .setIssuedAt(new Date(System.currentTimeMillis()))
                   .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60))
                   .setId(UUID.randomUUID().toString())
                   .signWith(SignatureAlgorithm.HS512, KEY)
                   .compact();
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.vennetics.microservices.common.core.security.jwt.IJwtTokenBuilder#
     * getSubjectFromJwtToken(java.lang.String, java.lang.Class)
     */
    @Override
    public T getSubjectFromJwtToken(final String token, final Class<T> classType) {

        try {
            return objectMapper.readValue(Jwts.parser()
                                              .setSigningKey(KEY)
                                              .parseClaimsJws(token)
                                              .getBody()
                                              .getSubject(),
                                          classType);
        } catch (ExpiredJwtException | UnsupportedJwtException | MalformedJwtException
                        | SignatureException | IllegalArgumentException | IOException e) {
            throw new PlatformFailedException("JWT dencoding failed for token: " + token, e);
        }
    }
}
