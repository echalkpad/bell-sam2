package com.vennetics.microservices.common.core.security.jwt;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vennetics.bell.sam.core.exception.PlatformFailedException;

public class JwtTokenBuilderTest {

    private static final String SUBJECT = "MyTestString";

    @Test
    public void shouldSupportTwoWayMapping() {
        final IJwtTokenBuilder<String> testClass = new JwtTokenBuilder<>(new ObjectMapper());

        assertEquals(SUBJECT,
                     testClass.getSubjectFromJwtToken(testClass.createJwtTokenFromSubject(SUBJECT), String.class));
    }

    @Test(expected = PlatformFailedException.class)
    public void shouldMapCreationFailuresToPlatformEx() throws Exception {

        final ObjectMapper mapper = mock(ObjectMapper.class);
        final IJwtTokenBuilder<String> testClass = new JwtTokenBuilder<>(mapper);

        when(mapper.writeValueAsString(SUBJECT)).thenThrow(new JsonParseException("", null));

        testClass.createJwtTokenFromSubject(SUBJECT);
    }

    @Test(expected = PlatformFailedException.class)
    public void shouldMapParsingFailuresToPlatformEx() throws Exception {

        final ObjectMapper mapper = mock(ObjectMapper.class);
        final IJwtTokenBuilder<String> testClass = new JwtTokenBuilder<>(mapper);

        when(mapper.readValue(SUBJECT, String.class)).thenThrow(new IOException(""));

        testClass.getSubjectFromJwtToken(SUBJECT, String.class);
    }

}
