package com.vennetics.bell.sam.admin.service.controllers;

import com.vennetics.bell.sam.error.adapters.DefaultErrorAdapter;
import com.vennetics.bell.sam.error.adapters.IErrorAdapter;
import com.vennetics.bell.sam.rest.controller.ExceptionHandlingRestController;
import com.vennetics.microservices.common.api.model.admin.AdminApiModelConstants;
import com.vennetics.microservices.common.api.model.admin.v1.application.ApplicationUserDetails;
import com.vennetics.microservices.common.api.model.admin.v1.application.IApplicationDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

/**
 * Spring controller for User Details REST API
 */
@RestController
@RequestMapping(AdminApiModelConstants.PATH_USERDETAILS)
public class ApplicationUserDetailsController extends ExceptionHandlingRestController {

    @Autowired
    @Qualifier(DefaultErrorAdapter.SERVICE_NAME)
    private IErrorAdapter errorAdapter;

    @Autowired
    private IApplicationDetailsService adminApplicationUserDetailsService;

    /**
     * Get all Applications
     *
     * @return all the Applications
     */
    @RequestMapping(method = RequestMethod.GET, value = AdminApiModelConstants.PATH_CLIENT_ID)
    public DeferredResult<ApplicationUserDetails> loadByClientId(@PathVariable(AdminApiModelConstants.PATH_PARAM_CLIENT_ID) final String clientId) {

        final DeferredResult<ApplicationUserDetails> defferedResult = new DeferredResult<>();

        adminApplicationUserDetailsService.loadByClientId(clientId)
                                          .subscribe(defferedResult::setResult,
                                                     defferedResult::setErrorResult);

        return defferedResult;
    }

    @Override
    protected IErrorAdapter getErrorAdapter() {
        return errorAdapter;
    }
}
