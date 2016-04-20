package com.vennetics.bell.sam.admin.service.controllers;

import com.datastax.driver.core.utils.UUIDs;
import com.vennetics.bell.sam.admin.service.api.IApplicationAdminService;
import com.vennetics.bell.sam.admin.service.utils.TestUtils;
import com.vennetics.bell.sam.model.admin.service.entities.cassandra.CassandraApplication;
import com.vennetics.microservices.common.api.model.admin.v1.application.ApplicationAllowedService;
import com.vennetics.microservices.common.api.model.admin.v1.application.ApplicationUserDetails;
import com.vennetics.microservices.common.api.model.admin.v1.application.ApplicationUserDetailsList;
import com.vennetics.microservices.common.api.model.admin.v1.application.PolicyType;
import com.vennetics.microservices.common.api.model.admin.v1.application.PolicyTypeList;
import com.vennetics.microservices.common.api.model.admin.v1.application.ProtocolType;
import com.vennetics.microservices.common.api.model.admin.v1.application.ProtocolTypeList;
import com.vennetics.microservices.common.api.model.admin.v1.application.Role;
import com.vennetics.microservices.common.api.model.admin.v1.application.RoleList;
import com.vennetics.microservices.common.api.model.admin.v1.application.ServiceType;
import com.vennetics.microservices.common.api.model.admin.v1.application.ServiceTypeList;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentation;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import rx.Observable;

import java.nio.charset.Charset;
import java.util.ArrayList;

import static com.vennetics.bell.sam.admin.service.controllers.ApplicationAdminServiceController.PATH_APPLICATION;
import static com.vennetics.bell.sam.admin.service.controllers.ApplicationAdminServiceController.PATH_APPLICATIONS;
import static com.vennetics.bell.sam.admin.service.controllers.ApplicationAdminServiceController.PATH_POLICIES;
import static com.vennetics.bell.sam.admin.service.controllers.ApplicationAdminServiceController.PATH_POLICY_TYPES;
import static com.vennetics.bell.sam.admin.service.controllers.ApplicationAdminServiceController.PATH_PROTOCOL_TYPES;
import static com.vennetics.bell.sam.admin.service.controllers.ApplicationAdminServiceController.PATH_ROLES;
import static com.vennetics.bell.sam.admin.service.controllers.ApplicationAdminServiceController.PATH_SERVICES;
import static com.vennetics.bell.sam.admin.service.controllers.ApplicationAdminServiceController.PATH_SERVICE_TYPES;
import static com.vennetics.bell.sam.admin.service.controllers.ApplicationAdminServiceController.VERSION_1;
import static com.vennetics.bell.sam.admin.service.utils.TestUtils.ACCOUNT_STATE;
import static com.vennetics.bell.sam.admin.service.utils.TestUtils.ALLOWED_SERVICE;
import static com.vennetics.bell.sam.admin.service.utils.TestUtils.APPLICATION_ID_1;
import static com.vennetics.bell.sam.admin.service.utils.TestUtils.APPLICATION_POLICY;
import static com.vennetics.bell.sam.admin.service.utils.TestUtils.CLIENT_ID_1;
import static com.vennetics.bell.sam.admin.service.utils.TestUtils.DESCRIPTION;
import static com.vennetics.bell.sam.admin.service.utils.TestUtils.EMAIL;
import static com.vennetics.bell.sam.admin.service.utils.TestUtils.FULL_APPLICATION_USER_DETAILS;
import static com.vennetics.bell.sam.admin.service.utils.TestUtils.NAME;
import static com.vennetics.bell.sam.admin.service.utils.TestUtils.POLICY_ID;
import static com.vennetics.bell.sam.admin.service.utils.TestUtils.POLICY_ORDER;
import static com.vennetics.bell.sam.admin.service.utils.TestUtils.PROTOCOL_TYPE_ID;
import static com.vennetics.bell.sam.admin.service.utils.TestUtils.RETURNED_APPLICATION_USER_DETAILS;
import static com.vennetics.bell.sam.admin.service.utils.TestUtils.SECRET;
import static com.vennetics.bell.sam.admin.service.utils.TestUtils.SERVICE_ID;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;



