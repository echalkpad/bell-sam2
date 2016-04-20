package com.vennetics.bell.sam.core.errors.model;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class ErrorDescriptionTest {

    @Test
    public void shouldDeliverFields() {
        final ErrorDescription description = new ErrorDescription("code", "type", 400, "message");

        assertThat(description.getCode(), is("code"));
        assertThat(description.getType(), is("type"));
        assertThat(description.getStatusCode(), is(400));
        assertThat(description.getMessage(), is("message"));
    }

    @Test
    public void shouldImplementToString() {
        final ErrorDescription description = new ErrorDescription("code", "type", 400, "message");

        assertThat(description.toString(), is("Error [code=code, type=type, statusCode=400, message=message]"));
    }

    @Test
    public void shouldImplementHashCodeOnCodeAndType() {
        final ErrorDescription description1 = new ErrorDescription("code1", "typea", 400, "message");
        final ErrorDescription description2 = new ErrorDescription("code1", "typez", 400, "message");
        final ErrorDescription description3 = new ErrorDescription("code2", "typez", 400, "message");

        assertThat(description1.hashCode(), is(not(description2.hashCode())));
        assertThat(description2.hashCode(), is(not(description3.hashCode())));
    }

    @Test
    public void shouldImplementEqualsBasedOnCodeAndType() {
        final ErrorDescription description1 = new ErrorDescription("code1", "typea", 400, "message");
        final ErrorDescription description2 = new ErrorDescription("code1", "typez", 400, "message");
        final ErrorDescription description3 = new ErrorDescription("code2", "typez", 400, "message");
        final ErrorDescription description4 = new ErrorDescription("code2", "typez", 401, "message2");
        final ErrorDescription description5 = new ErrorDescription(null, "typez", 400, "message");
        final ErrorDescription description6 = new ErrorDescription(null, "typez", 400, "message");
        final ErrorDescription description7 = new ErrorDescription("code2", null, 400, "message");
        final ErrorDescription description8 = new ErrorDescription("code2", null, 400, "message");

        assertThat(description1, equalTo(description1));
        assertThat(description1, not(equalTo("Not an error description!")));
        assertThat(description1, not(equalTo(null)));
        assertThat(description1, not(equalTo(description2)));
        assertThat(description1, not(equalTo(description2)));
        assertThat(description2, not(equalTo(description3)));
        assertThat(description3, equalTo(description4));
        assertThat(description5, equalTo(description6));
        assertThat(description7, equalTo(description8));
        assertThat(description5, not(equalTo(description7)));
        assertThat(description7, not(equalTo(description5)));
    }

}
