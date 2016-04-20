package com.vennetics.bell.sam.cassandra;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.core.querybuilder.Select;
import com.vennetics.bell.sam.cassandra.utils.CassandraTestUtils;
import com.vennetics.shared.test.utils.categories.IntegrationTest;
import org.cassandraunit.utils.EmbeddedCassandraServerHelper;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.cassandra.core.CassandraOperations;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * Ensures basic CRUD operations for a {@link Subscriber} can be sent via REST and persisted in a local
 * Cassandra backend.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = CassandraApplication.class)
@Category(IntegrationTest.class)
@WebIntegrationTest({ "server.port:0", "eureka.client.enabled:false", "spring.cloud.config.enabled:false",
        "spring.cloud.config.discovery.enabled:false", "security.basic.enabled:false" })
@ComponentScan("com.vennetics.bell.sam.core.cassandra")
public class CassandraApplicationTest {

    private static final Logger logger = LoggerFactory.getLogger(CassandraApplicationTest.class);
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private RestTemplate restTemplate = new TestRestTemplate();

    @Autowired
    private CassandraOperations cassandraTemplate;

    @Value("${local.server.port}")
    private int port;

    @BeforeClass
    public static void setUp() throws Exception {
        EmbeddedCassandraServerHelper.startEmbeddedCassandra(EmbeddedCassandraServerHelper.CASSANDRA_RNDPORT_YML_FILE,
                90000L);
        final Cluster cluster = Cluster.builder().addContactPoint(CassandraTestUtils.CASSANDRA_HOST)
                .withPort(EmbeddedCassandraServerHelper.getNativeTransportPort()).build();
        logger.debug("Test Server Started at {}:{}", CassandraTestUtils.CASSANDRA_HOST,
                EmbeddedCassandraServerHelper.getNativeTransportPort());
        final Session session = cluster.connect();
        session.execute(CassandraTestUtils.KEYSPACE_CREATE);
        logger.debug("Keyspace created");
        session.execute(CassandraTestUtils.TABLE_CREATE);
        logger.debug("Subscriber Table Created");
    }

    @AfterClass
    public static void tearDown() {
        logger.debug("Stopping Embedded Cassandra");
        EmbeddedCassandraServerHelper.cleanEmbeddedCassandra();
    }

    @Test
    public void contextLoads() {
    }

    @Test
    public void shouldCreateSubscriber() throws IOException {
        logger.debug("Creating Test Subscriber...");
        Subscriber testSubscriber = CassandraTestUtils.createDummySubscriber();
        logger.debug("Created testSubscriber with id: [{}]", testSubscriber.getId());

        sendRestSubscriberRequest(testSubscriber);

        final Select select =
                QueryBuilder.select().from(Subscriber.SUBSCRIBERS)
                        .where(QueryBuilder.eq(Subscriber.SUBSCRIBERS_ID, testSubscriber.getId())).limit(10);
        Subscriber result = cassandraTemplate.selectOne(select, Subscriber.class);
        assertEquals(testSubscriber.getId(), result.getId());
        assertEquals(testSubscriber.getFirstName(), result.getFirstName());
        assertEquals(testSubscriber.getSurName(), result.getSurName());
        assertEquals(testSubscriber.getMessage(), result.getMessage());

        cassandraTemplate.deleteAll(Subscriber.class);

    }

    @Test
    public void shouldCreateAndDeleteSubscriber() throws IOException {
        logger.debug("Creating Test Subscriber...");
        Subscriber testSubscriber = CassandraTestUtils.createDummySubscriber();
        logger.debug("Created testSubscriber with id: [{}]", testSubscriber.getId());

        sendRestSubscriberRequest(testSubscriber);

        logger.debug("Attempting to Delete Subscriber with id [{}]", testSubscriber.getId());
        restTemplate.delete("http://127.0.0.1:" + port + "/" + Subscriber.SUBSCRIBERS + "/" + testSubscriber.getId());
        logger.debug("Subscriber Deleted with id [{}]", testSubscriber.getId());

        final Select deleteSelect = QueryBuilder.select().from(Subscriber.SUBSCRIBERS)
                .where(QueryBuilder.eq(Subscriber.SUBSCRIBERS_ID, testSubscriber.getId())).limit(10);
        Subscriber deleteResult = cassandraTemplate.selectOne(deleteSelect, Subscriber.class);

        assertNull(deleteResult);

        cassandraTemplate.deleteAll(Subscriber.class);

    }

    @Test
    public void shouldCreateAndUpdateASubscriber() throws IOException {
        logger.debug("Creating Test Subscriber...");
        Subscriber testSubscriber = CassandraTestUtils.createDummySubscriber();
        logger.debug("Created testSubscriber with id: [{}]", testSubscriber.getId());

        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);

