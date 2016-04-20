package com.vennetics.bell.sam.adapters.ldap.exception;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

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

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { LibLdapErrorTestConfig.class })
public class LibLdapErrorTypesTest {

    @Autowired
    private IErrorDictionary errorDictionary;

    @Autowired
    @Qualifier(BellSubscriberErrorAdapter.SERVICE_NAME)
    private IErrorAdapter errorAdapter;

    @Autowired
    private IExceptionTypeMapper exceptionTypeMapper;

    @Test
    public void shouldMapAllErrorTypes() {

        for (final LibLdapErrorTypes errorType : LibLdapErrorTypes.values()) {

            assertNotNull(errorDictionary.getError(errorType));

        }
    }

    @Test
    public void shouldProvisionLdapNetworkException() {
        final LdapNetworkException exception = new LdapNetworkException(new RuntimeException("Message"));
        final MappedError mappedError = exceptionTypeMapper.mapException(exception);

        assertEquals(exception.getErrorMessageType().toString(), mappedError.getErrorCode());
        assertTrue(Arrays.asList(mappedError.getMessageVariables()).isEmpty());
        assertEquals("SVC0020", mappedError.getCode());

        assertNotNull(errorAdapter.adapt(mappedError));

    }

    @Test
    public void shouldProvisionLdapRequestException() {
        final LdapRequestException exception = new LdapRequestException(new RuntimeException("Message"));
        final MappedError mappedError = exceptionTypeMapper.mapException(exception);

        assertEquals(exception.getErrorMessageType().toString(), mappedError.getErrorCode());
        assertTrue(Arrays.asList(mappedError.getMessageVariables()).isEmpty());
        assertEquals("SVC0030", mappedError.getCode());

        assertNotNull(errorAdapter.adapt(mappedError));

    }

}
