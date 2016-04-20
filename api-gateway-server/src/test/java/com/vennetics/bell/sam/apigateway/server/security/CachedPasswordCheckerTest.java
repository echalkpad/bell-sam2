package com.vennetics.bell.sam.apigateway.server.security;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.crypto.password.PasswordEncoder;

@RunWith(MockitoJUnitRunner.class)
public class CachedPasswordCheckerTest {

    private CachedPasswordChecker testClass;

    @Mock
    private PasswordEncoder passwordEncoder;

    private static final CharSequence PASSWORD = "PASSWORD";

    private static final String ENCODED_PASSWORD = "ENCODED_PASSWORD";

    @Before
    public void init() {

        testClass = new CachedPasswordChecker(passwordEncoder);

        testClass.init();
    }

    @After
    public void destroy() {
        testClass.destroy();
    }

    @Test
    public void shouldReturnTrueForCorrectEncoding() {

        when(passwordEncoder.matches(PASSWORD, ENCODED_PASSWORD)).thenReturn(true);

        assertThat(testClass.matches(PASSWORD, ENCODED_PASSWORD), equalTo(true));
    }

    @Test
    public void shouldReturnFalseForWrongEncoding() {

        when(passwordEncoder.matches(PASSWORD, ENCODED_PASSWORD)).thenReturn(false);

        assertThat(testClass.matches(PASSWORD, ENCODED_PASSWORD), equalTo(false));
    }

    @Test
    public void shouldHitCacheOnSecondCall() {

        when(passwordEncoder.matches(PASSWORD, ENCODED_PASSWORD)).thenReturn(true);

        assertThat(testClass.matches(PASSWORD, ENCODED_PASSWORD), equalTo(true));
        assertThat(testClass.matches(PASSWORD, ENCODED_PASSWORD), equalTo(true));

        verify(passwordEncoder, times(1)).matches(PASSWORD, ENCODED_PASSWORD);
    }

    @Test
    public void shouldReturnFalseWhenCacheDoesNotMatchSupplied() {

        when(passwordEncoder.matches(PASSWORD, ENCODED_PASSWORD)).thenReturn(true);
        when(passwordEncoder.matches(PASSWORD, "WrongEncoding")).thenReturn(false);

        assertThat(testClass.matches(PASSWORD, ENCODED_PASSWORD), equalTo(true));
        assertThat(testClass.matches(PASSWORD, "WrongEncoding"), equalTo(false));
    }

    @Test
    public void shouldNotCacheFailures() {

        when(passwordEncoder.matches(PASSWORD, ENCODED_PASSWORD)).thenReturn(false);

        assertThat(testClass.matches(PASSWORD, ENCODED_PASSWORD), equalTo(false));
        assertThat(testClass.matches(PASSWORD, ENCODED_PASSWORD), equalTo(false));

        verify(passwordEncoder, times(2)).matches(PASSWORD, ENCODED_PASSWORD);
    }

    @Test
    public void shouldOverwriteCacheOnPasswordchange() {

        when(passwordEncoder.matches(PASSWORD, ENCODED_PASSWORD)).thenReturn(true);
        when(passwordEncoder.matches(PASSWORD, "DifferentPassword")).thenReturn(false);
        when(passwordEncoder.matches(PASSWORD, ENCODED_PASSWORD)).thenReturn(true);

        assertThat(testClass.matches(PASSWORD, ENCODED_PASSWORD), equalTo(true));
        assertThat(testClass.matches(PASSWORD, "DifferentPassword"), equalTo(false));
        assertThat(testClass.matches(PASSWORD, ENCODED_PASSWORD), equalTo(true));
        assertThat(testClass.matches(PASSWORD, ENCODED_PASSWORD), equalTo(true));

        verify(passwordEncoder, times(2)).matches(PASSWORD, ENCODED_PASSWORD);
    }

}
