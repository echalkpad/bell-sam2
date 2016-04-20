package com.vennetics.bell.sam.callback.simulator;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

import com.vennetics.shared.test.utils.categories.IntegrationTest;

/**
 * Test Case to verify that we can run up the application and pull sensible
 * values back.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = CallbackSimulatorApplication.class)
@WebIntegrationTest({ "server.port:0", "eureka.client.enabled:false",
        "spring.cloud.config.enabled:false", "spring.cloud.config.discovery.enabled:false",
        "security.basic.enabled:false" })
@Category(IntegrationTest.class)
@TestPropertySource(locations = "classpath:test.properties")
public class CallbackSimulatorApplicationTest {

    @Value("${local.server.port}")
    private int port;

    private final RestTemplate restTemplate = new TestRestTemplate();

    @Test
    public void contextLoads() {
    }

    @Test
    public void testEnvLoads() {

        final ResponseEntity<String> entity = restTemplate.getForEntity("http://localhost:" + port
                        + "/env", String.class);
        assertThat(entity.getStatusCode(), is(HttpStatus.OK));
        assertTrue(entity.getBody().contains("\"spring.application.name\":\"callback-simulator\""));
    }

}
