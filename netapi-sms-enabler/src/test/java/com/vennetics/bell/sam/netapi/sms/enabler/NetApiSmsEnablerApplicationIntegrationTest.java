package com.vennetics.bell.sam.netapi.sms.enabler;

import com.vennetics.shared.test.utils.categories.IntegrationTest;
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Test Case to verify that we can run up the application and pull sensible
 * values back.
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = NetApiSmsEnablerApplication.class)
@WebIntegrationTest({ "server.port:0", "eureka.client.enabled:false",
        "spring.cloud.config.enabled:false", "spring.cloud.config.discovery.enabled:false",
        "security.basic.enabled:false", "smpp.bindType=TRANSCEIVER",
        "smpp.primarySmppConnection.host=localhost", "smpp.windowSize=1",
        "smpp.primarySmppConnection.connectionTimeout=10000", "smpp.maxConcurrentSessions=1",
        "smpp.requestExpiryTimeout=5000", "smpp.systemId=smppclient1", "smpp.password=password",
        "smpp.enquireLinkPollingInterval=15000", "smpp.sourceAddress.ton=3",
        "smpp.sourceAddress.npi=0", "smpp.destinationAddress.ton=1",
        "smpp.destinationAddress.npi=1", "address.defaultRegion=CA",
        "smpp.enquireLinkTimeoutMilliseconds=5000",
        "smpp.outBoundThrottlingRate.intervalInMilliseconds=10000",
        "smpp.outBoundThrottlingRate.maxTasksPerInterval=100",
        "smpp.inBoundThrottlingRate.intervalInMilliseconds=10000",
        "smpp.inBoundThrottlingRate.maxTasksPerInterval=100",
        "smpp.smppUnbindTimeoutInMilliseconds=5000",
        "smpp.submitMessageTimeoutMilliseconds=10000"})
@Category(IntegrationTest.class)
public class NetApiSmsEnablerApplicationIntegrationTest {

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
        assertTrue(entity.getBody().contains("\"spring.application.name\":\"netapi-sms-enabler\""));
    }

}
