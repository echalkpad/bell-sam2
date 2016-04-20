package com.vennetics.bell.sam.error.adapters;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import com.vennetics.bell.sam.core.errors.model.ErrorDescription;
import com.vennetics.bell.sam.error.mapper.MappedError;
import generated.oma.xml.rest.netapi.common._1.PolicyException;
import generated.oma.xml.rest.netapi.common._1.RequestError;
import generated.oma.xml.rest.netapi.common._1.ServiceException;
import org.junit.Test;

public class NetapiErrorAdapterTest {

    private final IErrorAdapter adapter = new NetapiErrorAdapter();

    @Test
    public void shouldAdaptServiceException() {

        final MappedError error =
                new MappedError(dummyErrorDescription(), false, "A1", "A2");

        final Object result = adapter.adapt(error);

        assertThat(result, instanceOf(RequestError.class));

        final ServiceException exception = ((RequestError) result).getServiceException();

        assertThat(exception, is(notNullValue()));
        assertThat(exception.getMessageId(), is("code"));
        assertThat(exception.getText(), is("messageTemplate"));
        assertThat(exception.getVariables().size(), is(2));
        assertThat(exception.getVariables().get(0), is("A1"));
        assertThat(exception.getVariables().get(1), is("A2"));
    }

    @Test
    public void shouldAdaptPolicyException() {

        final MappedError error =
                new MappedError(dummyErrorDescription(), true, "A1", "A2");

        final Object result = adapter.adapt(error);

        assertThat(result, instanceOf(RequestError.class));

        final PolicyException exception = ((RequestError) result).getPolicyException();

        assertThat(exception, is(notNullValue()));
        assertThat(exception.getMessageId(), is("code"));
        assertThat(exception.getText(), is("messageTemplate"));
        assertThat(exception.getVariables().size(), is(2));
        assertThat(exception.getVariables().get(0), is("A1"));
        assertThat(exception.getVariables().get(1), is("A2"));
    }

    private ErrorDescription dummyErrorDescription() {
        return new ErrorDescription("code", "type", 400, "messageTemplate");
    }
}
