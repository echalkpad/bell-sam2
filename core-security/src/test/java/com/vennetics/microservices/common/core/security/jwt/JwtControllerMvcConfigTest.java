package com.vennetics.microservices.common.core.security.jwt;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.servlet.HandlerInterceptor;

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { JwtControllerMvcConfig.class })
public class JwtControllerMvcConfigTest {

    @Autowired
    private HandlerInterceptor jwtHeaderServerInterceptor;

    @Test
    public void shouldLoadInterceptorIntoConfig() {
        assertNotNull(jwtHeaderServerInterceptor);
    }

}
