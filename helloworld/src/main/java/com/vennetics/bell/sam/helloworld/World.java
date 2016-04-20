package com.vennetics.bell.sam.helloworld;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class World implements IWorld {

    @Value("${bar:World!}")
    private String bar;

    @Override
    public String helloFromConfig() {

        return bar;
    }

    @Override
    public String hello(final String from) {

        return "Hello " + from;
    }
}
