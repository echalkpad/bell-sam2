package com.vennetics.bell.sam.admin.service;

import com.vennetics.bell.sam.admin.service.utils.TestUtils;
import com.vennetics.bell.sam.core.cassandra.helpers.EmbeddedCassandraTestHelper;
import com.vennetics.bell.sam.model.admin.service.entities.cassandra.CassandraRole;
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
import java.util.Arrays;

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
import static com.vennetics.bell.sam.admin.service.utils.TestUtils.DESCRIPTION;
import static com.vennetics.bell.sam.admin.service.utils.TestUtils.KEYSPACE_CREATE;
import static com.vennetics.bell.sam.admin.service.utils.TestUtils.SERVICE_IDS;
import static com.vennetics.bell.sam.admin.service.utils.TestUtils.UPDATED_DESCRIPTION;
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
public class CassandraRoleRestTest {

    @ClassRule
    public static final RestDocumentation REST_DOCUMENTATION = new RestDocumentation(
                    "build/generated-snippets");
    private static final Logger logger = LoggerFactory.getLogger(CassandraRoleRestTest.class);
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
    public void testCrudRole() throws Exception {
        CassandraRole testCassandraRole = TestUtils.createDummyCassRole();

        // Create
        URI uri = new URI("http://127.0.0.1:" + port + "/" + CassandraRole.TABLE_NAME);
        mockMvc.perform(post(uri).contentType(contentType)
                .content(TestUtils.convertObjectToJsonBytes(testCassandraRole))
                .accept(contentType))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.description").value(DESCRIPTION))
                .andExpect(jsonPath("$.serviceTypeIds").isArray())
                .andExpect(jsonPath("$.serviceTypeIds[0]").value(isIn(SERVICE_IDS)))
                .andExpect(jsonPath("$.applicationIds").isArray())
                .andExpect(jsonPath("$.applicationIds[0]").value(
                        isIn(Arrays.asList(APPLICATION_ID_1.toString(), APPLICATION_ID_2.toString()))))
                .andDo(document("roles",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(fieldWithPath("roleId").description("The ID of this Role e.g. admin"),
                                fieldWithPath("description").description("The description for this Role."),
                                fieldWithPath("serviceTypeIds").description("A Set of ServiceTypeIds associated with this Role."),
                                fieldWithPath("applicationIds").description("A Set of ApplicationIds associated with this Role.")),
                        responseFields(fieldWithPath("description").description("The description for this Role."),
                                fieldWithPath("serviceTypeIds").description("A Set of ServiceTypeIds associated with this Role."),
                                fieldWithPath("applicationIds").description("A Set of ApplicationIds associated with this Role."),
                                fieldWithPath("_links").description("The returned links."))
                ));

        // Read
        mockMvc.perform(get(uri + "/" + testCassandraRole.getRoleId()).contentType(contentType)
                                                                      .content(TestUtils.convertObjectToJsonBytes(testCassandraRole))
                                                                      .accept(contentType))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.description").value(TestUtils.DESCRIPTION))
                .andExpect(jsonPath("$.serviceTypeIds").isArray())
                .andExpect(jsonPath("$.serviceTypeIds[0]").value(isIn(SERVICE_IDS)))
                .andExpect(jsonPath("$.applicationIds").isArray())
                .andExpect(jsonPath("$.applicationIds[0]").value(
                    isIn(Arrays.asList(APPLICATION_ID_1.toString(), APPLICATION_ID_2.toString()))))
                .andDo(document("roles",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(fieldWithPath("description").description("The description for this Role."),
                                fieldWithPath("serviceTypeIds").description("A Set of ServiceTypeIds associated with this Role."),
                                fieldWithPath("applicationIds").description("A Set of ApplicationIds associated with this Role."),
                                fieldWithPath("_links").description("The returned links."))
                ));

        // Update
        testCassandraRole.setDescription(UPDATED_DESCRIPTION);
        mockMvc.perform(put(uri + "/" + testCassandraRole.getRoleId()).contentType(contentType)
                                                                      .content(TestUtils.convertObjectToJsonBytes(testCassandraRole))
                                                                      .accept(contentType))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.description").value(UPDATED_DESCRIPTION))
                .andExpect(jsonPath("$.serviceTypeIds").isArray())
                .andExpect(jsonPath("$.serviceTypeIds[0]").value(isIn(SERVICE_IDS)))
                .andExpect(jsonPath("$.applicationIds").isArray())
                .andExpect(jsonPath("$.applicationIds[0]").value(
                    isIn(Arrays.asList(APPLICATION_ID_1.toString(), APPLICATION_ID_2.toString()))))
                .andDo(document("roles",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(fieldWithPath("roleId").description("The ID of this Role e.g. admin"),
                                fieldWithPath("description").description("The description for this Role."),
                                fieldWithPath("serviceTypeIds").description("A Set of ServiceTypeIds associated with this Role."),
                                fieldWithPath("applicationIds").description("A Set of ApplicationIds associated with this Role.")),
                        responseFields(fieldWithPath("description").description("The description for this Role."),
                                fieldWithPath("serviceTypeIds").description("A Set of ServiceTypeIds associated with this Role."),
                                fieldWithPath("applicationIds").description("A Set of ApplicationIds associated with this Role."),
                                fieldWithPath("_links").description("The returned links."))
                ));

        // Delete
        mockMvc.perform(delete(uri + "/" + testCassandraRole.getRoleId()).contentType(contentType)
                                                                         .content(TestUtils.convertObjectToJsonBytes(testCassandraRole))
                                                                         .accept(contentType))
                .andDo(print())
                .andExpect(status().isNoContent())
                .andDo(document("roles",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())));
    }

}