        sendRestSubscriberRequest(testSubscriber);

        final String id = testSubscriber.getId();

        logger.debug("Updating Subscriber JSON body");
        Map<String, Object> updateRequestBody = new HashMap<String, Object>();
        updateRequestBody.put(Subscriber.SUBSCRIBERS_ID, id);
        updateRequestBody.put("firstName", CassandraTestUtils.UPDATED_TEST_FIRSTNAME + id);
        updateRequestBody.put("surName", CassandraTestUtils.UPDATED_TEST_SURNAME + id);
        updateRequestBody.put(Subscriber.SUBSCRIBERS_MESSAGE, CassandraTestUtils.UPDATED_TEST_MESSAGE + id);
        logger.debug("Created JSON body: [{}]", updateRequestBody);

        logger.debug("Attempting to Update Subscriber with id [{}]", testSubscriber.getId());

        HttpEntity<String> httpUpdateEntity =
                new HttpEntity<String>(OBJECT_MAPPER.writeValueAsString(updateRequestBody), requestHeaders);

        restTemplate.put("http://127.0.0.1:" + port + "/" + Subscriber.SUBSCRIBERS + "/"
                + testSubscriber.getId(), httpUpdateEntity);
        logger.info("Subscriber Updated with id [{}]", testSubscriber.getId());

        Subscriber updatedSubscriber = restTemplate.getForObject("http://127.0.0.1:" + port + "/"
                + Subscriber.SUBSCRIBERS + "/"
                + testSubscriber.getId(), Subscriber.class);

//        final Select idSelect = QueryBuilder.select().from(Subscriber.SUBSCRIBERS)
//                .where(QueryBuilder.eq("id", id)).limit(10);
//

        assertEquals(CassandraTestUtils.UPDATED_TEST_FIRSTNAME + id, updatedSubscriber.getFirstName());
        assertEquals(CassandraTestUtils.UPDATED_TEST_SURNAME + id, updatedSubscriber.getSurName());
        assertEquals(CassandraTestUtils.UPDATED_TEST_MESSAGE + id, updatedSubscriber.getMessage());

    }

    @Test
    public void shouldGetASubscriber() throws IOException {
        logger.debug("Creating Test Subscriber...");
        Subscriber testSubscriber = CassandraTestUtils.createDummySubscriber();
        logger.debug("Created testSubscriber with id: [{}]", testSubscriber.getId());

        sendRestSubscriberRequest(testSubscriber);

        Subscriber result = restTemplate.getForObject("http://127.0.0.1:" + port + "/"
                + Subscriber.SUBSCRIBERS + "/" +  testSubscriber.getId(), Subscriber.class);

        assertNotNull(result);
        assertEquals(testSubscriber.getFirstName(), result.getFirstName());
        assertEquals(testSubscriber.getSurName(), result.getSurName());
        assertEquals(testSubscriber.getMessage(), result.getMessage());

    }

    private void sendRestSubscriberRequest(final Subscriber subscriber) throws IOException {

        logger.debug("Creating JSON body");
        Map<String, Object> requestBody = new HashMap<String, Object>();
        requestBody.put(Subscriber.SUBSCRIBERS_ID, subscriber.getId());
        requestBody.put("firstName", subscriber.getFirstName());
        requestBody.put("surName", subscriber.getSurName());
        requestBody.put(Subscriber.SUBSCRIBERS_MESSAGE, subscriber.getMessage());
        logger.debug("Created JSON body: [{}]", requestBody);

        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> httpEntity =
                new HttpEntity<String>(OBJECT_MAPPER.writeValueAsString(requestBody), requestHeaders);

        logger.debug("Posting Create Subscriber Request...");
        Map<String, Object> postResponse = restTemplate.postForObject("http://127.0.0.1:" + port
                + "/" + Subscriber.SUBSCRIBERS, httpEntity, Map.class, Collections.EMPTY_MAP);

        logger.debug("API Response Body: [{}]", postResponse);
        assertNotNull(postResponse);

        String message = postResponse.get(Subscriber.SUBSCRIBERS_MESSAGE).toString();
        assertEquals(subscriber.getMessage(), message);

        final Select postSelect = QueryBuilder.select().from(Subscriber.SUBSCRIBERS)
                        .where(QueryBuilder.eq(Subscriber.SUBSCRIBERS_ID, subscriber.getId())).limit(10);
        Subscriber postResult = cassandraTemplate.selectOne(postSelect, Subscriber.class);
        assertEquals(subscriber.getId(), postResult.getId());

        logger.debug("Subscriber Successfully Created and Persisted to Cassandra");
    }

}
