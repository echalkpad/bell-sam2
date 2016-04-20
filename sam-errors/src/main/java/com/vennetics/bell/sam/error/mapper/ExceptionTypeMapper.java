package com.vennetics.bell.sam.error.mapper;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.MissingServletRequestParameterException;

import com.netflix.hystrix.exception.HystrixBadRequestException;
import com.netflix.hystrix.exception.HystrixRuntimeException;
import com.vennetics.bell.sam.core.errors.IErrorDictionary;
import com.vennetics.bell.sam.core.errors.model.ErrorDescription;
import com.vennetics.bell.sam.core.exception.AbstractFormattedException;
import com.vennetics.bell.sam.core.exception.ApiException;
import com.vennetics.bell.sam.core.exception.BadRequestException;
import com.vennetics.bell.sam.core.exception.PolicyException;
import com.vennetics.bell.sam.error.RestErrorMessageType;
import com.vennetics.bell.sam.error.exceptions.InvalidInputValueException;

/**
 * Default implementation of {@link IExceptionTypeMapper}, adapted from a class
 * of the same name in J-BOX.
 * <p/>
 * Uses an {@link IErrorDictionary} to inform the mapping.
 */

@Service
public class ExceptionTypeMapper implements IExceptionTypeMapper {

    private static final Logger logger = LoggerFactory.getLogger(ExceptionTypeMapper.class);

    @Autowired(required = true)
    private IErrorDictionary errorDictionary;

    private final Map<Class<? extends Throwable>, ExceptionMapper> mappers = createMappers();

    @SuppressWarnings({ "squid:S1171", "serial" })
    private Map<Class<? extends Throwable>, ExceptionMapper> createMappers() {
        // Add exception mappers for the different exception classes in the
        // order in which they are to be
        // checked - hence the the use of a LinkedHashMap.
        return new LinkedHashMap<Class<? extends Throwable>, ExceptionMapper>() {
            {
                put(HystrixRuntimeException.class,
                    e -> mapHystrixRuntimeException((HystrixRuntimeException) e));
                put(HystrixBadRequestException.class, e -> mapHystrixBadRequestException(e));
                put(ApiException.class, e -> mapApiException((ApiException) e));
                put(AuthenticationException.class,
                    e -> mapAuthenticationException((AuthenticationException) e));
                put(IllegalArgumentException.class,
                    e -> mapIllegalArgumentException((IllegalArgumentException) e));
                put(MissingServletRequestParameterException.class,
                    e -> mapMissingServletRequestParameterException((MissingServletRequestParameterException) e));
                put(ExecutionException.class, e -> mapExecutionException((ExecutionException) e));
            }
        };
    }

    @Override
    public MappedError mapException(final Throwable e) {
        logger.debug(">>> mapException e:{}/{}", e.getClass().getSimpleName(), e.getMessage(), e);
        final ExceptionMapper mapper = findMapperForException(e);
        if (mapper != null) {
            return mapper.map(e);
        }

        logger.error("An unclassified Exception occurred during API invocation", e);
        return mapInternalError(e);
    }

    @Override
    public MappedError mapException(final Throwable e, final HttpServletRequest request) {
        return mapException(e).withPath(request.getPathInfo());
    }

    private ExceptionMapper findMapperForException(final Throwable e) {
        for (final Map.Entry<Class<? extends Throwable>, ExceptionMapper> entry : mappers.entrySet()) {
            if (entry.getKey().isAssignableFrom(e.getClass())) {
                return entry.getValue();
            }
        }
        return null;
    }

    private MappedError mapHystrixBadRequestException(final Throwable e) {
        if (e.getCause() != null) {
            return mapException(e.getCause());
        }
        return mapInternalError(e);
    }

    private MappedError mapAuthenticationException(final AuthenticationException e) {

        logger.debug("Translating Spring authentication exception: ", e.getMessage());

        if (e.getCause() instanceof ApiException) {
            return mapApiException((ApiException) e.getCause());
        }

        final ErrorDescription error = errorDictionary.getError(RestErrorMessageType.HTTP_401_UNAUTHORIZED);

        return new MappedError(error);
    }

