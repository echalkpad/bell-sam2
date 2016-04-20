package com.vennetics.bell.sam.adapters.ldap;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class SpringPooledLdapContextSourceTest {

    private static final String BASE = " o=mobility,c=ca";

    private static final String USER_DN = "uid=cbl-MTMSuser,ou=serviceplatform,c=ca";

    private static final String PASSWORD = "password";

    private static final String URLS = "ldap://10.240.8.138:389,ldap://10.240.8.139:389";

    private static final String CONNECT_TIMEOUT = "ldapConnectTimeout";

    private static final String REQUEST_LOGGING_ENABLED = "requestLoggingEnabled";

    private SpringPooledLdapContextSource testClass;

    @Before
    public void init() {
        testClass = new SpringPooledLdapContextSource(BASE,
                                                      USER_DN,
                                                      PASSWORD,
                                                      URLS,
                                                      CONNECT_TIMEOUT,
                                                      REQUEST_LOGGING_ENABLED);

    }

    @Test
    public void shouldSetCorrectValues() {

        assertEquals(BASE, testClass.getBaseLdapPathAsString());
        assertEquals(PASSWORD, testClass.getPassword());
    }

    @Test
    public void testWithBlankUsername() {
        testClass = new SpringPooledLdapContextSource(BASE,
                                                      "",
                                                      PASSWORD,
                                                      URLS,
                                                      CONNECT_TIMEOUT,
                                                      REQUEST_LOGGING_ENABLED);

        assertEquals(BASE, testClass.getBaseLdapPathAsString());
        assertEquals(PASSWORD, testClass.getPassword());
    }

    @Test
    public void testWithBlankPassword() {
        testClass = new SpringPooledLdapContextSource(BASE,
                                                      USER_DN,
                                                      "",
                                                      URLS,
                                                      CONNECT_TIMEOUT,
                                                      REQUEST_LOGGING_ENABLED);

        assertEquals(BASE, testClass.getBaseLdapPathAsString());
        assertEquals("", testClass.getPassword());
    }

    @Test
    public void testWithRequestLoggingEnabled() {
        testClass = new SpringPooledLdapContextSource(BASE,
                                                      USER_DN,
                                                      "",
                                                      URLS,
                                                      CONNECT_TIMEOUT,
                                                      "true");

        assertEquals(BASE, testClass.getBaseLdapPathAsString());
        assertEquals("", testClass.getPassword());
    }

    @Test
    public void testWithSingleUrl() {
        testClass = new SpringPooledLdapContextSource(BASE,
                                                      USER_DN,
                                                      "",
                                                      "ldap://10.240.8.138:389",
                                                      CONNECT_TIMEOUT,
                                                      REQUEST_LOGGING_ENABLED);

        assertEquals(BASE, testClass.getBaseLdapPathAsString());
        assertEquals("", testClass.getPassword());
    }

}
