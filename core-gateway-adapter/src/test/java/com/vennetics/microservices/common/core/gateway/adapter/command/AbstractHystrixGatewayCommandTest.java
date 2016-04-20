package com.vennetics.microservices.common.core.gateway.adapter.command;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.Is.isA;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import com.netflix.hystrix.exception.HystrixBadRequestException;
import com.netflix.hystrix.exception.HystrixRuntimeException;

public class AbstractHystrixGatewayCommandTest {

    @Rule
    @SuppressWarnings("checkstyle:visibilitymodifier")
    public final ExpectedException thrown = ExpectedException.none();

    @Test
    public void shouldConstructWithCommandGroupAndKeyNames() {

        final AbstractHystrixGatewayCommand command = new DummyCommand("grp", "key", null);
        assertThat(command.getCommandGroup().name(), is("grp"));
        assertThat(command.getCommandKey().name(), is("key"));
    }

    @Test
    public void shouldCatchHttpClientRequestExceptions() {

        final AbstractHystrixGatewayCommand command = new DummyCommand("grp", "key", new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Oops"));

        thrown.expect(HystrixBadRequestException.class);
        thrown.expectCause(isA(HttpClientErrorException.class));
        command.execute();
    }

    @Test
    public void shouldNotCatchOtherExceptions() {

        final AbstractHystrixGatewayCommand command = new DummyCommand("grp", "key", new HttpServerErrorException(HttpStatus.SERVICE_UNAVAILABLE, "Oops"));

        thrown.expect(HystrixRuntimeException.class);
        thrown.expectCause(isA(HttpServerErrorException.class));

        command.execute();
    }

    private static class DummyCommand extends AbstractHystrixGatewayCommand<String> {
        private final RuntimeException exceptionToThrow;
        public DummyCommand(final String commandGroup, final String commandKey, final RuntimeException exceptionToThrow) {
            super(commandGroup, commandKey);
            this.exceptionToThrow = exceptionToThrow;
        }

        @Override
        protected String runCommand() {
            throw exceptionToThrow;
        }
    }
}
