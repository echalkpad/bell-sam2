package com.vennetics.bell.sam.oneapi.rest.v091;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;

import com.vennetics.bell.sam.model.smsx.SendSmsResponse;
import com.vennetics.bell.sam.rest.config.converters.XmlConverter;

public class MarshallingTest {

    private final MappingJackson2XmlHttpMessageConverter converter = new XmlConverter().xmlMessageConverter();

    @Test
    public void shouldSerializeInExpectedFormWithDeclaration() throws Exception {
        final SendSmsResponse testClass = new SendSmsResponse();
        testClass.setMessageId("messageId");

        final String xml = converter.getObjectMapper().writeValueAsString(testClass);

        assertTrue(xml.startsWith("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"));

        assertTrue(xml.contains("<SmsResponse xmlns=\"\" messageId=\"messageId\"></SmsResponse>"));
    }
}
