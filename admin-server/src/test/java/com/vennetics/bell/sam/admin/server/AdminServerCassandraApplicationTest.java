package com.vennetics.bell.sam.admin.server;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import com.vennetics.bell.sam.admin.server.utils.AdminServerTestUtils;
import com.vennetics.bell.sam.model.admin.service.entities.cassandra.CassandraApplication;
import org.cassandraunit.utils.EmbeddedCassandraServerHelper;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.net.URI;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration Test for AdminServer.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@SpringApplicationConfiguration(classes = {AdminServerApplication.class, AdminServerCassandraTestConfig.class})
@Category(IntegrationTest.class)
@WebIntegrationTest({ "server.port:0", "eureka.client.enabled:false",
        "spring.cloud.config.enabled:false", "spring.cloud.config.discovery.enabled:false",
        "security.basic.enabled:false" })
public class AdminServerCassandraApplicationTest {

    private static final Logger logger = LoggerFactory.getLogger(AdminServerCassandraApplicationTest.class);

    @Value("${local.server.port}")
    private static int port;

    private final MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
                                                        MediaType.APPLICATION_JSON.getSubtype(),
                                                        Charset.forName("utf8"));
    private MockMvc mockMvc;
    @Autowired
    private WebApplicationContext wac;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
    private static CassandraApplication testCassandraApplication;
    private static URI uri;

    @BeforeClass
    public static void setUp() throws Exception {
        EmbeddedCassandraServerHelper.startEmbeddedCassandra(
                EmbeddedCassandraServerHelper.CASSANDRA_RNDPORT_YML_FILE, 90000L);

        final Cluster cluster = Cluster.builder()
                                       .addContactPoint(AdminServerTestUtils.CASSANDRA_HOST)
                                       .withPort(EmbeddedCassandraServerHelper
                                                         .getNativeTransportPort())
                                       .build();
        logger.debug("Test Server Started at {}:{}",
                     AdminServerTestUtils.CASSANDRA_HOST,
                     EmbeddedCassandraServerHelper.getNativeTransportPort());
        port = EmbeddedCassandraServerHelper.getNativeTransportPort();

        EmbeddedCassandraServerHelper.startEmbeddedCassandra(
                EmbeddedCassandraServerHelper.CASSANDRA_RNDPORT_YML_FILE, 90000L);

        final Session session = cluster.connect();
        session.execute(AdminServerTestUtils.KEYSPACE_CREATE);
        logger.debug("Keyspace created");
        session.execute(AdminServerTestUtils.CREATE_ALLOWED_SERVICES_TABLE);
        session.execute(AdminServerTestUtils.CREATE_APPLICATION_TABLE);
        session.execute(AdminServerTestUtils.CREATE_CLIENT_TABLE);
        session.execute(AdminServerTestUtils.CREATE_POLICY_TYPE_TABLE);
        session.execute(AdminServerTestUtils.CREATE_PROTOCOL_TYPE_TABLE);
        session.execute(AdminServerTestUtils.CREATE_ROLE_TABLE);
        session.execute(AdminServerTestUtils.CREATE_SERVICE_TYPE_TABLE);
        logger.debug("Tables Created");

        testCassandraApplication = AdminServerTestUtils.createDummyApplication();
        uri = new URI("http://127.0.0.1:" + port + "/" + CassandraApplication.TABLE_NAME);
        logger.debug("AAW port>>" + port);
        logger.debug("AAW uri>>" + uri);
    }

    @AfterClass
    public static void tearDown() {
        logger.info("Stopping Embedded Cassandra");
        EmbeddedCassandraServerHelper.cleanEmbeddedCassandra();
    }

    @Before
    public void mockMvcSetup() {
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        mockMvc = MockMvcBuilders.webAppContextSetup(this.wac)
                                 .build();
    }


    @Test
    public void testACreate() throws Exception {

        // Create
        mockMvc.perform(post(uri).contentType(contentType)
                                 .content(AdminServerTestUtils.convertObjectToJsonBytes(testCassandraApplication))
                                 .accept(contentType))
               .andDo(print())
               .andExpect(status().isCreated())
               .andExpect(content().contentType(contentType))
               .andExpect(jsonPath("$.clientId").value(AdminServerTestUtils.TEST_CLIENT_ID))
               .andExpect(jsonPath("$.roleIds").isArray())
               .andExpect(jsonPath("$.roleIds[0]").value(AdminServerTestUtils.TEST_ROLES.toArray()[0].toString()))
               .andExpect(jsonPath("$.policies").isArray())
               .andExpect(jsonPath("$.policies[0]").value(AdminServerTestUtils.TEST_POLICIES.toArray()[0].toString()))
               .andExpect(jsonPath("$.gatewayPolicies").isArray())
               .andExpect(jsonPath("$.gatewayPolicies[0]").value(AdminServerTestUtils.TEST_GW_POLICIES.toArray()[0].toString()))
               .andExpect(jsonPath("$.allowedServiceIds").isArray())
               .andExpect(jsonPath("$.allowedServiceIds[0]").value(AdminServerTestUtils.TEST_SERVICES.toArray()[0].toString()))
               .andExpect(jsonPath("$.name").value(AdminServerTestUtils.TEST_NAME))
               .andExpect(jsonPath("$.email").value(AdminServerTestUtils.TEST_EMAIL))
               .andExpect(jsonPath("$.secret").exists())
               .andExpect(jsonPath("$.accountState").value(AdminServerTestUtils.TEST_ACCOUNT_STATE))
               .andExpect(jsonPath("$.createdTimestamp").value(dateFormat.format(AdminServerTestUtils.TEST_CREATION_DATE)))
               .andExpect(jsonPath("$.lastModifiedTimestamp").value(dateFormat.format(AdminServerTestUtils.TEST_MODIFIED_DATE)));

    }

    @Test
    public void testBRead() throws Exception {
        // Read
        mockMvc.perform(get(uri + "/" + testCassandraApplication.getApplicationId()).contentType(contentType)
                                                                                    .content(AdminServerTestUtils.convertObjectToJsonBytes(testCassandraApplication))
                                                                                    .accept(contentType))
               .andDo(print())
               .andExpect(status().isOk())
               .andExpect(content().contentType(contentType))
               .andExpect(jsonPath("$.clientId").value(AdminServerTestUtils.TEST_CLIENT_ID))
               .andExpect(jsonPath("$.roleIds").isArray())
               .andExpect(jsonPath("$.roleIds[0]").value(AdminServerTestUtils.TEST_ROLES.toArray()[0].toString()))
               .andExpect(jsonPath("$.policies").isArray())
               .andExpect(jsonPath("$.policies[0]").value(AdminServerTestUtils.TEST_POLICIES.toArray()[0].toString()))
               .andExpect(jsonPath("$.gatewayPolicies").isArray())
               .andExpect(jsonPath("$.gatewayPolicies[0]").value(AdminServerTestUtils.TEST_GW_POLICIES.toArray()[0].toString()))
               .andExpect(jsonPath("$.allowedServiceIds").isArray())
               .andExpect(jsonPath("$.allowedServiceIds[0]").value(AdminServerTestUtils.TEST_SERVICES.toArray()[0].toString()))
               .andExpect(jsonPath("$.name").value(AdminServerTestUtils.TEST_NAME))
               .andExpect(jsonPath("$.email").value(AdminServerTestUtils.TEST_EMAIL))
               .andExpect(jsonPath("$.secret").exists())
               .andExpect(jsonPath("$.accountState").value(AdminServerTestUtils.TEST_ACCOUNT_STATE))
               .andExpect(jsonPath("$.createdTimestamp").value(dateFormat.format(AdminServerTestUtils.TEST_CREATION_DATE)))
               .andExpect(jsonPath("$.lastModifiedTimestamp").value(dateFormat.format(AdminServerTestUtils.TEST_MODIFIED_DATE)));
    }

    @Test
    public void testCUpdate() throws Exception {
        // Update
        testCassandraApplication.setName(AdminServerTestUtils.UPDATED_TEST_NAME);
        mockMvc.perform(put(uri + "/" + testCassandraApplication.getApplicationId()).contentType(contentType)
                                                                                    .content(AdminServerTestUtils.convertObjectToJsonBytes(testCassandraApplication))
                                                                                    .accept(contentType))
               .andDo(print())
               .andExpect(status().isOk())
               .andExpect(content().contentType(contentType))
               .andExpect(jsonPath("$.clientId").value(AdminServerTestUtils.TEST_CLIENT_ID))
               .andExpect(jsonPath("$.roleIds").isArray())
               .andExpect(jsonPath("$.roleIds[0]").value(AdminServerTestUtils.TEST_ROLES.toArray()[0].toString()))
               .andExpect(jsonPath("$.policies").isArray())
               .andExpect(jsonPath("$.policies[0]").value(AdminServerTestUtils.TEST_POLICIES.toArray()[0].toString()))
               .andExpect(jsonPath("$.gatewayPolicies").isArray())
               .andExpect(jsonPath("$.gatewayPolicies[0]").value(AdminServerTestUtils.TEST_GW_POLICIES.toArray()[0].toString()))
               .andExpect(jsonPath("$.allowedServiceIds").isArray())
               .andExpect(jsonPath("$.allowedServiceIds[0]").value(AdminServerTestUtils.TEST_SERVICES.toArray()[0].toString()))
               .andExpect(jsonPath("$.name").value(AdminServerTestUtils.UPDATED_TEST_NAME))
               .andExpect(jsonPath("$.email").value(AdminServerTestUtils.TEST_EMAIL))
               .andExpect(jsonPath("$.secret").exists())
               .andExpect(jsonPath("$.accountState").value(AdminServerTestUtils.TEST_ACCOUNT_STATE))
               .andExpect(jsonPath("$.createdTimestamp").value(dateFormat.format(AdminServerTestUtils.TEST_CREATION_DATE)))
               .andExpect(jsonPath("$.lastModifiedTimestamp").value(dateFormat.format(AdminServerTestUtils.TEST_MODIFIED_DATE)));
    }

    @Test
    public void testDDelete() throws Exception {
        // Delete
        mockMvc.perform(delete(uri + "/" + testCassandraApplication.getApplicationId()).contentType(contentType)
                                                                                       .content(AdminServerTestUtils.convertObjectToJsonBytes(testCassandraApplication))
                                                                                       .accept(contentType))
               .andDo(print())
               .andExpect(status().isNoContent());
    }
}
