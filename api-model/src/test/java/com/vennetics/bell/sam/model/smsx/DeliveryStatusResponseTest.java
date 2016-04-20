package com.vennetics.bell.sam.model.smsx;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.nullValue;
import static org.hamcrest.core.IsSame.sameInstance;

import org.junit.Test;

/**
 * Created on 08/03/2016.
 */
public class DeliveryStatusResponseTest {

    @Test
    public void shouldNotReturnNullListObject() throws Exception {
        final DeliveryStatusResponse objectUnderTest = new DeliveryStatusResponse();
        assertThat(objectUnderTest.getMessageResponses(), is(not(nullValue())));
    }

    @Test
    public void shouldSameObjectForFirstAndSubsequentCalls() throws Exception {
        final DeliveryStatusResponse objectUnderTest = new DeliveryStatusResponse();
        assertThat(objectUnderTest.getMessageResponses(), sameInstance(objectUnderTest.getMessageResponses()));
    }
}
