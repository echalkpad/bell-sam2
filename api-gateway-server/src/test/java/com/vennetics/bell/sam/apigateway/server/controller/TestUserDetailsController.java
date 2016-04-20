package com.vennetics.bell.sam.apigateway.server.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.vennetics.bell.sam.registry.ServiceConstants;

@RestController
public class TestUserDetailsController {


    @RequestMapping(
                    method = RequestMethod.GET,
                    value = ServiceConstants.BELL_SUBSCRIBER_URL + "/extraPath")
    public String bellSubscriberLookup() {

        return "bellSubscriberLookup";
    }

}
