package com.vennetics.bell.sam.configserver;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.cloud.config.environment.Environment;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

import com.vennetics.shared.test.utils.categories.IntegrationTest;

/**
 * Test Case to verify that we can run up the application and pull sensible
 * values back.
 *
 * @author markcorkery
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ConfigServerApplication.class)
@WebIntegrationTest({ "server.port:0", "eureka.client.enabled:false",
        "security.basic.enabled:false",
        "spring.cloud.config.server.git.uri:git@bell-sam-config.github.com:Vennetics/bell-sam-config.git" })
@Category(IntegrationTest.class)
public class ConfigServerApplicationTests {

    @Value("${local.server.port}")
    private int port;

    private final RestTemplate restTemplate = new TestRestTemplate();

    @Test
    public void contextLoads() {
    }

    @Test
    public void testDefaultProfileConfigIsAccessible() {
        final Environment testConfig = restTemplate.getForObject("http://localhost:" + port
                        + "/test/default", Environment.class);
        assertNotNull(testConfig);
        assertEquals("test", testConfig.getName());
        assertEquals("default", testConfig.getProfiles()[0]);
        assertEquals("git@bell-sam-config.github.com:Vennetics/bell-sam-config.git/test.yml",
                     testConfig.getPropertySources().get(0).getName());
        assertEquals("bar", testConfig.getPropertySources().get(0).getSource().get("info.foo"));
    }

    @Test
    public void testTestProfileConfigIsAccessible() {
        final Environment testConfigWithTestProfile = restTemplate.getForObject("http://localhost:"
                        + port + "/test/test", Environment.class);
        assertNotNull(testConfigWithTestProfile);
        assertEquals("test", testConfigWithTestProfile.getName());
        assertEquals("test", testConfigWithTestProfile.getProfiles()[0]);
        assertEquals("git@bell-sam-config.github.com:Vennetics/bell-sam-config.git/test.yml#test",
                     testConfigWithTestProfile.getPropertySources().get(0).getName());
        assertEquals("bart",
                     testConfigWithTestProfile.getPropertySources()
                                              .get(0)
                                              .getSource()
                                              .get("info.foo"));
    }

}
