package com.vennetics.bell.sam.discovery;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import com.vennetics.shared.test.utils.categories.IntegrationTest;
import org.junit.Before;
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

import java.util.Map;

/**
 * Test Case to verify that we can run up the application and pull sensible
 * values back for various endpoints.
 * 
 * @author markcorkery
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = DiscoveryApplication.class)
@WebIntegrationTest({ "server.port:0" })
@Category(IntegrationTest.class)
public class EurekaApplicationTests {

    @Value("${local.server.port}")
    private int port;
    
    @Value("${security.user.name}")
    private String username;
    
    @Value("${security.user.password}")
    private String password;

    private RestTemplate restTemplate;

    @Before
    public void setUpRestTemplateWithAuthCredentials() {
        restTemplate = new TestRestTemplate(username, password);
    }
    
    @Test
    public void contextLoads() {
    }

    @Test
    public void testAppCatalogLoads() {
        @SuppressWarnings("rawtypes")
        ResponseEntity<Map> entity = restTemplate.getForEntity("http://localhost:" + port
                        + "/eureka/apps", Map.class);
        assertEquals(HttpStatus.OK, entity.getStatusCode());
    }

    @Test
    public void testAdminLoads() {
        ResponseEntity<String> entity = restTemplate.getForEntity("http://localhost:" + port + "/",
                                                                  String.class);
        assertEquals(HttpStatus.OK, entity.getStatusCode());
    }

    @Test
    public void testEnvLoads() {
        ResponseEntity<String> entity = restTemplate.getForEntity("http://localhost:" + port
                        + "/env", String.class);
        assertEquals(HttpStatus.OK, entity.getStatusCode());
        assertThat(entity.getBody(), containsString("\"spring.application.name\":\"discoveryservice\""));    }

    @Test
    public void testEnvFailsWhenNoAuthSupplied() {
        final RestTemplate noAuthTemplate = new TestRestTemplate();
        ResponseEntity<String> entity = noAuthTemplate.getForEntity("http://localhost:" + port
                        + "/env", String.class);
        assertEquals(HttpStatus.UNAUTHORIZED, entity.getStatusCode());
    }
    
    @Test
    public void testHealthLoads() {
        ResponseEntity<String> entity = restTemplate.getForEntity("http://localhost:" + port
                        + "/health", String.class);
        assertEquals(HttpStatus.OK, entity.getStatusCode());
        assertTrue(entity.getBody().contains("\"status\":\"UP\""));
        assertTrue(entity.getBody().contains("diskSpace"));
    }

    @Test
    public void testHealthLoadsWhenNoAuthSupplied() {
        final RestTemplate noAuthTemplate = new TestRestTemplate();
        ResponseEntity<String> entity = noAuthTemplate.getForEntity("http://localhost:" + port
                        + "/health", String.class);
        assertEquals(HttpStatus.OK, entity.getStatusCode());
        assertEquals(entity.getBody(), "{\"description\":\"Spring Cloud Eureka Discovery Client\",\"status\":\"UP\"}");
    }
}
