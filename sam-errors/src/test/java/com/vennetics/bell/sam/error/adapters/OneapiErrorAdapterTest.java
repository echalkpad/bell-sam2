package com.vennetics.bell.sam.error.adapters;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;

import com.vennetics.bell.sam.core.errors.model.ErrorDescription;
import com.vennetics.bell.sam.error.Restv091Error;
import com.vennetics.bell.sam.error.mapper.MappedError;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OneapiErrorAdapterTest {

    private static final Logger LOG = LoggerFactory.getLogger(OneapiErrorAdapterTest.class);

    private final IErrorAdapter adapter = new OneapiErrorAdapter();

    @Test
    public void shouldAdaptServiceException() {

        final MappedError error =
                new MappedError(dummyErrorDescription(), false, "A1", "A2");

        final Object result = adapter.adapt(error);

        assertThat(result, instanceOf(Restv091Error.class));

        final Restv091Error restError = (Restv091Error) result;

        assertThat(restError.getErrorMessage(), is("messageTemplate first A1 second A2"));
    }

    @Test
    public void shouldAdaptPolicyException() {

        final MappedError error =
                new MappedError(dummyErrorDescription(), true, "A1", "A2");

        final Object result = adapter.adapt(error);

        assertThat(result, instanceOf(Restv091Error.class));

        final Restv091Error restError = (Restv091Error) result;

        assertThat(restError.getErrorMessage(), is("A policy error occurred. Error code is: code: messageTemplate first A1 second A2"));
    }

    private ErrorDescription dummyErrorDescription() {
        return new ErrorDescription("code", "type", 400, "messageTemplate first {0} second {1}");
    }
}
