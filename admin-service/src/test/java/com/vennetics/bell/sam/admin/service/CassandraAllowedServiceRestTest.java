package com.vennetics.bell.sam.admin.service;

import com.vennetics.bell.sam.admin.service.utils.TestUtils;
import com.vennetics.bell.sam.core.cassandra.helpers.EmbeddedCassandraTestHelper;
import com.vennetics.bell.sam.model.admin.service.entities.cassandra.CassandraAllowedService;
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
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.TimeZone;

import static com.vennetics.bell.sam.admin.service.utils.TestUtils.APPLICATION_ID_1;
import static com.vennetics.bell.sam.admin.service.utils.TestUtils.APPLICATION_ID_2;
import static com.vennetics.bell.sam.admin.service.utils.TestUtils.CASSANDRA_HOST;
import static com.vennetics.bell.sam.admin.service.utils.TestUtils.CREATE_ALLOWED_SERVICES_TABLE;
import static com.vennetics.bell.sam.admin.service.utils.TestUtils.CREATE_APPLICATION_TABLE;
import static com.vennetics.bell.sam.admin.service.utils.TestUtils.CREATE_CLIENT_TABLE;
import static com.vennetics.bell.sam.admin.service.utils.TestUtils.CREATE_POLICY_TYPE_TABLE;
import static com.vennetics.bell.sam.admin.service.utils.TestUtils.CREATE_PROTOCOL_TYPE_TABLE;
import static com.vennetics.bell.sam.admin.service.utils.TestUtils.CREATE_ROLE_TABLE;
import static com.vennetics.bell.sam.admin.service.utils.TestUtils.CREATE_SERVICE_TYPE_TABLE;
import static com.vennetics.bell.sam.admin.service.utils.TestUtils.KEYSPACE_CREATE;
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
 * CassandraAllowedService Rest endpoint tests
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = AdminServiceSpringBootTestConfig.class)
@Category(IntegrationTest.class)
@WebIntegrationTest({ "server.port:0", "eureka.client.enabled:false",
                "spring.cloud.config.enabled:false", "spring.cloud.config.discovery.enabled:false",
                "security.basic.enabled:false" })
public class CassandraAllowedServiceRestTest {