    private MappedError mapApiException(final ApiException e) {

        final ErrorDescription errorDescription = errorDictionary.getError(e.getErrorMessageType());

        MappedError mappedError;

        if (e instanceof BadRequestException && !((BadRequestException) e).isSpecificException()) {
            mappedError = mapNonSpecificException((BadRequestException) e, errorDescription);
        } else {
            mappedError = new MappedError(errorDescription,
                                          e instanceof PolicyException,
                                          e.getMessageArguments());
        }

        return mappedError.withTimestamp(e.getTimestamp());
    }

    private static MappedError mapNonSpecificException(final BadRequestException e,
                                                       final ErrorDescription errorDescription) {

        return new MappedError(errorDescriptionWithAlternativeMessageId(errorDescription,
                                                                        e.getNonSpecificMessageId()),
                               e instanceof PolicyException,
                               addOriginalMessageIdToArguments(e, errorDescription));
    }

    private static ErrorDescription errorDescriptionWithAlternativeMessageId(final ErrorDescription inputDescription,
                                                                             final String newMessageId) {
        return new ErrorDescription(newMessageId,
                                    inputDescription.getType(),
                                    inputDescription.getStatusCode(),
                                    inputDescription.getMessage());
    }

    private static String[] addOriginalMessageIdToArguments(final BadRequestException e,
                                                            final ErrorDescription errorDescription) {
        final String[] originalArguments = e.getMessageArguments();
        final String[] arguments = new String[originalArguments.length + 1];
        arguments[0] = errorDescription.getCode();
        System.arraycopy(originalArguments, 0, arguments, 1, originalArguments.length);
        return arguments;
    }

    private MappedError mapHystrixRuntimeException(final HystrixRuntimeException exception) {

        final HystrixRuntimeException.FailureType failureType = exception.getFailureType();
        logger.warn("Hystrix failure type = {}, command class = {}",
                 failureType,
                 exception.getImplementingClass().getName());

        // Command exception means an underlying cause. All other errors mean
        // hystrix prevented the request due to a condition being met e.g.
        // circuit open.
        if (failureType == HystrixRuntimeException.FailureType.COMMAND_EXCEPTION) {
            return mapException(exception.getCause());
        }

        final ErrorDescription error = errorDictionary.getError(RestErrorMessageType.RESOURCE_UNAVAILABLE_COMMAND_FAILED);

        return new MappedError(error, exception.getMessage());
    }

    private MappedError mapExecutionException(final ExecutionException exception) {

        // Command exception means an underlying cause. All other errors mean
        // hystrix prevented the request due to a condition being met e.g.
        // circuit open.
        if (exception.getCause() instanceof AbstractFormattedException) {
            return mapException(exception.getCause());
        }

        final ErrorDescription error = errorDictionary.getError(RestErrorMessageType.RESOURCE_INTERNAL_ERROR);

        return new MappedError(error, exception.getMessage());
    }

    private MappedError mapIllegalArgumentException(final IllegalArgumentException e) {

        final ErrorDescription error = errorDictionary.getError(RestErrorMessageType.RESOURCE_BAD_REQUEST_INVALID_ARGUMENT);

        return new MappedError(error, e.getMessage());
    }

    private MappedError mapMissingServletRequestParameterException(final MissingServletRequestParameterException e) {

        // Map missing servlet request param to invalid
        return mapException(new InvalidInputValueException(e.getParameterName()));
    }

    private MappedError mapInternalError(final Throwable e) {

        final ErrorDescription error = errorDictionary.getError(RestErrorMessageType.RESOURCE_INTERNAL_ERROR);
        return new MappedError(error, e.getMessage());
    }

    @FunctionalInterface
    private interface ExceptionMapper {
        MappedError map(final Throwable e);
    }
}
