package com.vennetics.microservices.common.core.soap.gateway.adapter;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;

import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Exchange;
import org.apache.cxf.service.invoker.Factory;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ExceptionMappingJAXWSMethodInvokerTest {

    @Rule
    @SuppressWarnings("checkstyle:visibilitymodifier")
    public final ExpectedException thrown = ExpectedException.none();

    @Mock private Factory mockFactory;
    @Mock private ISoapExceptionMapper mockExceptionMapper;
    @Mock private Exchange mockExchange;

    private ExceptionMappingJAXWSMethodInvoker invoker = new ExceptionMappingJAXWSMethodInvoker(mockFactory, mockExceptionMapper);

    @Before
    public void setup() {
        invoker = new ExceptionMappingJAXWSMethodInvoker(mockFactory, mockExceptionMapper);
    }

    @Test
    public void shouldPerformInvocation() throws Exception {

        final Object result = invoker.performInvocation(mockExchange, this, findTestMethod("okMethod"), null);

        assertThat(result.toString(), is("OK_RESULT"));
    }

    @Test
    public void shouldMapInvocationTargetExceptions() throws Exception {

        when(mockExceptionMapper.mapExceptionToFault(isA(Exception.class))).thenReturn(new Fault(new NullPointerException()));

        thrown.expect(Fault.class);
        invoker.performInvocation(mockExchange, this, findTestMethod("badMethod"), null);
    }

    @Test
    public void shouldNotMapOtherExceptions() throws Exception {

        thrown.expect(NoSuchMethodException.class);
        invoker.performInvocation(mockExchange, this, findTestMethod("noSuchMethod"), null);
    }

    private Method findTestMethod(final String name) throws Exception {
        return this.getClass().getMethod(name);
    }

    public String okMethod() {
        return "OK_RESULT";
    }

    public void badMethod() throws Exception {
        throw new Exception("Bad");
    }

}
