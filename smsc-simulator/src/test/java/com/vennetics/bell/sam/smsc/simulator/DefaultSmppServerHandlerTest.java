package com.vennetics.bell.sam.smsc.simulator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.cloudhopper.smpp.SmppServerSession;
import com.cloudhopper.smpp.SmppSessionConfiguration;
import com.cloudhopper.smpp.SmppSessionCounters;
import com.cloudhopper.smpp.SmppSessionHandler;
import com.cloudhopper.smpp.pdu.BaseBind;
import com.cloudhopper.smpp.pdu.BaseBindResp;
import com.cloudhopper.smpp.util.ConcurrentCommandCounter;

@RunWith(MockitoJUnitRunner.class)
public class DefaultSmppServerHandlerTest {

    // Object under test
    private DefaultSmppServerHandler objectUnderTest;

    // Mock Objects
    @Mock
    private SmppServerSession mockSession;

    @Mock
    private BaseBind mockBaseBind;

    @Mock
    private BaseBindResp mockBaseBindResp;

    @Mock
    private SmppSessionCounters mockCounters;

    // Concrete Objects
    private SmppSessionConfiguration smppConfiguration;
    private Long sessionId = 123L;
    private String systemId = "test";

    @Before
    public void setup() {
        objectUnderTest = new DefaultSmppServerHandler();
        smppConfiguration = new SmppSessionConfiguration();
    }

    @Test
    public void shouldSetSessionNameAsExpectedOnBind() throws Exception {
        smppConfiguration.setSystemId(systemId);
        assertNull(smppConfiguration.getName());
        objectUnderTest.sessionBindRequested(sessionId, smppConfiguration, mockBaseBind);
        assertEquals(smppConfiguration.getName(), "Application.SMPP." + systemId);
    }

    @Test
    public void shouldSetServerStateAsExpectedOnSessionCreated() throws Exception {
        objectUnderTest.sessionCreated(sessionId, mockSession, mockBaseBindResp);
        verify(mockSession).serverReady(isA(SmppSessionHandler.class));
    }

    @Test
    public void shouldDestroySessionAsExpectedOnSessionDestroyed() throws Exception {
        objectUnderTest.sessionDestroyed(sessionId, mockSession);
        verify(mockSession).destroy();
    }

    @Test
    public void shouldDestroySessionAndLogCounterAsExpectedOnSessionDestroyed() throws Exception {
        when(mockSession.hasCounters()).thenReturn(true);
        when(mockSession.getCounters()).thenReturn(mockCounters);
        when(mockCounters.getRxSubmitSM()).thenReturn(new ConcurrentCommandCounter());
        objectUnderTest.sessionDestroyed(sessionId, mockSession);
        verify(mockSession).destroy();
    }
}
