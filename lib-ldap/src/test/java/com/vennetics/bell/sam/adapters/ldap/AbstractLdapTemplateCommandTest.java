package com.vennetics.bell.sam.adapters.ldap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.dao.QueryTimeoutException;
import org.springframework.ldap.CommunicationException;
import org.springframework.ldap.InvalidNameException;
import org.springframework.ldap.NameNotFoundException;
import org.springframework.ldap.core.LdapOperations;

import com.netflix.hystrix.exception.HystrixRuntimeException;
import com.vennetics.bell.sam.adapters.ldap.exception.LdapNetworkException;
import com.vennetics.bell.sam.adapters.ldap.exception.LdapRequestException;
import com.vennetics.bell.sam.core.exception.PlatformFailedException;

@RunWith(MockitoJUnitRunner.class)
public class AbstractLdapTemplateCommandTest {

    private static final String COMMAND_NAME_KEY = "commandNameKey";

    private static final String COMMAND_GROUP_KEY = "commandGroupKey";

    private static final String RESULT_TO_RETURN = "RESULT_TO_RETURN";

    @Mock
    private LdapOperations ldapTemplate;

    private TestLdapCommand testClass;

    @Before
    public void init() {
        testClass = new TestLdapCommand(COMMAND_GROUP_KEY,
                                        COMMAND_NAME_KEY,
                                        ldapTemplate,
                                        RESULT_TO_RETURN);
    }

    @Test
    public void shouldSetHystrixProperties() {

        assertEquals(COMMAND_GROUP_KEY, testClass.getCommandGroup().name());
        assertEquals(COMMAND_NAME_KEY, testClass.getCommandKey().name());

    }

    @Test
    public void shouldReturnResultWhenNoErrors() {

        assertEquals(RESULT_TO_RETURN, testClass.execute());

    }

    @Test
    public void shouldPropogateLdapRequestErrors() {

        when(ldapTemplate.lookup(RESULT_TO_RETURN)).thenThrow(new LdapRequestException(null));

        try {
            testClass.execute();
            fail("Expected HystrixRuntimeException");
        } catch (final HystrixRuntimeException e) {
            assertTrue(e.getCause() instanceof LdapRequestException);
        }

    }

    @Test
    public void shouldMapNameNotFoundExceptionToEmptyResult() {

        when(ldapTemplate.lookup(RESULT_TO_RETURN)).thenThrow(new NameNotFoundException(""));

        assertEquals(RESULT_TO_RETURN, testClass.execute());

    }

    @Test
    public void shouldMapCommsExceptionToLdapNetworkError() {

        when(ldapTemplate.lookup(RESULT_TO_RETURN)).thenThrow(new CommunicationException(null));

        try {
            testClass.execute();
            fail("Expected HystrixRuntimeException");
        } catch (final HystrixRuntimeException e) {
            assertTrue(e.getCause() instanceof LdapNetworkException);
            assertTrue(e.getCause().getCause() instanceof CommunicationException);
        }

    }

    @Test
    public void shouldMapNamingExceptionToPlatformFailed() {

        when(ldapTemplate.lookup(RESULT_TO_RETURN)).thenThrow(new InvalidNameException(null));

        try {
            testClass.execute();
            fail("Expected HystrixRuntimeException");
        } catch (final HystrixRuntimeException e) {
            assertTrue(e.getCause() instanceof PlatformFailedException);
            assertTrue(e.getCause().getCause() instanceof InvalidNameException);
        }

    }

    @Test
    public void shouldMapDataAccessExceptionToLdapNetworkError() {

        when(ldapTemplate.lookup(RESULT_TO_RETURN)).thenThrow(new QueryTimeoutException(""));

        try {
            testClass.execute();
            fail("Expected HystrixRuntimeException");
        } catch (final HystrixRuntimeException e) {
            assertTrue(e.getCause() instanceof LdapNetworkException);
            assertTrue("Expected cause to be QueryTimeoutException but was"
                            + e.getCause().getCause(),
                       e.getCause().getCause() instanceof QueryTimeoutException);
        }

    }

    @Test
    public void shouldMapUnclassifiedExceptionToPlatformFailed() {

        when(ldapTemplate.lookup(RESULT_TO_RETURN)).thenThrow(new RuntimeException(""));

        try {
            testClass.execute();
            fail("Expected HystrixRuntimeException");
        } catch (final HystrixRuntimeException e) {
            assertTrue(e.getCause() instanceof PlatformFailedException);
            assertTrue(e.getCause().getCause() instanceof RuntimeException);
        }

    }

    private static class TestLdapCommand extends AbstractLdapTemplateCommand<String> {

        private final String resultToReturn;

        public TestLdapCommand(final String commandGroupKey,
                               final String commandNameKey,
                               final LdapOperations ldapTemplate,
                               final String resultToReturn) {
            super(commandGroupKey, commandNameKey, ldapTemplate);
            this.resultToReturn = resultToReturn;
        }

        @Override
        protected String run() throws Exception {

            try {
                // Simple call to allow mocked behaviour
                getLdapTemplate().lookup(RESULT_TO_RETURN);
            } catch (final RuntimeException e) {
                //
                handleLdapRuntimeException(e, "");
            }

            return resultToReturn;
        }

    }
}
