package com.vennetics.bell.sam.adapters.messaging.smpp.support.autoconfigure;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

import org.junit.Before;
import org.junit.Test;

import com.vennetics.bell.sam.adapters.messaging.smpp.SmppInboundEvent;

import rx.subjects.Subject;

public class SmppConfigTest {

    // Object under test
    private SmppConfig objectUnderTest;

    @Before
    public void setup() {
        objectUnderTest = new SmppConfig();
    }

    @Test
    public void firstInstanceShouldBeSameAsSubsequentInstancesOfMessageBus() {
        final Subject<SmppInboundEvent, SmppInboundEvent> firstInstance = objectUnderTest.inboundMessageBus();
        assertNotNull(firstInstance);
        final Subject<SmppInboundEvent, SmppInboundEvent> secondInstance = objectUnderTest.inboundMessageBus();
        assertSame(firstInstance, secondInstance);
        final Subject<SmppInboundEvent, SmppInboundEvent> thirdInstance = objectUnderTest.inboundMessageBus();
        assertSame(thirdInstance, secondInstance);
    }

}
