package com.vennetics.bell.sam.subscriber.thirdparty.service.exceptions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.vennetics.bell.sam.core.errors.IErrorDictionary;
import com.vennetics.bell.sam.error.adapters.BellSubscriberErrorAdapter;
import com.vennetics.bell.sam.error.adapters.IErrorAdapter;
import com.vennetics.bell.sam.error.mapper.IExceptionTypeMapper;
import com.vennetics.bell.sam.error.mapper.MappedError;
import com.vennetics.bell.sam.sdm.adapter.api.SdmQueryFilter;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { SubscriberThirdPartySdmErrorTestConfig.class })
public class SubscriberThirdPartySdmErrorTypesTest {

    @Autowired
    private IErrorDictionary errorDictionary;

    @Autowired
    @Qualifier(BellSubscriberErrorAdapter.SERVICE_NAME)
    private IErrorAdapter errorAdapter;

    @Autowired
    private IExceptionTypeMapper exceptionTypeMapper;

    @Test
    public void shouldProvisionAllErrorDefinitions() {

        for (final SubscriberThirdPartySdmErrorTypes errorType : SubscriberThirdPartySdmErrorTypes.values()) {

            assertNotNull(errorDictionary.getError(errorType));

        }
    }

    @Test
    public void shouldProvisionInvalidFilterTypeException() {
        final InvalidFilterTypeException exception = new InvalidFilterTypeException("invalidFilter");
        final MappedError mappedError = exceptionTypeMapper.mapException(exception);

        assertEquals(exception.getErrorMessageType().toString(), mappedError.getErrorCode());
        assertTrue(Arrays.asList(mappedError.getMessageVariables()).contains("invalidFilter"));
        assertEquals("POL0080", mappedError.getCode());

        assertNotNull(errorAdapter.adapt(mappedError));

    }

    @Test
    public void shouldProvisionInvalidImsiException() {
        final InvalidImsiException exception = new InvalidImsiException("invalidFilter");
        final MappedError mappedError = exceptionTypeMapper.mapException(exception);

        assertEquals(exception.getErrorMessageType().toString(), mappedError.getErrorCode());
        assertTrue(Arrays.asList(mappedError.getMessageVariables()).contains("invalidFilter"));
        assertEquals("POL0130", mappedError.getCode());

        assertNotNull(errorAdapter.adapt(mappedError));

    }

    @Test
    public void shouldProvisionInvalidMdnException() {
        final InvalidMdnException exception = new InvalidMdnException("invalidFilter");
        final MappedError mappedError = exceptionTypeMapper.mapException(exception);

        assertEquals(exception.getErrorMessageType().toString(), mappedError.getErrorCode());
        assertTrue(Arrays.asList(mappedError.getMessageVariables()).contains("invalidFilter"));
        assertEquals("POL0070", mappedError.getCode());

        assertNotNull(errorAdapter.adapt(mappedError));

    }

    @Test
    public void shouldProvisionNonUniqueSubscriberException() {
        final Set<SdmQueryFilter> sdmQueryFilters = new HashSet<>(Arrays.asList(new SdmQueryFilter("key",
                                                                                                   "value")));

        final SubscriberNotUniqueException exception = new SubscriberNotUniqueException(sdmQueryFilters);
        final MappedError mappedError = exceptionTypeMapper.mapException(exception);

        assertEquals(exception.getErrorMessageType().toString(), mappedError.getErrorCode());
        assertTrue(Arrays.asList(mappedError.getMessageVariables()).contains("key"));
        assertTrue(Arrays.asList(mappedError.getMessageVariables()).contains("value"));
        assertEquals("SVC0060", mappedError.getCode());

        assertNotNull(errorAdapter.adapt(mappedError));

    }

    @Test
    public void shouldProvisionSubscriberNotFoundException() {
        final Set<SdmQueryFilter> sdmQueryFilters = new HashSet<>(Arrays.asList(new SdmQueryFilter("key",
                                                                                                   "value")));
        final SubscriberNotFoundException exception = new SubscriberNotFoundException(sdmQueryFilters);
        final MappedError mappedError = exceptionTypeMapper.mapException(exception);

        assertEquals(exception.getErrorMessageType().toString(), mappedError.getErrorCode());
        assertTrue(Arrays.asList(mappedError.getMessageVariables()).contains("key"));
        assertTrue(Arrays.asList(mappedError.getMessageVariables()).contains("value"));
        assertEquals("SVC0010", mappedError.getCode());

        assertNotNull(errorAdapter.adapt(mappedError));

    }

    @Test
    public void shouldProvisionWildcardForbiddenException() {
        final WildcardForbiddenException exception = new WildcardForbiddenException("key");
        final MappedError mappedError = exceptionTypeMapper.mapException(exception);

        assertEquals(exception.getErrorMessageType().toString(), mappedError.getErrorCode());
        assertTrue(Arrays.asList(mappedError.getMessageVariables()).contains("key"));
        assertEquals("POL0120", mappedError.getCode());

        assertNotNull(errorAdapter.adapt(mappedError));

    }

}
