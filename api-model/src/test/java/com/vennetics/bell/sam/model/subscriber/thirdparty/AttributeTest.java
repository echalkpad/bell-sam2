package com.vennetics.bell.sam.model.subscriber.thirdparty;

import static org.junit.Assert.assertTrue;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;


/**
 * The test class for the SubscriberProfile POJO
 *
 */
public class AttributeTest {

    private String name;
    private String value;

    @Before
    public void setUp() {
        // Create test data
        name = "min";
        value = "5142509795";
    }

    @Test
    public void testAttributePojo() throws JsonProcessingException {

        final Attribute attribute = new Attribute(name, value);
        Assert.assertEquals("The Attribute returned an unexpected value",
                            name, attribute.getName());
        Assert.assertEquals("The Attribute returned an unexpected value",
                            value, attribute.getValue());
    }

    @Test
    public void shouldImplementEquals() {
        EqualsVerifier.forClass(Attribute.class).suppress(Warning.NONFINAL_FIELDS).verify();
    }

    @Test
    public void shouldImplementToString() {
        assertTrue(new Attribute(name, value).toString().contains("name=min"));
        assertTrue(new Attribute(name, value).toString().contains("value=5142509795"));
    }
}
