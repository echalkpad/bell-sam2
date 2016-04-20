package com.vennetics.bell.sam.error.adapters;

import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Test;

import com.vennetics.bell.sam.core.errors.model.ErrorDescription;
import com.vennetics.bell.sam.error.mapper.MappedError;

public class BellSubscriberErrorAdapterTest {

    private final IErrorAdapter adapter = new BellSubscriberErrorAdapter();

    @Test
    public void shouldAdaptServiceException() {

        final MappedError error = new MappedError(dummyErrorDescription(), false, "A1", "A2");

        final Object result = adapter.adapt(error);

        assertThat(result, sameInstance(error));
    }

    @Test
    public void shouldAdaptPolicyException() {

        final MappedError error = new MappedError(dummyErrorDescription(), true, "A1", "A2");

        final Object result = adapter.adapt(error);

        assertThat(result, sameInstance(error));
    }

    private ErrorDescription dummyErrorDescription() {
        return new ErrorDescription("code", "type", 400, "messageTemplate");
    }
}
