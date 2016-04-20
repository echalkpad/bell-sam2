package com.vennetics.bell.sam.helloworld;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { World.class })
@ComponentScan("com.vennetics.bell.sam.helloworld")
public class WorldTest {

    @Autowired
    private IWorld world;

    @Test
    public void test() {

        assertEquals("Hello Bob", world.hello("Bob"));
    }

}
