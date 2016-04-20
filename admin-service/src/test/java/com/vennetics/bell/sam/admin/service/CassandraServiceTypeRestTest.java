package com.vennetics.bell.sam.admin.service;

import com.vennetics.bell.sam.admin.service.utils.TestUtils;
import com.vennetics.bell.sam.core.cassandra.helpers.EmbeddedCassandraTestHelper;
import com.vennetics.bell.sam.model.admin.service.entities.cassandra.CassandraServiceType;
import org.cassandraunit.utils.EmbeddedCassandraServerHelper;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
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

import static com.vennetics.bell.sam.admin.service.utils.TestUtils.CASSANDRA_HOST;
import static com.vennetics.bell.sam.admin.service.utils.TestUtils.CREATE_ALLOWED_SERVICES_TABLE;
import static com.vennetics.bell.sam.admin.service.utils.TestUtils.CREATE_APPLICATION_TABLE;
import static com.vennetics.bell.sam.admin.service.utils.TestUtils.CREATE_CLIENT_TABLE;
import static com.vennetics.bell.sam.admin.service.utils.TestUtils.CREATE_POLICY_TYPE_TABLE;
import static com.vennetics.bell.sam.admin.service.utils.TestUtils.CREATE_PROTOCOL_TYPE_TABLE;
import static com.vennetics.bell.sam.admin.service.utils.TestUtils.CREATE_ROLE_TABLE;
import static com.vennetics.bell.sam.admin.service.utils.TestUtils.CREATE_SERVICE_TYPE_TABLE;
import static com.vennetics.bell.sam.admin.service.utils.TestUtils.KEYSPACE_CREATE;
import static com.vennetics.bell.sam.admin.service.utils.TestUtils.NAME;
import static com.vennetics.bell.sam.admin.service.utils.TestUtils.POLICY_IDS;
import static com.vennetics.bell.sam.admin.service.utils.TestUtils.UPDATED_NAME;
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
 * CassandraServiceType Rest endpoint tests
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {AdminServiceSpringBootTestConfig.class})
@Category(IntegrationTest.class)
@WebIntegrationTest({ "server.port:0", "eureka.client.enabled:false",
                "spring.cloud.config.enabled:false", "spring.cloud.config.discovery.enabled:false",
                "security.basic.enabled:false" })
public class CassandraServiceTypeRestTest {

    @ClassRule
    public static final RestDocumentation REST_DOCUMENTATION = new RestDocumentation(
                    "build/generated-snippets");
    private static final Logger logger = LoggerFactory.getLogger(CassandraServiceTypeRestTest.class);
    @Value("${local.server.port}")
    private static int port;
    private final MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
                                                        MediaType.APPLICATION_JSON.getSubtype(),
                                                        Charset.forName("utf8"));
    private MockMvc mockMvc;
    @Autowired
    private WebApplicationContext wac;

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
    }

    @AfterClass
    public static void tearDown() {
        logger.info("Stopping Embedded Cassandra");
        EmbeddedCassandraServerHelper.cleanEmbeddedCassandra();
    }

    @Before
    public void docSetup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(this.wac)
                                 .apply(documentationConfiguration(this.REST_DOCUMENTATION))
                                 .build();
    }

    @Test
    public void testCrudServiceType() throws Exception {
        CassandraServiceType testCassandraServiceType = TestUtils.createDummyCassServiceType();

        // Create
        URI uri = new URI("http://127.0.0.1:" + port + "/" + CassandraServiceType.TABLE_NAME);
        mockMvc.perform(post(uri).contentType(contentType)
                .content(TestUtils.convertObjectToJsonBytes(testCassandraServiceType))
                .accept(contentType))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.name").value(NAME))
                .andExpect(jsonPath("$.policyTypeIds").isArray())
                .andExpect(jsonPath("$.policyTypeIds[0]").value(isIn(POLICY_IDS)))
                .andExpect(jsonPath("$.protocolTypeId").value(TestUtils.PROTOCOL_TYPE_ID))
                .andDo(document("service_types",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(fieldWithPath("serviceTypeId").description("The ID of this Service Type."),
                                fieldWithPath("name").description("The name of this Service Type."),
                                fieldWithPath("policyTypeIds").description("A Set of PolicyTypeIds associated with this Service Type."),
                                fieldWithPath("protocolTypeId").description("The ID of the Protocol Type associated with this ServiceType e.g. NetApi.")),
                        responseFields(fieldWithPath("name").description("The name of this Service Type."),
                                fieldWithPath("policyTypeIds").description("A Set of PolicyTypeIds associated with this Service Type."),
                                fieldWithPath("protocolTypeId").description("The ID of the Protocol Type associated with this Service Type e.g. NetApi."),
                                fieldWithPath("_links").description("The returned links."))
                ));

        // Read
        mockMvc.perform(get(uri + "/" + testCassandraServiceType.getServiceTypeId()).contentType(contentType)
                                                                                    .content(TestUtils.convertObjectToJsonBytes(testCassandraServiceType))
                                                                                    .accept(contentType))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.name").value(NAME))
                .andExpect(jsonPath("$.policyTypeIds").isArray())
                .andExpect(jsonPath("$.policyTypeIds[0]").value(isIn(POLICY_IDS)))
                .andExpect(jsonPath("$.protocolTypeId").value(TestUtils.PROTOCOL_TYPE_ID))
                .andDo(document("service_types",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(fieldWithPath("name").description("The name of this Service Type."),
                                fieldWithPath("policyTypeIds").description("A Set of PolicyTypeIds associated with this Service Type."),
                                fieldWithPath("protocolTypeId").description("The ID of the Protocol Type associated with this ServiceType e.g. NetApi."),
                                fieldWithPath("_links").description("The returned links."))
                ));

        // Update
        testCassandraServiceType.setName(UPDATED_NAME);
        mockMvc.perform(put(uri + "/" + testCassandraServiceType.getServiceTypeId()).contentType(contentType)
                                                                                    .content(TestUtils.convertObjectToJsonBytes(testCassandraServiceType))
                                                                                    .accept(contentType))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.name").value(UPDATED_NAME))
                .andExpect(jsonPath("$.policyTypeIds").isArray())
                .andExpect(jsonPath("$.policyTypeIds[0]").value(isIn(POLICY_IDS)))
                .andExpect(jsonPath("$.protocolTypeId").value(TestUtils.PROTOCOL_TYPE_ID))
                .andDo(document("service_types",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(fieldWithPath("serviceTypeId").description("The ID of this Service Type."),
                                fieldWithPath("name").description("The name of this Service Type."),
                                fieldWithPath("policyTypeIds").description("A Set of PolicyTypeIds associated with this Service Type."),
                                fieldWithPath("protocolTypeId").description("The ID of the Protocol Type associated with this ServiceType e.g. NetApi.")),
                        responseFields(fieldWithPath("name").description("The name of this Service Type."),
                                fieldWithPath("policyTypeIds").description("A Set of PolicyTypeIds associated with this Service Type."),
                                fieldWithPath("protocolTypeId").description("The ID of the Protocol Type associated with this ServiceType e.g. NetApi."),
                                fieldWithPath("_links").description("The returned links."))
                ));

        // Delete
        mockMvc.perform(delete(uri + "/" + testCassandraServiceType.getServiceTypeId()).contentType(contentType)
                                                                                       .content(TestUtils.convertObjectToJsonBytes(testCassandraServiceType))
                                                                                       .accept(contentType))
                .andDo(print())
                .andExpect(status().isNoContent())
                .andDo(document("service_types",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())));
    }

}
