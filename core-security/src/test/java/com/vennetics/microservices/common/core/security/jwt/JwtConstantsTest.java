package com.vennetics.microservices.common.core.security.jwt;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class JwtConstantsTest {

    @Test
    public void shouldSetConstantForHeaderValue() {
        assertThat(JwtConstants.JWT_HEADER_INTERNAL, equalTo("X-VENNETICS-JWT-TOKEN"));
    }

}
