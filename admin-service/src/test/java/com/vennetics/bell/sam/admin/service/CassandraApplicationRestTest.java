package com.vennetics.bell.sam.admin.service;

import com.vennetics.bell.sam.admin.service.utils.TestUtils;
import com.vennetics.bell.sam.core.cassandra.helpers.EmbeddedCassandraTestHelper;
import com.vennetics.bell.sam.model.admin.service.entities.cassandra.CassandraApplication;
import org.cassandraunit.utils.EmbeddedCassandraServerHelper;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
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
import org.springframework.restdocs.RestDocumentation;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.net.URI;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import static com.vennetics.bell.sam.admin.service.utils.TestUtils.ACCOUNT_STATE;
import static com.vennetics.bell.sam.admin.service.utils.TestUtils.CASSANDRA_HOST;
import static com.vennetics.bell.sam.admin.service.utils.TestUtils.CLIENT_ID_1;
import static com.vennetics.bell.sam.admin.service.utils.TestUtils.CREATE_ALLOWED_SERVICES_TABLE;
import static com.vennetics.bell.sam.admin.service.utils.TestUtils.CREATE_APPLICATION_TABLE;
import static com.vennetics.bell.sam.admin.service.utils.TestUtils.CREATE_CLIENT_TABLE;
import static com.vennetics.bell.sam.admin.service.utils.TestUtils.CREATE_POLICY_TYPE_TABLE;
import static com.vennetics.bell.sam.admin.service.utils.TestUtils.CREATE_PROTOCOL_TYPE_TABLE;
import static com.vennetics.bell.sam.admin.service.utils.TestUtils.CREATE_ROLE_TABLE;
import static com.vennetics.bell.sam.admin.service.utils.TestUtils.CREATE_SERVICE_TYPE_TABLE;
import static com.vennetics.bell.sam.admin.service.utils.TestUtils.EMAIL;
import static com.vennetics.bell.sam.admin.service.utils.TestUtils.KEYSPACE_CREATE;
import static com.vennetics.bell.sam.admin.service.utils.TestUtils.NAME;
import static com.vennetics.bell.sam.admin.service.utils.TestUtils.POLICIES_STRING;
import static com.vennetics.bell.sam.admin.service.utils.TestUtils.ROLE_IDS;
import static com.vennetics.bell.sam.admin.service.utils.TestUtils.SERVICE_IDS;
import static com.vennetics.bell.sam.admin.service.utils.TestUtils.SERVICE_POLICIES_STRING;
import static org.hamcrest.Matchers.isIn;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
/**
 * CassandraApplication Rest endpoint tests
 */
