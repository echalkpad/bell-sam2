package com.vennetics.bell.sam.oneapi.rest.v091.sms;

import com.vennetics.bell.sam.core.errors.model.ErrorDescription;
import com.vennetics.bell.sam.error.adapters.IErrorAdapter;
import com.vennetics.bell.sam.error.mapper.MappedError;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.instanceOf;

/**
 * Created on 16/03/2016.
 */
public class OneapiErrorAsStringAdapterTest {

    // Object Under Test
    private final IErrorAdapter objectUnderTest = new OneapiErrorAsStringAdapter();

    @Test
    public void shouldAdaptServiceException() {

        final MappedError error =
                        new MappedError(dummyErrorDescription(), false, "A1", "A2");

        final Object result = objectUnderTest.adapt(error);

        assertThat(result, instanceOf(String.class));

        final String restError = (String) result;

        assertThat(restError, containsString("messageTemplate first A1 second A2"));
    }

    @Test
    public void shouldAdaptPolicyException() {

        final MappedError error =
                        new MappedError(dummyErrorDescription(), true, "A1", "A2");

        final Object result = objectUnderTest.adapt(error);

        assertThat(result, instanceOf(String.class));

        final String restError = (String) result;

        assertThat(restError, containsString("A policy error occurred. Error code is: code: messageTemplate first A1 second A2"));
    }

    private ErrorDescription dummyErrorDescription() {
        return new ErrorDescription("code", "type", 400, "messageTemplate first {0} second {1}");
    }

}
