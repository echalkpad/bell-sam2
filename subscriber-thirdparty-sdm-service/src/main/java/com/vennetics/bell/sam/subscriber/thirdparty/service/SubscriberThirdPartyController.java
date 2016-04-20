package com.vennetics.bell.sam.subscriber.thirdparty.service;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import com.vennetics.bell.sam.error.adapters.BellSubscriberErrorAdapter;
import com.vennetics.bell.sam.error.adapters.IErrorAdapter;
import com.vennetics.bell.sam.model.subscriber.thirdparty.FeatureCodes;
import com.vennetics.bell.sam.model.subscriber.thirdparty.SearchFilterType;
import com.vennetics.bell.sam.model.subscriber.thirdparty.SubscriberProfile;
import com.vennetics.bell.sam.model.subscriber.thirdparty.SubscriberThirdPartyModelConstants;
import com.vennetics.bell.sam.rest.controller.ExceptionHandlingRestController;
import com.vennetics.microservices.common.api.model.admin.v1.application.ApplicationUserDetails;
import com.vennetics.microservices.common.core.security.jwt.JwtConstants;

/**
 * Spring REST Controller for SubX
 */
@RestController
@RequestMapping(SubscriberThirdPartyModelConstants.PATH_SUBSCRIBER_THIRD_PARTY)
public class SubscriberThirdPartyController extends ExceptionHandlingRestController {

    private static final Logger logger = LoggerFactory.getLogger(SubscriberThirdPartyController.class);

    @Autowired
    private ISubscriberThirdPartyService subscriberThirdPartyService;

    @Autowired
    @Qualifier(BellSubscriberErrorAdapter.SERVICE_NAME)
    private IErrorAdapter errorAdapter;

    @ModelAttribute(JwtConstants.JWT_HEADER_INTERNAL)
    public ApplicationUserDetails getApplication(final HttpServletRequest request) {
        return (ApplicationUserDetails) request.getAttribute(JwtConstants.JWT_HEADER_INTERNAL);
    }

    @InitBinder
    public void initBinder(final WebDataBinder binder) {
        // register a custom editor for the search filter type.
        binder.registerCustomEditor(SearchFilterType.class, new SearchFilterTypePropertyEditor());
    }

    @PostConstruct
    public void init() {
        logger.info("Initialised SubscriberThirdPartyController");
    }

    /**
     * The GET endpoint for SubX
     *
     * @param searchFilter
     * @param filterValue
     * @return REST response
     */
    @RequestMapping(
                    method = RequestMethod.GET,
                    value = SubscriberThirdPartyModelConstants.PATH_GET_SUB_PROFILE)
    public DeferredResult<SubscriberProfile> getSubscriberProfile(@ModelAttribute(JwtConstants.JWT_HEADER_INTERNAL) final ApplicationUserDetails application,
                                                                  @RequestParam(
                                                                                  value = SubscriberThirdPartyModelConstants.QPARAM_SEARCH_FILTER,
                                                                                  required = true) final SearchFilterType searchFilter,
                                                                  @RequestParam(
                                                                                  value = SubscriberThirdPartyModelConstants.QPARAM_FILTER_VALUE,
                                                                                  required = true) final String filterValue) {

        // Doing nothing with this. Just proving it's accessible
        logger.debug("Handling request for application: {}", application);

        logger.debug("REST call for getSubscriberProfile with search filter: {} and filter "
                        + "value: {}",
                     searchFilter,
                     filterValue);

        final DeferredResult<SubscriberProfile> deffered = new DeferredResult<>();

        subscriberThirdPartyService.getSubscriberProfile(searchFilter, filterValue)
                                   .subscribe(deffered::setResult, deffered::setErrorResult); // subscribe
        // async

        return deffered;
    }

    @RequestMapping(
                    method = RequestMethod.GET,
                    value = SubscriberThirdPartyModelConstants.PATH_HAS_FEATURE_CODES)
    public DeferredResult<FeatureCodes> hasFeatureCodes(@ModelAttribute(JwtConstants.JWT_HEADER_INTERNAL) final ApplicationUserDetails application,
                                                        @RequestParam(
                                                                        value = SubscriberThirdPartyModelConstants.QPARAM_MDN,
                                                                        required = true) final String mdn,
                                                        @RequestParam(
                                                                        value = SubscriberThirdPartyModelConstants.QPARAM_AFFILIATE_ID,
                                                                        required = false) final String affiliateId,
                                                        @RequestParam(
                                                                        value = SubscriberThirdPartyModelConstants.QPARAM_FEATURE_CODE,
                                                                        required = true) final List<String> featureCodes) {
        // Doing nothing with this. Just proving it's accessible
        logger.debug("Handling request for application: {}", application);

        logger.debug("REST call for hasFeatureCodes with mdn: {} and affiliate id: {}",
                     mdn,
                     affiliateId);

        final DeferredResult<FeatureCodes> deffered = new DeferredResult<>();

        subscriberThirdPartyService.hasFeatureCodes(mdn, affiliateId, featureCodes)
                                   .subscribe(deffered::setResult, deffered::setErrorResult); // subscribe
        // async
        return deffered;

    }

    @Override
    protected IErrorAdapter getErrorAdapter() {
        return errorAdapter;
    }

}
