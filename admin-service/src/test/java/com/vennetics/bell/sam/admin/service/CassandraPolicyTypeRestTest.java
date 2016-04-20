package com.vennetics.bell.sam.admin.service;

import com.vennetics.bell.sam.admin.service.utils.TestUtils;
import com.vennetics.bell.sam.core.cassandra.helpers.EmbeddedCassandraTestHelper;
import com.vennetics.bell.sam.model.admin.service.entities.cassandra.CassandraPolicyType;
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
import static com.vennetics.bell.sam.admin.service.utils.TestUtils.SERVICE_IDS;
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
 * CassandraPolicyType Rest endpoint tests
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {AdminServiceSpringBootTestConfig.class})
@Category(IntegrationTest.class)
@WebIntegrationTest({ "server.port:0", "eureka.client.enabled:false",
                "spring.cloud.config.enabled:false", "spring.cloud.config.discovery.enabled:false",
                "security.basic.enabled:false" })
public class CassandraPolicyTypeRestTest {

    @ClassRule
    public static final RestDocumentation REST_DOCUMENTATION = new RestDocumentation(
                    "build/generated-snippets");
    private static final Logger logger = LoggerFactory.getLogger(CassandraPolicyTypeRestTest.class);
    @Value("${local.server.port}")
    private static int port;
    private final MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
                                                        MediaType.APPLICATION_JSON.getSubtype(),
                                                        Charset.forName("utf8"));
    private MockMvc mockMvc;
    @Autowired private WebApplicationContext wac;

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
    public void testCrudPolicyType() throws Exception {
        CassandraPolicyType testCassandraPolicyType = TestUtils.createDummyCassPolicyType();

        // Create
        URI uri = new URI("http://127.0.0.1:" + port + "/" + CassandraPolicyType.TABLE_NAME);
        mockMvc.perform(post(uri).contentType(contentType)
                .content(TestUtils.convertObjectToJsonBytes(testCassandraPolicyType))
                .accept(contentType))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.name").value(NAME))
                .andExpect(jsonPath("$.policyOrder").value(TestUtils.POLICY_ORDER))
                .andExpect(jsonPath("$.serviceTypeIds").isArray())
                .andExpect(jsonPath("$.serviceTypeIds[0]").value(SERVICE_IDS.toArray()[0].toString()))
                .andDo(document("policy_types",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(fieldWithPath("policyTypeId").description("The ID of this Policy Type."),
                                fieldWithPath("name").description("The name of this Policy Type."),
                                fieldWithPath("policyOrder").description("The order in which this policy should be enforced."),
                                fieldWithPath("serviceTypeIds").description("A Set of ServiceTypeIds associated with this Policy Type.")),
                        responseFields(fieldWithPath("name").description("The name of this Policy Type."),
                                fieldWithPath("policyOrder").description("The order in which this policy should be enforced."),
                                fieldWithPath("serviceTypeIds").description("A Set of ServiceTypeIds associated with this Policy Type."),
                                fieldWithPath("_links").description("The returned links."))
                ));

        // Read
        mockMvc.perform(get(uri + "/" + testCassandraPolicyType.getPolicyTypeId()).contentType(contentType)
                                                                                  .content(TestUtils.convertObjectToJsonBytes(testCassandraPolicyType))
                                                                                  .accept(contentType))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.name").value(NAME))
                .andExpect(jsonPath("$.policyOrder").value(TestUtils.POLICY_ORDER))
                .andExpect(jsonPath("$.serviceTypeIds").isArray())
                .andExpect(jsonPath("$.serviceTypeIds[0]").value(SERVICE_IDS.toArray()[0].toString()))
                .andDo(document("policy_types",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(fieldWithPath("name").description("The name of this Policy Type."),
                                fieldWithPath("policyOrder").description("The order in which this policy should be enforced."),
                                fieldWithPath("serviceTypeIds").description("A Set of ServiceTypeIds associated with this Policy Type."),
                                fieldWithPath("_links").description("The returned links."))
                ));

        // Update
        testCassandraPolicyType.setName(TestUtils.UPDATED_NAME);
        mockMvc.perform(put(uri + "/" + testCassandraPolicyType.getPolicyTypeId()).contentType(contentType)
                                                                                  .content(TestUtils.convertObjectToJsonBytes(testCassandraPolicyType))
                                                                                  .accept(contentType))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.name").value(TestUtils.UPDATED_NAME))
                .andExpect(jsonPath("$.policyOrder").value(TestUtils.POLICY_ORDER))
                .andExpect(jsonPath("$.serviceTypeIds").isArray())
                .andExpect(jsonPath("$.serviceTypeIds[0]").value(SERVICE_IDS.toArray()[0].toString()))
                .andDo(document("policy_types",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(fieldWithPath("policyTypeId").description("The ID of this Policy Type."),
                                fieldWithPath("name").description("The name of this Policy Type."),
                                fieldWithPath("policyOrder").description("The order in which this policy should be enforced."),
                                fieldWithPath("serviceTypeIds").description("A Set of ServiceTypeIds associated with this Policy Type.")),
                        responseFields(fieldWithPath("name").description("The name of this Policy Type."),
                                fieldWithPath("policyOrder").description("The order in which this policy should be enforced."),
                                fieldWithPath("serviceTypeIds").description("A Set of ServiceTypeIds associated with this Policy Type."),
                                fieldWithPath("_links").description("The returned links."))
                ));

        // Delete
        mockMvc.perform(delete(uri + "/" + testCassandraPolicyType.getPolicyTypeId()).contentType(contentType)
                                                                                     .content(TestUtils.convertObjectToJsonBytes(testCassandraPolicyType))
                                                                                     .accept(contentType))
                .andDo(print())
                .andExpect(status().isNoContent())
                .andDo(document("policy_types",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())));
    }

}
