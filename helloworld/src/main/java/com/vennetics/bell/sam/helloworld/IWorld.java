package com.vennetics.bell.sam.helloworld;

/**
 * Hello world interface
 */
public interface IWorld {

    /**
     * @return the value in the config server.
     */
    String helloFromConfig();

    /**
     * Say hello.
     *
     * @param from
     * @return
     */
    String hello(String from);
}
