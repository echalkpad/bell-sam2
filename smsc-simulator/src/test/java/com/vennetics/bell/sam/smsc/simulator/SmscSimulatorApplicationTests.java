package com.vennetics.bell.sam.smsc.simulator;

import static org.junit.Assert.assertEquals;
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
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

import com.vennetics.shared.test.utils.categories.IntegrationTest;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = SmscSimulatorApplication.class)
@WebIntegrationTest({ "server.port:0", "eureka.client.enabled:false", "spring.cloud.config.enabled:false",
        "spring.cloud.config.discovery.enabled:false", "security.basic.enabled:false" })
@Category(IntegrationTest.class)
public class SmscSimulatorApplicationTests {

    @Value("${local.server.port}")
    private int port;

    private final RestTemplate restTemplate = new TestRestTemplate();

    @Test
    public void contextLoads() {
    }
    
    @Test
    public void testEnvLoads() {
        ResponseEntity<String> entity = restTemplate.getForEntity("http://localhost:" + port
                        + "/env", String.class);
        assertEquals(HttpStatus.OK, entity.getStatusCode());
        assertTrue(entity.getBody().contains("\"spring.application.name\":\"smsc-simulator\""));
    }
}