    @ClassRule
    public static final RestDocumentation REST_DOCUMENTATION = new RestDocumentation(
                    "build/generated-snippets");
    private static final Logger logger = LoggerFactory.getLogger(CassandraAllowedServiceRestTest.class);
    @Value("${local.server.port}")
    private static int port;
    private final MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
                                                        MediaType.APPLICATION_JSON.getSubtype(),
                                                        Charset.forName("utf8"));
    private MockMvc mockMvc;
    @Autowired
    private WebApplicationContext wac;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

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
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        mockMvc = MockMvcBuilders.webAppContextSetup(this.wac)
                                 .apply(documentationConfiguration(this.REST_DOCUMENTATION))
                                 .build();
    }

    @Test
    public void testCrudAllowedService() throws Exception {
        CassandraAllowedService testCassandraAllowedService = TestUtils.createDummyCassAllowedService();

        // Create
        URI uri = new URI("http://127.0.0.1:" + port + "/" + CassandraAllowedService.TABLE_NAME);
        mockMvc.perform(post(uri).contentType(contentType)
                .content(TestUtils.convertObjectToJsonBytes(testCassandraAllowedService))
                .accept(contentType))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.applicationIds").isArray())
                .andExpect(jsonPath("$.applicationIds[0]").value(
                        isIn(Arrays.asList(APPLICATION_ID_1.toString(), APPLICATION_ID_2.toString()))))
                .andExpect(jsonPath("$.createdTimestamp").value(dateFormat.format(TestUtils.CREATION_DATE)))
                .andExpect(jsonPath("$.lastModifiedTimestamp").value(dateFormat.format(TestUtils.MODIFIED_DATE)))
                .andDo(document("allowed_services",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(fieldWithPath("allowedServiceId").description("The ID of this AllowedService 1:1 mapping to the ServiceTypeId."),
                                fieldWithPath("applicationIds").description("A Set of Applications that are allowed to use this service."),
                                fieldWithPath("createdTimestamp").description("The date and time this record was created."),
                                fieldWithPath("lastModifiedTimestamp").description("A date and time this record was last modified.")),
                        responseFields(fieldWithPath("applicationIds").description("A Set of Applications that are allowed to use this service."),
                                fieldWithPath("createdTimestamp").description("The date and time this record was created."),
                                fieldWithPath("lastModifiedTimestamp").description("The date and time this record was last modified."),
                                fieldWithPath("_links").description("The returned links."))
                ));

        // Read
        mockMvc.perform(get(uri + "/" + testCassandraAllowedService.getAllowedServiceId()).contentType(contentType)
                                                                                          .content(TestUtils.convertObjectToJsonBytes(testCassandraAllowedService))
                                                                                          .accept(contentType))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.applicationIds").isArray())
                .andExpect(jsonPath("$.applicationIds[0]").value(
                        isIn(Arrays.asList(APPLICATION_ID_1.toString(), APPLICATION_ID_2.toString()))))
                .andExpect(jsonPath("$.createdTimestamp").value(dateFormat.format(TestUtils.CREATION_DATE)))
                .andExpect(jsonPath("$.lastModifiedTimestamp").value(dateFormat.format(TestUtils.MODIFIED_DATE)))
                .andDo(document("allowed_services",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(fieldWithPath("applicationIds").description("A Set of Applications that are allowed to use this service."),
                                fieldWithPath("createdTimestamp").description("The date and time this record was created."),
                                fieldWithPath("lastModifiedTimestamp").description("A date and time this record was last modified."),
                                fieldWithPath("_links").description("The returned links."))
                ));

        // Update
        testCassandraAllowedService.setApplicationIds(TestUtils.UPDATED_TEST_APPLICATIONS);
        mockMvc.perform(put(uri + "/" + testCassandraAllowedService.getAllowedServiceId()).contentType(contentType)
                                                                                          .content(TestUtils.convertObjectToJsonBytes(testCassandraAllowedService))
                                                                                          .accept(contentType))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.applicationIds").isArray())
                .andExpect(jsonPath("$.applicationIds[0]").value(
                        isIn(Arrays.asList(TestUtils.UPDATED_TEST_APPLICATIONS.toArray()[0].toString(), TestUtils.UPDATED_TEST_APPLICATIONS.toArray()[1].toString()))))
                .andExpect(jsonPath("$.createdTimestamp").value(dateFormat.format(TestUtils.CREATION_DATE)))
                .andExpect(jsonPath("$.lastModifiedTimestamp").value(dateFormat.format(TestUtils.MODIFIED_DATE)))
                .andDo(document("allowed_services",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(fieldWithPath("allowedServiceId").description("The ID of this AllowedService 1:1 mapping to the ServiceTypeId."),
                                fieldWithPath("applicationIds").description("A Set of Applications that are allowed to use this service."),
                                fieldWithPath("createdTimestamp").description("The date and time this record was created."),
                                fieldWithPath("lastModifiedTimestamp").description("A date and time this record was last modified.")),
                        responseFields(fieldWithPath("applicationIds").description("A Set of Applications that are allowed to use this service."),
                                fieldWithPath("createdTimestamp").description("The date and time this record was created."),
                                fieldWithPath("lastModifiedTimestamp").description("The date and time this record was last modified."),
                                fieldWithPath("_links").description("The returned links."))
                ));

        // Delete
        mockMvc.perform(delete(uri + "/" + testCassandraAllowedService.getAllowedServiceId()).contentType(contentType)
                                                                                             .content(TestUtils.convertObjectToJsonBytes(testCassandraAllowedService))
                                                                                             .accept(contentType))
                .andDo(print())
                .andExpect(status().isNoContent())
                .andDo(document("allowed_services",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())));
    }

}
