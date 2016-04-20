package com.vennetics.microservices.common.core.security.jwt;

/**
 * Spring wireable interface for transforming objects into a JWT token and back.
 * The token will be converted to the subject of a JWT via JSON object mapping.
 * Therefore ensure the class passed to this interface has been jackson
 * annotated or is a JavaBean.
 *
 * @param <T>
 *            the class type of the token being transformed.
 */
public interface IJwtTokenBuilder<T> {

    String createJwtTokenFromSubject(T subject);

    T getSubjectFromJwtToken(String token, Class<T> classType);

}
