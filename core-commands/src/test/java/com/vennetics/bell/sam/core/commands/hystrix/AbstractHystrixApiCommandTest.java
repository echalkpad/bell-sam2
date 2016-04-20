package com.vennetics.bell.sam.core.commands.hystrix;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.netflix.hystrix.exception.HystrixBadRequestException;
import com.vennetics.bell.sam.core.exception.BadRequestException;

public class AbstractHystrixApiCommandTest {

    private static final String RUN_RESULT = "RESULT";

    private TestCommand command;
    private ExceptionalTestCommand exceptionTestCommand;

    @Before
    public void setup() {
        command = new TestCommand();
        exceptionTestCommand = new ExceptionalTestCommand();
    }

    @Test
    public void testDefaults() {
        assertEquals("commandGroupKeyAbstractHystrixApiCommandTest", command.getCommandGroup()
                .name());
        assertEquals("commandNameKeyAbstractHystrixApiCommandTest", command.getCommandKey().name());
    }

    @Test
    public void testRunCommand() {
        assertEquals(RUN_RESULT, command.execute());
    }

    @Test(expected = HystrixBadRequestException.class)
    public void testRunCommandHandlesBadRequestException() {
        exceptionTestCommand.execute();
    }

    private static class TestCommand extends AbstractHystrixApiCommand<String> {
        public TestCommand() {
            super("commandGroupKeyAbstractHystrixApiCommandTest",
                    "commandNameKeyAbstractHystrixApiCommandTest");
        }

        @Override
        protected String runCommand() {
            return RUN_RESULT;
        }

    }

    private enum TestErrorMessage {
        ERROR
    }

    private static class MyError extends BadRequestException {
        public MyError(final Enum<?> messageCode, final String... messageParams) {
            super(messageCode, messageParams);
        }
    }

    private class ExceptionalTestCommand extends AbstractHystrixApiCommand<String> {
        public ExceptionalTestCommand() {
            super("commandGroupKeyAbstractHystrixApiCommandTest",
                    "commandNameKeyAbstractHystrixApiCommandTest");
        }

        @Override
        protected String runCommand() {
            throw new MyError(TestErrorMessage.ERROR, "");
        }

    }
}