/**
 * ApplicationAdminServiceController Test Class
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ApplicationAdminServiceControllerTestConfig.class })
@WebAppConfiguration
public class ApplicationAdminServiceControllerTest {
    private static final Logger logger = LoggerFactory.getLogger(ApplicationAdminServiceControllerTest.class);

    @ClassRule
    public static final RestDocumentation REST_DOCUMENTATION = new RestDocumentation("build/generated-snippets");

    private static final MediaType JSON_CONTENT = new MediaType(MediaType.APPLICATION_JSON.getType(),
                                                                MediaType.APPLICATION_JSON.getSubtype(),
                                                                Charset.forName("utf8"));


    private CassandraApplication cassandraApplication1;

    @Autowired
    private IApplicationAdminService mockApplicationAdminService;

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;


    @Before
    public void setup() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                                 .apply(documentationConfiguration(REST_DOCUMENTATION))
                                 .build();

        cassandraApplication1 = TestUtils.createDummyCassApplication();
    }


    @Test
    public void testCreateApplication() throws Exception {
        final ApplicationUserDetails applicationToCreate = new ApplicationUserDetails(CLIENT_ID_1,
                                                                           NAME,
                                                                           EMAIL,
                                                                           SECRET,
                                                                           ACCOUNT_STATE,
                                                                           null);

        final ApplicationUserDetails returnedApplication = new ApplicationUserDetails(CLIENT_ID_1,
                                                                NAME,
                                                                EMAIL,
                                                                ACCOUNT_STATE,
                                                                UUIDs.timeBased());

        when(mockApplicationAdminService.createApplication(applicationToCreate)).thenReturn(Observable.just(returnedApplication));

        final MvcResult mvcResult = mockMvc.perform(post("/" + VERSION_1 + "/" + PATH_APPLICATIONS + "/" + PATH_APPLICATION)
                                                        .contentType(JSON_CONTENT)
                                                        .content(TestUtils.convertObjectToJsonString(applicationToCreate))
                                                        .accept(JSON_CONTENT))
                                           .andDo(print())
                                           .andExpect(request().asyncStarted())
                                           .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
               .andDo(print())
               .andExpect(status().isCreated())
               .andExpect(content().contentType(JSON_CONTENT))
               .andExpect(jsonPath("$.applicationUserDetails.clientId").value(returnedApplication.getClientId()))
               .andExpect(jsonPath("$.applicationUserDetails.name").value(returnedApplication.getName()))
               .andExpect(jsonPath("$.applicationUserDetails.email").value(returnedApplication.getEmail()))
               .andExpect(jsonPath("$.applicationUserDetails.state").value(returnedApplication.getState()))
               .andExpect(jsonPath("$.applicationUserDetails.applicationId").value(returnedApplication.getApplicationId().toString()))
               .andDo(document("createApplication",
                               preprocessRequest(prettyPrint()),
                               preprocessResponse(prettyPrint()),
                               requestFields(fieldWithPath("applicationUserDetails.clientId").description("The Client ID that is associated with this Application."),
                                             fieldWithPath("applicationUserDetails.name").description("The name of this Application."),
                                             fieldWithPath("applicationUserDetails.email").description("The email of this Application."),
                                             fieldWithPath("applicationUserDetails.secret").description("The secret of this Application."),
                                             fieldWithPath("applicationUserDetails.state").description("The account state of this Application e.g. ACTIVE.")),
                               responseHeaders(headerWithName("Location").description("The created resource header location.")),
                               responseFields(fieldWithPath("applicationUserDetails.clientId").description("The Client ID that is associated with this Application."),
                                              fieldWithPath("applicationUserDetails.applicationId").description("The Application ID of this Application."),
                                              fieldWithPath("applicationUserDetails.name").description("The name of this Application."),
                                              fieldWithPath("applicationUserDetails.email").description("The email of this Application."),
                                              fieldWithPath("applicationUserDetails.state").description("The account state of this Application e.g. ACTIVE."))

               ));
    }

    @Test
    public void testGetAllApplications() throws Exception {
        final ApplicationUserDetails application1 = RETURNED_APPLICATION_USER_DETAILS;
        final ApplicationUserDetails application2 = RETURNED_APPLICATION_USER_DETAILS;


        ArrayList<ApplicationUserDetails> applications = new ArrayList<>();
        applications.add(application1);
        applications.add(application2);

        ApplicationUserDetailsList applicationList = new ApplicationUserDetailsList(applications);
        when(mockApplicationAdminService.getAllApplications()).thenReturn(Observable.just(applicationList));

        final MvcResult mvcResult = mockMvc.perform(get("/" + VERSION_1 + "/" + PATH_APPLICATIONS + "/" + PATH_APPLICATION)
                                                        .contentType(JSON_CONTENT)
                                                        .accept(JSON_CONTENT))
                                           .andDo(print())
                                           .andExpect(request().asyncStarted())
                                           .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
               .andDo(print())
               .andExpect(status().isOk())
               .andExpect(content().contentType(JSON_CONTENT))
               .andExpect(jsonPath("$.applicationList.applications[0].clientId").value(application1.getClientId()))
               .andExpect(jsonPath("$.applicationList.applications[0].name").value(application1.getName()))
               .andExpect(jsonPath("$.applicationList.applications[0].email").value(application1.getEmail()))
               .andExpect(jsonPath("$.applicationList.applications[0].state").value(application1.getState()))
               .andExpect(jsonPath("$.applicationList.applications[0].applicationId").value(application1.getApplicationId().toString()))
               .andExpect(jsonPath("$.applicationList.applications[0].roles").isArray())
               .andExpect(jsonPath("$.applicationList.applications[0].roles[0]").value(application1.getRoles().toArray()[0].toString()))
               .andExpect(jsonPath("$.applicationList.applications[0].applicationPolicies").isArray())
               .andExpect(jsonPath("$.applicationList.applications[0].allowedServices").isArray())
               .andExpect(jsonPath("$.applicationList.applications[1].clientId").value(application2.getClientId()))
               .andExpect(jsonPath("$.applicationList.applications[1].name").value(application2.getName()))
               .andExpect(jsonPath("$.applicationList.applications[1].email").value(application2.getEmail()))
               .andExpect(jsonPath("$.applicationList.applications[1].state").value(application2.getState()))
               .andExpect(jsonPath("$.applicationList.applications[1].applicationId").value(application2.getApplicationId().toString()))
               .andExpect(jsonPath("$.applicationList.applications[1].roles").isArray())
               .andExpect(jsonPath("$.applicationList.applications[1].roles[0]").value(application2.getRoles().toArray()[0].toString()))
               .andExpect(jsonPath("$.applicationList.applications[1].applicationPolicies").isArray())
               .andExpect(jsonPath("$.applicationList.applications[1].allowedServices").isArray())
               .andDo(document("getAllApplications",
                               preprocessRequest(prettyPrint()),
                               preprocessResponse(prettyPrint()),
                               responseFields(fieldWithPath(".applicationList.applications[0].clientId").description("The Client ID that is associated with this Application."),
                                              fieldWithPath(".applicationList.applications[0].applicationId").description("The Application ID of this Application."),
                                              fieldWithPath(".applicationList.applications[0].roles").description("A Set of Roles that this Application possess."),
                                              fieldWithPath(".applicationList.applications[0].applicationPolicies")
                                                  .description("A Set of JSON Objects representing the Applications Policies."),
                                              fieldWithPath(".applicationList.applications[0].allowedServices")
                                                  .description("A Set of Allowed Services that this Application has access to."),
                                              fieldWithPath(".applicationList.applications[0].name").description("The name of this Application."),
                                              fieldWithPath(".applicationList.applications[0].email").description("The email of this Application."),
                                              fieldWithPath(".applicationList.applications[0].state").description("The account state of this Application e.g. ACTIVE."))
               ));
    }



    @Test
    public void testGetApplicationById() throws Exception {
        ApplicationUserDetails application1 = RETURNED_APPLICATION_USER_DETAILS;
        when(mockApplicationAdminService.getApplicationById(application1.getApplicationId())).thenReturn(Observable.just(application1));

        final MvcResult mvcResult = mockMvc.perform(get("/" + VERSION_1 + "/" + PATH_APPLICATIONS + "/" + PATH_APPLICATION
                                                            + "/" + cassandraApplication1.getApplicationId())
                                           .contentType(JSON_CONTENT)
                                           .accept(JSON_CONTENT))
                                           .andDo(print())
                                           .andExpect(request().asyncStarted())
                                           .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
               .andDo(print())
               .andExpect(status().isOk())
               .andExpect(content().contentType(JSON_CONTENT))
               .andExpect(jsonPath("$.applicationUserDetails.clientId").value(application1.getClientId()))
               .andExpect(jsonPath("$.applicationUserDetails.name").value(application1.getName()))
               .andExpect(jsonPath("$.applicationUserDetails.email").value(application1.getEmail()))
               .andExpect(jsonPath("$.applicationUserDetails.state").value(application1.getState()))
               .andExpect(jsonPath("$.applicationUserDetails.applicationId").value(application1.getApplicationId().toString()))
               .andExpect(jsonPath("$.applicationUserDetails.roles").isArray())
               .andExpect(jsonPath("$.applicationUserDetails.roles[0]").value(application1.getRoles().toArray()[0].toString()))
               .andExpect(jsonPath("$.applicationUserDetails.applicationPolicies").isArray())
               .andExpect(jsonPath("$.applicationUserDetails.allowedServices").isArray())
               .andDo(document("getApplicationById",
                               preprocessRequest(prettyPrint()),
                               preprocessResponse(prettyPrint()),
                               responseFields(fieldWithPath("applicationUserDetails.clientId").description("The Client ID that is associated with this Application."),
                                              fieldWithPath("applicationUserDetails.applicationId").description("The Application ID of this Application."),
                                              fieldWithPath("applicationUserDetails.roles").description("A Set of Roles that this Application possess."),
                                              fieldWithPath("applicationUserDetails.applicationPolicies")
                                                  .description("A Set of JSON Objects representing the Applications Policies."),
                                              fieldWithPath("applicationUserDetails.allowedServices")
                                                  .description("A Set of Allowed Services that this Application has access to."),
                                              fieldWithPath("applicationUserDetails.name").description("The name of this Application."),
                                              fieldWithPath("applicationUserDetails.email").description("The email of this Application."),
                                              fieldWithPath("applicationUserDetails.state").description("The account state of this Application e.g. ACTIVE."))
               ));
    }

    @Test
    public void testDeleteApplication() throws Exception {
        ApplicationUserDetails application = FULL_APPLICATION_USER_DETAILS;
        when(mockApplicationAdminService.deleteApplication(application.getApplicationId())).thenReturn(Observable.just(null));

        final MvcResult mvcResult = mockMvc.perform(delete("/" + VERSION_1 + "/" + PATH_APPLICATIONS + "/" + PATH_APPLICATION
                                                            + "/" + application.getApplicationId())
                                                        .contentType(JSON_CONTENT))
                                                        .andDo(print())
                                                        .andExpect(request().asyncStarted())
                                                        .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
               .andDo(print())
               .andExpect(status().isNoContent())
               .andDo(document("deleteApplication",
                               preprocessRequest(prettyPrint()),
                               preprocessResponse(prettyPrint())
               ));
    }

    @Test
    public void testUpdateApplication() throws Exception {
        final ApplicationUserDetails applicationToUpdate = new ApplicationUserDetails(CLIENT_ID_1,
                                                                NAME,
                                                                EMAIL,
                                                                SECRET,
                                                                ACCOUNT_STATE,
                                                                APPLICATION_ID_1);

        final ApplicationUserDetails application = RETURNED_APPLICATION_USER_DETAILS;
        when(mockApplicationAdminService.updateApplication(application.getApplicationId(), applicationToUpdate))
            .thenReturn(Observable.just(application));

        final MvcResult mvcResult = mockMvc.perform(put("/" + VERSION_1 + "/" + PATH_APPLICATIONS + "/" + PATH_APPLICATION
                                                             + "/" + application.getApplicationId())
                                                        .contentType(JSON_CONTENT)
                                                        .content(TestUtils.convertObjectToJsonString(applicationToUpdate))
                                                        .accept(JSON_CONTENT))
                                           .andDo(print())
                                           .andExpect(request().asyncStarted())
                                           .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
               .andDo(print())
               .andExpect(status().isOk())
               .andExpect(content().contentType(JSON_CONTENT))
               .andExpect(jsonPath("$.applicationUserDetails.clientId").value(application.getClientId()))
               .andExpect(jsonPath("$.applicationUserDetails.name").value(application.getName()))
               .andExpect(jsonPath("$.applicationUserDetails.email").value(application.getEmail()))
               .andExpect(jsonPath("$.applicationUserDetails.state").value(application.getState()))
               .andExpect(jsonPath("$.applicationUserDetails.applicationId").value(application.getApplicationId().toString()))
               .andExpect(jsonPath("$.applicationUserDetails.roles").isArray())
               .andExpect(jsonPath("$.applicationUserDetails.roles[0]").value(application.getRoles().toArray()[0].toString()))
               .andExpect(jsonPath("$.applicationUserDetails.applicationPolicies").isArray())
               .andExpect(jsonPath("$.applicationUserDetails.allowedServices").isArray())
               .andDo(document("updateApplication",
                               preprocessRequest(prettyPrint()),
                               preprocessResponse(prettyPrint()),
                               requestFields(fieldWithPath("applicationUserDetails.clientId").description("The Client ID that is associated with this Application."),
                                             fieldWithPath("applicationUserDetails.name").description("The name of this Application."),
                                             fieldWithPath("applicationUserDetails.email").description("The email of this Application."),
                                             fieldWithPath("applicationUserDetails.secret").description("The secret of this Application."),
                                             fieldWithPath("applicationUserDetails.applicationId").description("The Application ID of this Application."),
                                             fieldWithPath("applicationUserDetails.state").description("The account state of this Application e.g. ACTIVE.")),
                               responseFields(fieldWithPath("applicationUserDetails.clientId").description("The Client ID that is associated with this Application."),
                                              fieldWithPath("applicationUserDetails.applicationId").description("The Application ID of this Application."),
                                              fieldWithPath("applicationUserDetails.roles").description("A Set of Roles that this Application possess."),
                                              fieldWithPath("applicationUserDetails.applicationPolicies")
                                                  .description("A Set of JSON Objects representing the Applications Policies."),
                                              fieldWithPath("applicationUserDetails.allowedServices")
                                                  .description("A Set of Allowed Services that this Application has access to."),
                                              fieldWithPath("applicationUserDetails.name").description("The name of this Application."),
                                              fieldWithPath("applicationUserDetails.email").description("The email of this Application."),
                                              fieldWithPath("applicationUserDetails.state").description("The account state of this Application e.g. ACTIVE."))
               ));
    }

    @Test
    public void testAddService() throws Exception {
        final ApplicationAllowedService allowedService = ALLOWED_SERVICE;
        final ApplicationUserDetails application = FULL_APPLICATION_USER_DETAILS;
        when(mockApplicationAdminService.addService(
            application.getApplicationId(), allowedService.getServiceTypeId())).thenReturn(Observable.just(allowedService));

        final MvcResult mvcResult = mockMvc.perform(put("/" + VERSION_1 + "/" + PATH_APPLICATIONS + "/" + PATH_APPLICATION
                                                            + "/" + application.getApplicationId()
                                                            + "/" + PATH_SERVICES
                                                            + "/" + allowedService.getServiceTypeId())
                                                        .contentType(JSON_CONTENT)
                                                        .accept(JSON_CONTENT))
                                           .andDo(print())
                                           .andExpect(request().asyncStarted())
                                           .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
               .andDo(print())
               .andExpect(status().isOk())
               .andExpect(content().contentType(JSON_CONTENT))
               .andExpect(jsonPath("$.allowedService.serviceTypeId").value(allowedService.getServiceTypeId()))
               .andExpect(jsonPath("$.allowedService.policies").isArray())
               .andDo(document("addService",
                               preprocessRequest(prettyPrint()),
                               preprocessResponse(prettyPrint()),
                               responseFields(fieldWithPath("allowedService.serviceTypeId").description("The ID of this AllowedService."),
                                              fieldWithPath("allowedService.policies").description("A Set of Policies that are associated with this service."))
               ));
    }

    @Test
    public void testDeleteService() throws Exception {

        when(mockApplicationAdminService.deleteService(
            APPLICATION_ID_1, SERVICE_ID)).thenReturn(Observable.just(null));

        final MvcResult mvcResult = mockMvc.perform(delete("/" + VERSION_1 + "/" + PATH_APPLICATIONS + "/" + PATH_APPLICATION
                                                            + "/" + APPLICATION_ID_1
                                                            + "/" + PATH_SERVICES
                                                            + "/" + SERVICE_ID)
                                                        .contentType(JSON_CONTENT)
                                                        .accept(JSON_CONTENT))
                                           .andDo(print())
                                           .andExpect(request().asyncStarted())
                                           .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
               .andDo(print())
               .andExpect(status().isNoContent())
               .andDo(document("deleteService",
                               preprocessRequest(prettyPrint()),
                               preprocessResponse(prettyPrint())
               ));
    }


    @Test
    public void testAddRole() throws Exception {
        final Role role = TestUtils.createDummyRole();
        when(mockApplicationAdminService.addRole(
            cassandraApplication1.getApplicationId(), role.getRoleId())).thenReturn(Observable.just(role));

        final MvcResult mvcResult = mockMvc.perform(put("/" + VERSION_1 + "/" + PATH_APPLICATIONS + "/" + PATH_APPLICATION
                                                            + "/" + cassandraApplication1.getApplicationId()
                                                            + "/" + PATH_ROLES
                                                            + "/" + role.getRoleId())
                                                        .contentType(JSON_CONTENT)
                                                        .accept(JSON_CONTENT))
                                           .andDo(print())
                                           .andExpect(request().asyncStarted())
                                           .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
               .andDo(print())
               .andExpect(status().isOk())
               .andExpect(content().contentType(JSON_CONTENT))
               .andExpect(jsonPath("$.role.roleId").value(role.getRoleId()))
               .andExpect(jsonPath("$.role.description").value(role.getDescription()))
               .andExpect(jsonPath("$.role.serviceTypeIds").isArray())
               .andExpect(jsonPath("$.role.applicationIds").isArray())
               .andDo(document("addRole",
                               preprocessRequest(prettyPrint()),
                               preprocessResponse(prettyPrint()),
                               responseFields(fieldWithPath("role.roleId").description("The ID of this Role e.g. admin"),
                                              fieldWithPath("role.description").description("The description for this Role."),
                                              fieldWithPath("role.serviceTypeIds").description("A Set of ServiceTypeIds associated with this Role."),
                                              fieldWithPath("role.applicationIds").description("A Set of ApplicationIds associated with this Role."))
               ));
    }


    @Test
    public void testDeleteRole() throws Exception {
        final Role role = TestUtils.createDummyRole();
        when(mockApplicationAdminService.deleteRole(
            cassandraApplication1.getApplicationId(), role.getRoleId())).thenReturn(Observable.just(null));

        final MvcResult mvcResult = mockMvc.perform(delete("/" + VERSION_1 + "/" + PATH_APPLICATIONS + "/" + PATH_APPLICATION
                                                            + "/" + cassandraApplication1.getApplicationId()
                                                            + "/" + PATH_ROLES
                                                            + "/" + role.getRoleId())
                                                        .contentType(JSON_CONTENT)
                                                        .accept(JSON_CONTENT))
                                           .andDo(print())
                                           .andExpect(request().asyncStarted())
                                           .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
               .andDo(print())
               .andExpect(status().isNoContent())
               .andDo(document("deleteRole",
                               preprocessRequest(prettyPrint()),
                               preprocessResponse(prettyPrint())
               ));
    }

    @Test
    public void testGetAllServiceTypes() throws Exception {
        final ServiceType serviceType1 = TestUtils.createDummyServiceType();

        ArrayList<ServiceType> serviceTypes = new ArrayList<>();
        serviceTypes.add(serviceType1);
        final ServiceTypeList serviceTypeList = new ServiceTypeList(serviceTypes);
        when(mockApplicationAdminService.getAllServiceTypes()).thenReturn(Observable.just(serviceTypeList));

        final MvcResult mvcResult = mockMvc.perform(get("/" + VERSION_1 + "/" + PATH_APPLICATIONS + "/" + PATH_SERVICE_TYPES)
                                                        .contentType(JSON_CONTENT)
                                                        .accept(JSON_CONTENT))
                                           .andDo(print())
                                           .andExpect(request().asyncStarted())
                                           .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
               .andDo(print())
               .andExpect(status().isOk())
               .andExpect(content().contentType(JSON_CONTENT))
               .andExpect(jsonPath("$.serviceTypeList.serviceTypes").isArray())
               .andExpect(jsonPath("$.serviceTypeList.serviceTypes[0].serviceTypeId").value(serviceType1.getServiceTypeId()))
               .andExpect(jsonPath("$.serviceTypeList.serviceTypes[0].description").value(serviceType1.getDescription()))
               .andExpect(jsonPath("$.serviceTypeList.serviceTypes[0].protocolTypeId").value(serviceType1.getProtocolTypeId()))
               .andExpect(jsonPath("$.serviceTypeList.serviceTypes[0].policyTypeIds").isArray())
               .andExpect(jsonPath("$.serviceTypeList.serviceTypes[0].policyTypeIds[0]").value(serviceType1.getPolicyTypeIds().toArray()[0].toString()))
               .andDo(document("getAllServiceTypes",
                               preprocessRequest(prettyPrint()),
                               preprocessResponse(prettyPrint()),
                               responseFields(fieldWithPath("serviceTypeList.serviceTypes[0].serviceTypeId").description("The ID of this Service Type."),
                                              fieldWithPath("serviceTypeList.serviceTypes[0].description").description("The description of this Service Type."),
                                              fieldWithPath("serviceTypeList.serviceTypes[0].policyTypeIds")
                                                  .description("A Set of PolicyTypeIds associated with this Service Type."),
                                              fieldWithPath("serviceTypeList.serviceTypes[0].protocolTypeId")
                                                  .description("The ID of the Protocol Type associated with this ServiceType e.g. NetApi."))
               ));
    }


    @Test
    public void testGetAllRoles() throws Exception {
        final Role role1 = TestUtils.createDummyRole();

        ArrayList<Role> roles = new ArrayList<>();
        roles.add(role1);
        final RoleList roleList = new RoleList(roles);
        when(mockApplicationAdminService.getAllRoles()).thenReturn(Observable.just(roleList));

        final MvcResult mvcResult = mockMvc.perform(get("/" + VERSION_1 + "/" + PATH_APPLICATIONS + "/" + PATH_ROLES)
                                                        .contentType(JSON_CONTENT)
                                                        .accept(JSON_CONTENT))
                                           .andDo(print())
                                           .andExpect(request().asyncStarted())
                                           .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
               .andDo(print())
               .andExpect(status().isOk())
               .andExpect(content().contentType(JSON_CONTENT))
               .andExpect(jsonPath("$.roleList.roles").isArray())
               .andExpect(jsonPath("$.roleList.roles[0].roleId").value(role1.getRoleId()))
               .andExpect(jsonPath("$.roleList.roles[0].description").value(role1.getDescription()))
               .andExpect(jsonPath("$.roleList.roles[0].applicationIds").isArray())
               .andExpect(jsonPath("$.roleList.roles[0].applicationIds[0]").value(role1.getApplicationIds().toArray()[0].toString()))
               .andExpect(jsonPath("$.roleList.roles[0].serviceTypeIds").isArray())
               .andExpect(jsonPath("$.roleList.roles[0].serviceTypeIds[0]").value(role1.getServiceTypeIds().toArray()[0].toString()))
               .andDo(document("getAllRoles",
                               preprocessRequest(prettyPrint()),
                               preprocessResponse(prettyPrint()),
                               responseFields(fieldWithPath("roleList.roles[0].roleId").description("The Role ID that is associated with this Role."),
                                              fieldWithPath("roleList.roles[0].description").description("The description for this Role."),
                                              fieldWithPath("roleList.roles[0].serviceTypeIds").description("A Set of ServiceTypeIds associated with this Role."),
                                              fieldWithPath("roleList.roles[0].applicationIds").description("A Set of ApplicationIds associated with this Role."))
               ));
    }


    @Test
    public void testAddApplicationPolicy() throws Exception {

        when(mockApplicationAdminService.addApplicationPolicy(APPLICATION_ID_1, POLICY_ID, APPLICATION_POLICY))
            .thenReturn(Observable.just(APPLICATION_POLICY));

        final MvcResult mvcResult = mockMvc.perform(put("/" + VERSION_1 + "/" + PATH_APPLICATIONS + "/" + PATH_APPLICATION
                                                            + "/" + APPLICATION_ID_1
                                                            + "/" + PATH_POLICIES + "/" + POLICY_ID)
                                                        .contentType(JSON_CONTENT)
                                                        .content(TestUtils.convertObjectToJsonString(APPLICATION_POLICY))
                                                        .accept(JSON_CONTENT))
                                           .andDo(print())
                                           .andExpect(request().asyncStarted())
                                           .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
               .andDo(print())
               .andExpect(status().isOk())
               .andExpect(content().contentType(JSON_CONTENT))
               .andExpect(jsonPath("$.applicationPolicy.policyId").value(POLICY_ID))
               .andExpect(jsonPath("$.applicationPolicy.values").isMap())
               .andDo(document("addApplicationPolicy",
                               preprocessRequest(prettyPrint()),
                               preprocessResponse(prettyPrint()),
                               responseFields(fieldWithPath("applicationPolicy.policyId").description("The ID of this Policy."),
                                              fieldWithPath("applicationPolicy.values").description("The Policy Key/Values map."))
               ));
    }

    @Test
    public void testDeleteApplicationPolicy() throws Exception {

        when(mockApplicationAdminService.deleteApplicationPolicy(
            APPLICATION_ID_1, POLICY_ID)).thenReturn(Observable.just(null));

        final MvcResult mvcResult = mockMvc.perform(delete("/" + VERSION_1 + "/" + PATH_APPLICATIONS + "/" + PATH_APPLICATION
                                                               + "/" + APPLICATION_ID_1
                                                               + "/" + PATH_POLICIES
                                                               + "/" + POLICY_ID)
                                                        .contentType(JSON_CONTENT)
                                                        .accept(JSON_CONTENT))
                                           .andDo(print())
                                           .andExpect(request().asyncStarted())
                                           .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
               .andDo(print())
               .andExpect(status().isNoContent())
               .andDo(document("deleteApplicationPolicy",
                               preprocessRequest(prettyPrint()),
                               preprocessResponse(prettyPrint())
               ));
    }

    @Test
    public void testAddServicePolicy() throws Exception {

        when(mockApplicationAdminService.addServicePolicy(APPLICATION_ID_1, SERVICE_ID, POLICY_ID, APPLICATION_POLICY))
            .thenReturn(Observable.just(APPLICATION_POLICY));

        final MvcResult mvcResult = mockMvc.perform(put("/" + VERSION_1 + "/" + PATH_APPLICATIONS + "/" + PATH_APPLICATION
                                                            + "/" + cassandraApplication1.getApplicationId()
                                                            + "/" + PATH_SERVICES
                                                            + "/" + SERVICE_ID
                                                            + "/" + PATH_POLICIES
                                                            + "/" + POLICY_ID)
                                                        .contentType(JSON_CONTENT)
                                                        .content(TestUtils.convertObjectToJsonString(APPLICATION_POLICY))
                                                        .accept(JSON_CONTENT))
                                           .andDo(print())
                                           .andExpect(request().asyncStarted())
                                           .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
               .andDo(print())
               .andExpect(status().isOk())
               .andExpect(content().contentType(JSON_CONTENT))
               .andExpect(jsonPath("$.applicationPolicy.policyId").value(POLICY_ID))
               .andExpect(jsonPath("$.applicationPolicy.values").isMap())
               .andDo(document("addServicePolicy",
                               preprocessRequest(prettyPrint()),
                               preprocessResponse(prettyPrint()),
                               responseFields(fieldWithPath("applicationPolicy.policyId").description("The ID of this Policy."),
                                              fieldWithPath("applicationPolicy.values").description("The Policy Key/Values map."))
               ));
    }

    @Test
    public void testDeleteServicePolicy() throws Exception {

        when(mockApplicationAdminService.deleteServicePolicy(
            APPLICATION_ID_1, SERVICE_ID, POLICY_ID)).thenReturn(Observable.just(null));

        final MvcResult mvcResult = mockMvc.perform(delete("/" + VERSION_1 + "/" + PATH_APPLICATIONS + "/" + PATH_APPLICATION
                                                               + "/" + APPLICATION_ID_1
                                                               + "/" + PATH_SERVICES
                                                               + "/" + SERVICE_ID
                                                               + "/" + PATH_POLICIES
                                                               + "/" + POLICY_ID)
                                                        .contentType(JSON_CONTENT)
                                                        .accept(JSON_CONTENT))
                                           .andDo(print())
                                           .andExpect(request().asyncStarted())
                                           .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
               .andDo(print())
               .andExpect(status().isNoContent())
               .andDo(document("deleteServicePolicy",
                               preprocessRequest(prettyPrint()),
                               preprocessResponse(prettyPrint())
               ));
    }

    @Test
    public void testGetAllProtocolTypes() throws Exception {
        final ProtocolType protocolType1 = TestUtils.createDummyProtocolType();
        final ProtocolType protocolType2 = TestUtils.createDummyProtocolType2();

        ArrayList<ProtocolType> protocolTypes = new ArrayList<>();
        protocolTypes.add(protocolType1);
        protocolTypes.add(protocolType2);
        final ProtocolTypeList protocolTypeList = new ProtocolTypeList(protocolTypes);
        when(mockApplicationAdminService.getAllProtocolTypes()).thenReturn(Observable.just(protocolTypeList));

        final MvcResult mvcResult = mockMvc.perform(get("/" + VERSION_1 + "/" + PATH_APPLICATIONS
                                                            + "/" + PATH_PROTOCOL_TYPES)
                                                        .contentType(JSON_CONTENT)
                                                        .accept(JSON_CONTENT))
                                           .andDo(print())
                                           .andExpect(request().asyncStarted())
                                           .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
               .andDo(print())
               .andExpect(status().isOk())
               .andExpect(content().contentType(JSON_CONTENT))
               .andExpect(jsonPath("$.protocolTypeList.protocolTypes").isArray())
               .andExpect(jsonPath("$.protocolTypeList.protocolTypes[0].protocolTypeId").value(PROTOCOL_TYPE_ID))
               .andExpect(jsonPath("$.protocolTypeList.protocolTypes[0].description").value(DESCRIPTION))
               .andExpect(jsonPath("$.protocolTypeList.protocolTypes[0].serviceTypeIds").isArray())
               .andExpect(jsonPath("$.protocolTypeList.protocolTypes[0].serviceTypeIds[0]").value(SERVICE_ID))
               .andDo(document("getAllProtocolTypes",
                               preprocessRequest(prettyPrint()),
                               preprocessResponse(prettyPrint()),
                               responseFields(fieldWithPath("protocolTypeList.protocolTypes[0].protocolTypeId").description("The ID of this Protocol Type."),
                                              fieldWithPath("protocolTypeList.protocolTypes[0].description").description("The description for this Protocol Type."),
                                              fieldWithPath("protocolTypeList.protocolTypes[0].serviceTypeIds").description("A Set of ServiceTypeIds associated "
                                                                                                                                + "with this Protocol Type."))
               ));
    }


    @Test
    public void testGetAllPolicyTypes() throws Exception {
        final PolicyType policyType1 = TestUtils.createDummyPolicyType();

        ArrayList<PolicyType> policyTypes = new ArrayList<>();
        policyTypes.add(policyType1);
        final PolicyTypeList policyTypeList = new PolicyTypeList(policyTypes);
        when(mockApplicationAdminService.getAllPolicyTypes()).thenReturn(Observable.just(policyTypeList));

        final MvcResult mvcResult = mockMvc.perform(get("/" + VERSION_1 + "/" + PATH_APPLICATIONS
                                                            + "/" + PATH_POLICY_TYPES)
                                                        .contentType(JSON_CONTENT)
                                                        .accept(JSON_CONTENT))
                                           .andDo(print())
                                           .andExpect(request().asyncStarted())
                                           .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
               .andDo(print())
               .andExpect(status().isOk())
               .andExpect(content().contentType(JSON_CONTENT))
               .andExpect(jsonPath("$.policyTypeList.policyTypes").isArray())
               .andExpect(jsonPath("$.policyTypeList.policyTypes[0].policyTypeId").value(POLICY_ID))
               .andExpect(jsonPath("$.policyTypeList.policyTypes[0].description").value(DESCRIPTION))
               .andExpect(jsonPath("$.policyTypeList.policyTypes[0].policyOrder").value(POLICY_ORDER))
               .andExpect(jsonPath("$.policyTypeList.policyTypes[0].serviceTypeIds").isArray())
               .andExpect(jsonPath("$.policyTypeList.policyTypes[0].serviceTypeIds[0]").value(SERVICE_ID))
               .andDo(document("getAllPolicyTypes",
                               preprocessRequest(prettyPrint()),
                               preprocessResponse(prettyPrint()),
                               responseFields(fieldWithPath(".policyTypeList.policyTypes[0].policyTypeId").description("The ID of this Policy Type."),
                                              fieldWithPath(".policyTypeList.policyTypes[0].description").description("The description of this Policy Type."),
                                              fieldWithPath(".policyTypeList.policyTypes[0].policyOrder").description("The order in which this policy should be enforced."),
                                              fieldWithPath(".policyTypeList.policyTypes[0].serviceTypeIds")
                                                  .description("A Set of ServiceTypeIds associated with this Policy Type."))
               ));
    }
}
