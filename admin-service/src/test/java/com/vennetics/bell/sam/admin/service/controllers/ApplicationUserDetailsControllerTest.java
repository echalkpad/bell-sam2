package com.vennetics.bell.sam.admin.service.controllers;

import com.vennetics.bell.sam.admin.service.exceptions.AdminServiceEntityNotFoundException;
import com.vennetics.microservices.common.api.model.admin.AdminApiModelConstants;
import com.vennetics.microservices.common.api.model.admin.v1.application.ApplicationUserDetails;
import com.vennetics.microservices.common.api.model.admin.v1.application.IApplicationDetailsService;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
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
import java.util.UUID;

import static com.vennetics.microservices.common.api.model.admin.util.AdminApiTestData.APPLICATION_USER_DETAILS;
import static com.vennetics.microservices.common.api.model.admin.util.AdminApiTestData.POLICY_ID;
import static com.vennetics.microservices.common.api.model.admin.util.AdminApiTestData.POLICY_NAME;
import static com.vennetics.microservices.common.api.model.admin.util.AdminApiTestData.POLICY_VALUES;
import static com.vennetics.microservices.common.api.model.admin.util.AdminApiTestData.ROLE_ADMIN;
import static com.vennetics.microservices.common.api.model.admin.util.AdminApiTestData.ROLE_USER;
import static com.vennetics.microservices.common.api.model.admin.util.AdminApiTestData.SERVICE_TYPE_ID;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ApplicationAdminServiceControllerTestConfig.class })
@WebAppConfiguration
public class ApplicationUserDetailsControllerTest {

    @ClassRule
    public static final RestDocumentation REST_DOCUMENTATION = new RestDocumentation("build/generated-snippets");

    private static final MediaType JSON_CONTENT = new MediaType(MediaType.APPLICATION_JSON.getType(),
                                                                MediaType.APPLICATION_JSON.getSubtype(),
                                                                Charset.forName("utf8"));

    @Autowired
    private IApplicationDetailsService mockApplicationDetailsService;

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @Before
    public void setup() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                                 .apply(documentationConfiguration(REST_DOCUMENTATION))
                                 .build();
    }

    @Test
    public void shouldAllowQueryByClientId() throws Exception {

        when(mockApplicationDetailsService.loadByClientId("clientId")).thenReturn(Observable.<ApplicationUserDetails>just(APPLICATION_USER_DETAILS));

        final MvcResult mvcResult = mockMvc.perform(get(AdminApiModelConstants.PATH_USERDETAILS
                        + "/clientId").accept(JSON_CONTENT))
                                           .andDo(print())
                                           .andExpect(request().asyncStarted())
                                           .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
               .andDo(print())
               .andExpect(status().isOk())
               .andExpect(content().contentType(JSON_CONTENT))

               .andExpect(jsonPath("$.applicationUserDetails.clientId").value(APPLICATION_USER_DETAILS.getClientId()))
               .andExpect(jsonPath("$.applicationUserDetails.secret").value(APPLICATION_USER_DETAILS.getSecret()))

               .andExpect(jsonPath("$.applicationUserDetails.roles[*]",
                                   containsInAnyOrder(ROLE_USER, ROLE_ADMIN)))

               .andExpect(jsonPath("$.applicationUserDetails.allowedServices[*].serviceTypeId",
                                   contains(SERVICE_TYPE_ID)))
               .andExpect(jsonPath("$.applicationUserDetails.allowedServices[*].policies[*].policyId",
                                   contains(POLICY_ID)))
               .andExpect(jsonPath("$.applicationUserDetails.allowedServices[*].policies[*].values."
                               + POLICY_NAME + "[*]", containsInAnyOrder(POLICY_VALUES.toArray())))

               .andExpect(jsonPath("$.applicationUserDetails.applicationPolicies[*].policyId",
                                   contains(POLICY_ID)))
               .andExpect(jsonPath("$.applicationUserDetails.applicationPolicies[*].values."
                               + POLICY_NAME + "[*]", containsInAnyOrder(POLICY_VALUES.toArray())))

               .andDo(document("loadByClientId",
                               preprocessRequest(prettyPrint()),
                               preprocessResponse(prettyPrint()),

                               // Manually formatting to keep in line length
                               // @formatter:off
                               responseFields(fieldWithPath("applicationUserDetails.clientId")
                                                  .description("The Client ID that is associated with this Application."),
                                              fieldWithPath("applicationUserDetails.secret")
                                                  .description("The encoded secret for authenticating this application."),
                                              fieldWithPath("applicationUserDetails.state")
                                                  .description("The state of the application - INACTIVE or ACTIVE."),

                                              fieldWithPath("applicationUserDetails.allowedServices")
                                                  .description("List of allowed service Ids."),
                                              fieldWithPath("applicationUserDetails.allowedServices[].serviceTypeId")
                                                  .description("Identifier for an allowed service."),
                                              fieldWithPath("applicationUserDetails.allowedServices[].policies")
                                                  .description("The policies provisioned against the allowed service."),
                                              fieldWithPath("applicationUserDetails.allowedServices[].policies[].policyId")
                                                  .description("The identifier for a provisioned policy type."),
                                              fieldWithPath("applicationUserDetails.allowedServices[].policies[].values")
                                                  .description("A map keyed on the policy value key with a set of values against each key."),

                                              fieldWithPath("applicationUserDetails.roles")
                                                  .description("The roles assigned to this application."),

                                              fieldWithPath("applicationUserDetails.applicationPolicies")
                                                  .description("The service policies assigned to this application."),
                                              fieldWithPath("applicationUserDetails.applicationPolicies[].policyId")
                                                  .description("The identifier for a provisioned policy type."),
                                              fieldWithPath("applicationUserDetails.applicationPolicies[].values")
                                                  .description("A map keyed on the policy value key with a set of values against each key.")
                              // @formatter:on

        )));
    }

    @Test
    public void shouldReturnOkWhenNotFound() throws Exception {

        when(mockApplicationDetailsService.loadByClientId("clientId")).thenReturn(Observable.<ApplicationUserDetails>just(APPLICATION_USER_DETAILS));

        final MvcResult mvcResult = mockMvc.perform(get(AdminApiModelConstants.PATH_USERDETAILS
                        + "/clientId").accept(JSON_CONTENT))
                                           .andDo(print())
                                           .andExpect(request().asyncStarted())
                                           .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
               .andDo(print())
               .andExpect(status().isOk())
               .andExpect(content().contentType(JSON_CONTENT));

    }

    @Test
    public void shouldHandleThrownExceptions() throws Exception {
        when(mockApplicationDetailsService.loadByClientId("clientId")).thenThrow(new AdminServiceEntityNotFoundException(UUID.randomUUID().toString()));

        mockMvc.perform(get(AdminApiModelConstants.PATH_USERDETAILS + "/clientId")
                                                                                            .accept(JSON_CONTENT))
               .andDo(print())
               .andExpect(status().isNotFound())
               .andExpect(content().contentType(JSON_CONTENT))
               .andReturn();
    }

}
