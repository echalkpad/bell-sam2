package com.vennetics.bell.sam.helloworld;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RefreshScope
@EnableAutoConfiguration
public class HelloWorldController {

    @Autowired
    private IWorld world;

    @RequestMapping("/helloworld")
    public String message() {
        return world.helloFromConfig();
    }

}
