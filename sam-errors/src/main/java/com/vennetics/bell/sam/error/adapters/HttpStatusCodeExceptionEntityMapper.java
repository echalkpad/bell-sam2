package com.vennetics.bell.sam.error.adapters;

import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpStatusCodeException;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Functionality that identifies the underlying HttpStatusCodeException cause of
 * a thrown exception and maps to one of a list of supplied entities. Can be
 * used anywhere we're capturing exceptions on the client side and need to pull
 * the cause.
 * <P>
 * This class exists due to some of the protocols meaning more than one type can
 * be returned on the same call and the status code cannot be used to interpret
 * the expected type. e.g. ServiceException and PolicyException.
 *
 */
public class HttpStatusCodeExceptionEntityMapper {

    private static final Logger logger = LoggerFactory.getLogger(HttpStatusCodeExceptionEntityMapper.class);

    // Pattern used to identify the name of the root element in a json document.
    // Looks for '{"ROOT_NAME"' at the start of the document, but allows any
    // whitespace before the '{' and
    // between the '{' and the '"'.
    private static final Pattern ROOT_ELEMENT_PATTERN = Pattern.compile("\\s*\\{\\s*\"([^\"]*)\".*");

    private final ObjectMapper jsonObjectMapper;

    private final List<Class<?>> entityClasses;

    public HttpStatusCodeExceptionEntityMapper(final ObjectMapper jsonObjectMapper,
                                               final List<Class<?>> exceptionClasses) {
        super();
        this.jsonObjectMapper = jsonObjectMapper;
        entityClasses = exceptionClasses;
    }

    @SuppressWarnings("rawtypes")
    public ResponseEntity captureEntityFromException(final Exception ex) {
        final HttpStatusCodeException scException = findHttpStatusCodeException(ex);
        if (scException != null) {
            final HttpStatus statusCode = scException.getStatusCode();
            final Object exceptionResponse = parseBody(scException.getResponseBodyAsString());
            if (exceptionResponse != null) {
                return new ResponseEntity<>(exceptionResponse, statusCode);
            }
        }
        return null;
    }

    /**
     * Parse the response into
     *
     * @param body
     * @return the parsed response.
     */
    protected Object parseBody(final String body) {

        final Class<?> responseClass = identifyEntityClass(body);
        if (responseClass != null) {
            try {
                return jsonObjectMapper.readValue(body, responseClass);
            } catch (final IOException e) {
                logger.warn("Failed to parse exception body. Expected class: {}. Body: {}",
                            responseClass,
                            body,
                            e);
            }
        }
        return null;
    }

    // Protected only to avoid PMD complaints about it being unused.
    protected static HttpStatusCodeException findHttpStatusCodeException(final Throwable exception) {
        logger.debug("findHttpStatusCodeException {}/{}", exception, exception.getMessage());
        Throwable nextToCheck = exception;
        while (nextToCheck != null) {
            logger.debug("nextToCheck {}/{}", nextToCheck, nextToCheck.getMessage());
            if (nextToCheck instanceof HttpStatusCodeException) {
                return (HttpStatusCodeException) nextToCheck;
            }
            nextToCheck = nextToCheck.getCause();
        }
        return null;
    }

    protected Class<?> identifyEntityClass(final String body) {
        final String rootElementName = extractRootElementNameFromJson(body);

        return entityClasses.stream()
                            .filter(clazz -> clazz.getSimpleName()
                                                  .equalsIgnoreCase(rootElementName))
                            .findFirst()
                            .orElse(null);
    }

    private static String extractRootElementNameFromJson(final String body) {
        logger.debug(">>> extractRootElementNameFromJson {}", body);
        final Matcher matcher = ROOT_ELEMENT_PATTERN.matcher(body);
        if (matcher.matches()) {
            logger.warn("rootElement {}", matcher.group(1));
            return matcher.group(1);
        }
        return null;
    }
}
