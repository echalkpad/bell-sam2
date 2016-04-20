package com.vennetics.bell.sam.subscriber.thirdparty.gateway.adapter;

import org.apache.cxf.interceptor.Fault;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;

import com.vennetics.bell.sam.error.adapters.HttpStatusCodeExceptionEntityMapper;
import com.vennetics.bell.sam.error.adapters.IErrorAdapter;
import com.vennetics.bell.sam.error.mapper.IExceptionTypeMapper;
import com.vennetics.bell.sam.error.mapper.MappedError;
import com.vennetics.microservices.common.core.soap.gateway.adapter.ISoapExceptionMapper;
import com.vennetics.microservices.common.core.soap.gateway.adapter.SoapExceptionMapper;

/**
 * Sub-X specific implementation of {@link ISoapExceptionMapper}. Extends the
 * default behaviour with logic to handle any response entity of type
 * {@link MappedError} and convert it to a ServiceException or PolicyException.
 * Defaults to standard behaviour if any other response POJOs are encountered
 * which will generally result in an internal error unless the exception was
 * thrown in-process.
 */
public class SubscriberThirdPartySoapExceptionMapper extends SoapExceptionMapper
                                                     implements ISoapExceptionMapper {

    private static final Logger logger = LoggerFactory.getLogger(SubscriberThirdPartySoapExceptionMapper.class);

    private final HttpStatusCodeExceptionEntityMapper exceptionToEntityMapper;

    public SubscriberThirdPartySoapExceptionMapper(final IExceptionTypeMapper exceptionMapper,
                                                   final IErrorAdapter errorAdapter,
                                                   final HttpStatusCodeExceptionEntityMapper exceptionToEntityMapper) {
        super(exceptionMapper, errorAdapter);
        this.exceptionToEntityMapper = exceptionToEntityMapper;
    }

    @Override
    public Fault mapExceptionToFault(final Throwable cause) {
        logger.debug(">>> mapExceptionToFault {}", cause.getClass());

        final Fault fault = mapClientErrorToFault(cause);

        if (fault != null) {
            return fault;
        }

        return mapToServiceErrorFault(cause);

    }

    private Fault mapToServiceErrorFault(final Throwable cause) {


        // Sub-X should always return a class of ServiceException or
        // PolicyException
        // If we cannot map the response sent by the Sub-X enabler wrap the
        // mapping in a ServiceException
        // i.e. default to SVC0001 with error code matching our internal

        final MappedError error = getExceptionMapper().mapException(cause);

        logger.debug("Handling mapping of non-client error: {}", error);

        return newFault(SubscriberThirdPartyExceptionUtils.newServiceException(com.vennetics.bell.sam.core.exception.ServiceException.NON_SPECIFIC_SERVICE_MESSAGE_ID,
                                                                               error.getMessageTemplate(),
                                                                               error.getMessageVariables()),
                        error.getStatusCode());
    }

    private Fault mapClientErrorToFault(final Throwable cause) {
        // Sub-X service commands will wrap any thrown HttpClientErrorException
        // as a bad request.
        // Therefore this should be in the nested cause of the exception.

        final HttpClientErrorException clientErrorException = getCauseIfHttpClientErrorException(cause);
        if (clientErrorException != null) {

            final Fault fault = faultFromHttpClientErrorException(clientErrorException);

            if (fault != null) {

                logger.debug("Mapped client error to fault: {}", fault);
                return fault;
            }
        }

        return null;
    }

    /**
     * Maps from a HttpClientErrorException to a SOAP fault.
     *
     * @param e
     *            the exception to handle
     * @return the soap fault
     */
    private Fault faultFromHttpClientErrorException(final HttpClientErrorException e) {


        @SuppressWarnings("rawtypes")
        final ResponseEntity entity = exceptionToEntityMapper.captureEntityFromException(e);

        final Object body = entity != null ? entity.getBody() : null;
        if (body instanceof MappedError) {

            logger.debug("Handling mapping from client error: {}", body);

            final MappedError error = (MappedError) entity.getBody();

            if (error.isPolicyError()) {

                return newFault(SubscriberThirdPartyExceptionUtils.newPolicyException(error.getCode(),
                                                                                      error.getMessageTemplate(),
                                                                                      error.getMessageVariables()),
                                error.getStatusCode());
            }

            return newFault(SubscriberThirdPartyExceptionUtils.newServiceException(error.getCode(),
                                                                                   error.getMessageTemplate(),
                                                                                   error.getMessageVariables()),
                            error.getStatusCode());
        }

        return null;

    }

}
