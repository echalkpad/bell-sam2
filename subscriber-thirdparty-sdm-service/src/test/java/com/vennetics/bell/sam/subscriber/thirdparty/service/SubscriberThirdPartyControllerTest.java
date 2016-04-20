package com.vennetics.bell.sam.subscriber.thirdparty.service;

import static org.hamcrest.core.StringContains.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

import com.vennetics.bell.sam.model.subscriber.thirdparty.SearchFilterType;
import com.vennetics.bell.sam.model.subscriber.thirdparty.SubscriberThirdPartyModelConstants;
import com.vennetics.bell.sam.sdm.adapter.api.SdmQueryFilter;
import com.vennetics.bell.sam.subscriber.thirdparty.service.exceptions.SubscriberNotFoundException;
import com.vennetics.bell.sam.subscriber.thirdparty.service.util.test.SubscriberThirdPartyTestDataBuilder;

import rx.Observable;

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { SubscriberThirdPartyControllerTestConfig.class })
public class SubscriberThirdPartyControllerTest {

    @ClassRule
    public static final RestDocumentation REST_DOCUMENTATION = new RestDocumentation("build/generated-snippets");

    private static final MediaType JSON_CONTENT = new MediaType(MediaType.APPLICATION_JSON.getType(),
                                                                MediaType.APPLICATION_JSON.getSubtype(),
                                                                Charset.forName("utf8"));

    private static final String SEARCH_MDN = "4151234567";

    private static final String AFFILIATE_ID = "023";

    private static final List<String> FEATURE_CODES = Arrays.asList("3FEA", "3FEB", "XXX");

    @Autowired
    private ISubscriberThirdPartyService subscriberThirdPartyService;

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
    public void shouldSupportSuccessfulGetSubscriberProfileQuery() throws Exception {

        // Mock LDAP response
        when(subscriberThirdPartyService.getSubscriberProfile(SearchFilterType.MDN,
                                                              SEARCH_MDN)).thenReturn(Observable.just(SubscriberThirdPartyTestDataBuilder.SUBSCRIBER_PROFILE));

        // Happy Path

        final MvcResult mvcResult = mockMvc.perform(get("/api/subscriber/profile").param("searchFilter",
                                                                                         "MDN")
                                                                                  .param("filterValue",
                                                                                         SEARCH_MDN)
                                                                                  .contentType(JSON_CONTENT)
                                                                                  .accept(JSON_CONTENT))
                                           .andDo(print())
                                           .andExpect(request().asyncStarted())
                                           .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
               .andDo(print())
               .andExpect(status().isOk())
               .andExpect(content().contentType(JSON_CONTENT))
               .andExpect(jsonPath("$.subscriberProfile.attributes").isArray())
               .andExpect(jsonPath("$.subscriberProfile.attributes[0].name").value("MDN"))
               .andExpect(jsonPath("$.subscriberProfile.attributes[0].value").value("123456789"))
               .andExpect(jsonPath("$.subscriberProfile.attributes[1].name").value("eamLanguage"))
               .andExpect(jsonPath("$.subscriberProfile.attributes[1].value").value("E"))
               .andDo(document("getSubscriberProfile",
                               preprocessRequest(prettyPrint()),
                               preprocessResponse(prettyPrint()),
                               requestParameters(parameterWithName("searchFilter").description("The Filter which is one among this list (MDN,SUBID)"),
                                                 parameterWithName("filterValue").description("The Value of the searchFilter.")),
                               responseFields(fieldWithPath("subscriberProfile.attributes").description("A list of Name Value pairs containing the attributes for a Subscriber"))));

    }

    @Test
    public void shouldSupportSuccessfulHasFeatureCodesQuery() throws Exception {

        // Mock LDAP response
        when(subscriberThirdPartyService.hasFeatureCodes(SEARCH_MDN,
                                                         AFFILIATE_ID,
                                                         FEATURE_CODES)).thenReturn(Observable.just(SubscriberThirdPartyTestDataBuilder.FEATURE_CODES));

        // Happy Path
        final MvcResult mvcResult = mockMvc.perform(get("/api/subscriber/featureCodes").param(SubscriberThirdPartyModelConstants.QPARAM_MDN,
                                                                                              SEARCH_MDN)
                                                                                       .param(SubscriberThirdPartyModelConstants.QPARAM_AFFILIATE_ID,
                                                                                              AFFILIATE_ID)
                                                                                       .param(SubscriberThirdPartyModelConstants.QPARAM_FEATURE_CODE,
                                                                                              FEATURE_CODES.get(0))
                                                                                       .param(SubscriberThirdPartyModelConstants.QPARAM_FEATURE_CODE,
                                                                                              FEATURE_CODES.get(1))
                                                                                       .param(SubscriberThirdPartyModelConstants.QPARAM_FEATURE_CODE,
                                                                                              FEATURE_CODES.get(2))
                                                                                       .contentType(JSON_CONTENT)
                                                                                       .accept(JSON_CONTENT))
                                           .andDo(print())
                                           .andExpect(request().asyncStarted())
                                           .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
               .andDo(print())
               .andExpect(status().isOk())
               .andExpect(content().contentType(JSON_CONTENT))
               .andExpect(jsonPath("$.featureCodes.codes").isArray())
               .andExpect(jsonPath("$.featureCodes.codes[0]").value(Boolean.TRUE))
               .andExpect(jsonPath("$.featureCodes.codes[1]").value(Boolean.TRUE))
               .andExpect(jsonPath("$.featureCodes.codes[2]").value(Boolean.FALSE))
               .andDo(document("hasFeatureCodes",
                               preprocessRequest(prettyPrint()),
                               preprocessResponse(prettyPrint()),
                               requestParameters(parameterWithName(SubscriberThirdPartyModelConstants.QPARAM_MDN).description("The MDN identifiying the subscriber."),
                                                 parameterWithName(SubscriberThirdPartyModelConstants.QPARAM_AFFILIATE_ID).description("(Optional) The affiliateID "
                                                                 + "associated with the subscriber."),
                                                 parameterWithName(SubscriberThirdPartyModelConstants.QPARAM_FEATURE_CODE).description("One or more codes to check.")),
                               responseFields(fieldWithPath("featureCodes.codes").description("A list of Boolean values indicating whether the subscriber has the feature."))));
    }