@RunWith(SpringJUnit4ClassRunner.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@SpringApplicationConfiguration(classes = {AdminServiceSpringBootTestConfig.class})
@Category(IntegrationTest.class)
@WebIntegrationTest({ "server.port:0", "eureka.client.enabled:false",
                "spring.cloud.config.enabled:false", "spring.cloud.config.discovery.enabled:false",
                "security.basic.enabled:false" })
public class CassandraApplicationRestTest {

    @ClassRule
    public static final RestDocumentation REST_DOCUMENTATION = new RestDocumentation(
                    "build/generated-snippets");
    private static final Logger logger = LoggerFactory.getLogger(CassandraApplicationRestTest.class);
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
        EmbeddedCassandraTestHelper.startCassandraCluster(EmbeddedCassandraServerHelper.CASSANDRA_RNDPORT_YML_FILE,
                                                          port,
                                                          CASSANDRA_HOST,
                                                          KEYSPACE_CREATE,
                                                          CREATE_ALLOWED_SERVICES_TABLE,
                                                          CREATE_APPLICATION_TABLE,
                                                          CREATE_CLIENT_TABLE,
                                                          CREATE_POLICY_TYPE_TABLE,
                                                          CREATE_PROTOCOL_TYPE_TABLE,
                                                          CREATE_ROLE_TABLE,
                                                          CREATE_SERVICE_TYPE_TABLE);

        testCassandraApplication = TestUtils.createDummyCassApplication();
        uri = new URI("http://127.0.0.1:" + port + "/" + CassandraApplication.TABLE_NAME);
    }

    @AfterClass
    public static void tearDown() {
        logger.info("Stopping Embedded Cassandra");
        EmbeddedCassandraServerHelper.cleanEmbeddedCassandra();
    }

    @Before
    public void docSetup() {
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        mockMvc = MockMvcBuilders.webAppContextSetup(this.wac)
                                 .apply(documentationConfiguration(this.REST_DOCUMENTATION))
                                 .build();
    }

    @Test
    public void testACreate() throws Exception {

        // Create
        mockMvc.perform(post(uri).contentType(contentType)
                .content(TestUtils.convertObjectToJsonBytes(testCassandraApplication))
                .accept(contentType))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.clientId").value(CLIENT_ID_1))
                .andExpect(jsonPath("$.roleIds").isArray())
                .andExpect(jsonPath("$.roleIds[0]").value(isIn(ROLE_IDS)))
                .andExpect(jsonPath("$.policies").isArray())
                .andExpect(jsonPath("$.policies[0]").value(isIn(POLICIES_STRING)))
                .andExpect(jsonPath("$.gatewayPolicies").isArray())
                .andExpect(jsonPath("$.gatewayPolicies[0]").value(isIn(SERVICE_POLICIES_STRING)))
                .andExpect(jsonPath("$.allowedServiceIds").isArray())
                .andExpect(jsonPath("$.allowedServiceIds[0]").value(isIn(SERVICE_IDS)))
                .andExpect(jsonPath("$.name").value(NAME))
                .andExpect(jsonPath("$.email").value(EMAIL))
                .andExpect(jsonPath("$.secret").exists())
                .andExpect(jsonPath("$.accountState").value(ACCOUNT_STATE))
                .andExpect(jsonPath("$.createdTimestamp").value(dateFormat.format(TestUtils.CREATION_DATE)))
                .andExpect(jsonPath("$.lastModifiedTimestamp").value(dateFormat.format(TestUtils.MODIFIED_DATE)))
                .andDo(document("applications",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(fieldWithPath("applicationId").description("The ID of this Application."),
                                fieldWithPath("clientId").description("The Client ID that is associated with this Application."),
                                fieldWithPath("roleIds").description("A Set of Role IDs that this Application possess."),
                                fieldWithPath("policies").description(
                                        "A Set of JSON Objects representing the Policies enforced at the AllowedService (enabler) layer for this Application."),
                                fieldWithPath("gatewayPolicies").description(
                                        "A Set of JSON Objects representing the Gateway Policies enforced at the Gateway layer for this Application."),
                                fieldWithPath("allowedServiceIds").description("A Set of AllowedService IDs that this Application has access to."),
                                fieldWithPath("name").description("The name of this Application."),
                                fieldWithPath("email").description("The email of this Application."),
                                fieldWithPath("secret").description("The secret of this Application."),
                                fieldWithPath("accountState").description("The account state of this Application e.g. active."),
                                fieldWithPath("createdTimestamp").description("The date and time this record was created."),
                                fieldWithPath("lastModifiedTimestamp").description("A date and time this record was last modified.")),
                        responseFields(fieldWithPath("clientId").description("The Client ID that is associated with this Application."),
                                fieldWithPath("roleIds").description("A Set of Role IDs that this Application possess."),
                                fieldWithPath("policies").description(
                                        "A Set of JSON Objects representing the Policies enforced at the AllowedService (enabler) layer for this Application."),
                                fieldWithPath("gatewayPolicies").description(
                                        "A Set of JSON Objects representing the Gateway Policies enforced at the Gateway layer for this Application."),
                                fieldWithPath("allowedServiceIds").description("A Set of AllowedService IDs that this Application has access to."),
                                fieldWithPath("name").description("The name of this Application."),
                                fieldWithPath("email").description("The email of this Application."),
                                fieldWithPath("secret").description("The secret of this Application."),
                                fieldWithPath("accountState").description("The account state of this Application e.g. active."),
                                fieldWithPath("createdTimestamp").description("The date and time this record was created."),
                                fieldWithPath("lastModifiedTimestamp").description("The date and time this record was last modified."),
                                fieldWithPath("_links").description("The returned links."))
                ));

    }

    @Test
    public void testBRead() throws Exception {
        // Read
        mockMvc.perform(get(uri + "/" + testCassandraApplication.getApplicationId()).contentType(contentType)
                                                                                    .content(TestUtils.convertObjectToJsonBytes(testCassandraApplication))
                                                                                    .accept(contentType))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.clientId").value(CLIENT_ID_1))
                .andExpect(jsonPath("$.roleIds").isArray())
                .andExpect(jsonPath("$.roleIds[0]").value(isIn(ROLE_IDS)))
                .andExpect(jsonPath("$.policies").isArray())
                .andExpect(jsonPath("$.policies[0]").value(isIn(POLICIES_STRING)))
                .andExpect(jsonPath("$.gatewayPolicies").isArray())
                .andExpect(jsonPath("$.gatewayPolicies[0]").value(isIn(SERVICE_POLICIES_STRING)))
                .andExpect(jsonPath("$.allowedServiceIds").isArray())
                .andExpect(jsonPath("$.allowedServiceIds[0]").value(isIn(SERVICE_IDS)))
                .andExpect(jsonPath("$.name").value(NAME))
                .andExpect(jsonPath("$.email").value(EMAIL))
                .andExpect(jsonPath("$.secret").exists())
                .andExpect(jsonPath("$.accountState").value(ACCOUNT_STATE))
                .andExpect(jsonPath("$.createdTimestamp").value(dateFormat.format(TestUtils.CREATION_DATE)))
                .andExpect(jsonPath("$.lastModifiedTimestamp").value(dateFormat.format(TestUtils.MODIFIED_DATE)))
                .andDo(document("applications",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(fieldWithPath("clientId").description("The Client ID that is associated with this Application."),
                                fieldWithPath("roleIds").description("A Set of Role IDs that this Application possess."),
                                fieldWithPath("policies").description(
                                        "A Set of JSON Objects representing the Policies enforced at the AllowedService (enabler) layer for this Application."),
                                fieldWithPath("gatewayPolicies").description(
                                        "A Set of JSON Objects representing the Gateway Policies enforced at the Gateway layer for this Application."),
                                fieldWithPath("allowedServiceIds").description("A Set of AllowedService IDs that this Application has access to."),
                                fieldWithPath("name").description("The name of this Application."),
                                fieldWithPath("email").description("The email of this Application."),
                                fieldWithPath("secret").description("The secret of this Application."),
                                fieldWithPath("accountState").description("The account state of this Application e.g. active."),
                                fieldWithPath("createdTimestamp").description("The date and time this record was created."),
                                fieldWithPath("lastModifiedTimestamp").description("The date and time this record was last modified."),
                                fieldWithPath("_links").description("The returned links."))
                ));
    }

    @Test
    public void testCUpdate() throws Exception {
        // Update
        testCassandraApplication.setName(TestUtils.UPDATED_NAME);
        mockMvc.perform(put(uri + "/" + testCassandraApplication.getApplicationId()).contentType(contentType)
                                                                                    .content(TestUtils.convertObjectToJsonBytes(testCassandraApplication))
                                                                                    .accept(contentType))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.clientId").value(CLIENT_ID_1))
                .andExpect(jsonPath("$.roleIds").isArray())
                .andExpect(jsonPath("$.roleIds[0]").value(isIn(ROLE_IDS)))
                .andExpect(jsonPath("$.policies").isArray())
                .andExpect(jsonPath("$.policies[0]").value(isIn(POLICIES_STRING)))
                .andExpect(jsonPath("$.gatewayPolicies").isArray())
                .andExpect(jsonPath("$.gatewayPolicies[0]").value(isIn(SERVICE_POLICIES_STRING)))
                .andExpect(jsonPath("$.allowedServiceIds").isArray())
                .andExpect(jsonPath("$.allowedServiceIds[0]").value(isIn(SERVICE_IDS)))
                .andExpect(jsonPath("$.name").value(TestUtils.UPDATED_NAME))
                .andExpect(jsonPath("$.email").value(EMAIL))
                .andExpect(jsonPath("$.secret").exists())
                .andExpect(jsonPath("$.accountState").value(ACCOUNT_STATE))
                .andExpect(jsonPath("$.createdTimestamp").value(dateFormat.format(TestUtils.CREATION_DATE)))
                .andExpect(jsonPath("$.lastModifiedTimestamp").value(dateFormat.format(TestUtils.MODIFIED_DATE)))
                .andDo(document("applications",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(fieldWithPath("applicationId").description("The ID of this Application."),
                                fieldWithPath("clientId").description("The Client ID that is associated with this Application."),
                                fieldWithPath("roleIds").description("A Set of Role IDs that this Application possess."),
                                fieldWithPath("policies").description(
                                        "A Set of JSON Objects representing the Policies enforced at the AllowedService (enabler) layer for this Application."),
                                fieldWithPath("gatewayPolicies").description(
                                        "A Set of JSON Objects representing the Gateway Policies enforced at the Gateway layer for this Application."),
                                fieldWithPath("allowedServiceIds").description("A Set of AllowedService IDs that this Application has access to."),
                                fieldWithPath("name").description("The name of this Application."),
                                fieldWithPath("email").description("The email of this Application."),
                                fieldWithPath("secret").description("The secret of this Application."),
                                fieldWithPath("accountState").description("The account state of this Application e.g. active."),
                                fieldWithPath("createdTimestamp").description("The date and time this record was created."),
                                fieldWithPath("lastModifiedTimestamp").description("A date and time this record was last modified.")),
                        responseFields(fieldWithPath("clientId").description("The Client ID that is associated with this Application."),
                                fieldWithPath("roleIds").description("A Set of Role IDs that this Application possess."),
                                fieldWithPath("policies").description(
                                        "A Set of JSON Objects representing the Policies enforced at the AllowedService (enabler) layer for this Application."),
                                fieldWithPath("gatewayPolicies").description(
                                        "A Set of JSON Objects representing the Gateway Policies enforced at the Gateway layer for this Application."),
                                fieldWithPath("allowedServiceIds").description("A Set of AllowedService IDs that this Application has access to."),
                                fieldWithPath("name").description("The name of this Application."),
                                fieldWithPath("email").description("The email of this Application."),
                                fieldWithPath("secret").description("The secret of this Application."),
                                fieldWithPath("accountState").description("The account state of this Application e.g. active."),
                                fieldWithPath("createdTimestamp").description("The date and time this record was created."),
                                fieldWithPath("lastModifiedTimestamp").description("The date and time this record was last modified."),
                                fieldWithPath("_links").description("The returned links."))
                ));
    }

    @Test
    public void testDDelete() throws Exception {
        // Delete
        mockMvc.perform(delete(uri + "/" + testCassandraApplication.getApplicationId()).contentType(contentType)
                                                                                       .content(TestUtils.convertObjectToJsonBytes(testCassandraApplication))
                                                                                       .accept(contentType))
                .andDo(print())
                .andExpect(status().isNoContent())
                .andDo(document("applications",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())));
    }

}
