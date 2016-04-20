package com.vennetics.bell.sam.adapters.ldap;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isNotNull;
import static org.mockito.Mockito.when;
import static org.springframework.ldap.query.LdapQueryBuilder.query;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapOperations;
import org.springframework.ldap.query.LdapQuery;
import org.springframework.ldap.query.SearchScope;

import com.netflix.hystrix.exception.HystrixRuntimeException;
import com.vennetics.bell.sam.core.exception.PlatformFailedException;

@RunWith(MockitoJUnitRunner.class)
public class AbstractLdapSearchCommandTest {

    private static final List<String> EXPECTED_RESULT = Arrays.asList("ExpectedResult");

    private static final String COMMAND_NAME_KEY = "commandNameKey";

    private static final String COMMAND_GROUP_KEY = "commandGroupKey";

    @Mock
    private LdapOperations ldapTemplate;

    @Mock
    private AttributesMapper<String> attributesMapper;

    private TestLdapSearchCommand testClass;

    @Before
    public void init() {

        testClass = new TestLdapSearchCommand(COMMAND_GROUP_KEY,
                                              COMMAND_NAME_KEY,
                                              ldapTemplate,
                                              query().base("o=mobility,c=ca")
                                                     .searchScope(SearchScope.OBJECT)
                                                     .where("attribute")
                                                     .is("value"),
                                              attributesMapper);
    }

    @Test
    public void shouldReturnLdapResult() {

        when(ldapTemplate.search(isNotNull(LdapQuery.class),
                                 eq(attributesMapper))).thenReturn(EXPECTED_RESULT);

        assertTrue(testClass.execute().contains(EXPECTED_RESULT.get(0)));
    }

    @Test
    public void shouldThrowPlatformFailedForRuntimeException() {

        when(ldapTemplate.search(isNotNull(LdapQuery.class),
                                 eq(attributesMapper))).thenThrow(new RuntimeException());

        try {
            testClass.execute();
            fail("Expected HystrixRuntimeException");
        } catch (final HystrixRuntimeException e) {
            assertTrue(e.getCause() instanceof PlatformFailedException);
        }
    }

    @Test
    public void shouldMapNullToEmptyResult() {

        when(ldapTemplate.search(isNotNull(LdapQuery.class),
                                 eq(attributesMapper))).thenReturn(null);

        testClass.execute();
        assertTrue(testClass.getResult().isEmpty());
    }

    @Test
    public void shouldHandleEmptyResult() {

        when(ldapTemplate.search(isNotNull(LdapQuery.class),
                                 eq(attributesMapper))).thenReturn(Arrays.asList(new String[] {}));

        testClass.execute();
        assertTrue(testClass.getResult().isEmpty());
    }

    private static class TestLdapSearchCommand extends AbstractLdapSearchCommand<String> {

        private List<String> result;

        public TestLdapSearchCommand(final String commandGroupKey,
                                     final String commandNameKey,
                                     final LdapOperations ldapTemplate,
                                     final LdapQuery ldapQuery,
                                     final AttributesMapper<String> attributesMapper) {
            super(commandGroupKey, commandNameKey, ldapTemplate, ldapQuery, attributesMapper);
        }

        @Override
        protected List<String> run() throws Exception {

            result = super.search();

            return EXPECTED_RESULT;
        }

        public List<String> getResult() {
            return result;
        }
    }
}