    @Test
    public void shouldSupportSuccessfulHasFeatureCodesQueryWithOptionalAffiliateIdMissing() throws Exception {

        // Mock LDAP response
        when(subscriberThirdPartyService.hasFeatureCodes(SEARCH_MDN,
                                                         null,
                                                         FEATURE_CODES)).thenReturn(Observable.just(SubscriberThirdPartyTestDataBuilder.FEATURE_CODES));

        // Happy Path
        final MvcResult mvcResult = mockMvc.perform(get("/api/subscriber/featureCodes").param(SubscriberThirdPartyModelConstants.QPARAM_MDN,
                                                                                              SEARCH_MDN)
                                                                                       .param(SubscriberThirdPartyModelConstants.QPARAM_FEATURE_CODE,
                                                                                              FEATURE_CODES.get(0))
                                                                                       .param(SubscriberThirdPartyModelConstants.QPARAM_FEATURE_CODE,
                                                                                              FEATURE_CODES.get(1))
                                                                                       .param(SubscriberThirdPartyModelConstants.QPARAM_FEATURE_CODE,
                                                                                              FEATURE_CODES.get(2))
                                                                                       .contentType(JSON_CONTENT)
                                                                                       .accept(JSON_CONTENT))
                                           .andDo(print())
                                           .andExpect(request().asyncStarted())
                                           .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
               .andDo(print())
               .andExpect(status().isOk())
               .andExpect(content().contentType(JSON_CONTENT))
               .andExpect(jsonPath("$.featureCodes.codes").isArray())
               .andExpect(jsonPath("$.featureCodes.codes[0]").value(Boolean.TRUE))
               .andExpect(jsonPath("$.featureCodes.codes[1]").value(Boolean.TRUE))
               .andExpect(jsonPath("$.featureCodes.codes[2]").value(Boolean.FALSE));
    }

    @Test
    public void shouldRejectInvalidQueryParam() throws Exception {

        // Validation is not done asynchronously

        mockMvc.perform(get("/api/subscriber/profile").param("searchFilter", "BLAH")
                                                      .param("filterValue", "123456789")
                                                      .contentType(JSON_CONTENT)
                                                      .accept(JSON_CONTENT))
               .andDo(print())
               .andExpect(status().isBadRequest())
               .andExpect(content().string(containsString("POL0080")))
               .andReturn();
    }

    @Test
    public void shouldHandleExceptionsFromLowerLeveApi() throws Exception {
        final Set<SdmQueryFilter> sdmQueryFilters = new HashSet<>(Arrays.asList(new SdmQueryFilter("filter",
                                                                                                   "value")));
        // Mock LDAP response
        when(subscriberThirdPartyService.getSubscriberProfile(SearchFilterType.MDN,
                                                              "123456789")).thenThrow(new SubscriberNotFoundException(sdmQueryFilters));

        // Happy Path

        mockMvc.perform(get("/api/subscriber/profile").param("searchFilter", "MDN")
                                                      .param("filterValue", "123456789")
                                                      .contentType(JSON_CONTENT)
                                                      .accept(JSON_CONTENT))
               .andDo(print())
               .andExpect(status().isBadRequest());

    }

    @Test
    public void shouldRejectMissingSearchFilter() throws Exception {

        // Validation is not done asynchronously

        mockMvc.perform(get("/api/subscriber/profile").param("filterValue", "123456789")
                                                      .contentType(JSON_CONTENT)
                                                      .accept(JSON_CONTENT))
               .andDo(print())
               .andExpect(status().isBadRequest())
               .andExpect(content().string(containsString("SVC0002")))
               .andReturn();
    }

    @Test
    public void shouldRejectMissingSearchValue() throws Exception {

        // Validation is not done asynchronously

        mockMvc.perform(get("/api/subscriber/profile").param("searchFilter", "MDN")
                                                      .contentType(JSON_CONTENT)
                                                      .accept(JSON_CONTENT))
               .andDo(print())
               .andExpect(status().isBadRequest())
               .andExpect(content().string(containsString("SVC0002")))
               .andReturn();
    }

}
