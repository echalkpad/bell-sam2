package com.vennetics.bell.sam.adapters.messaging.smpp.support.connection;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import com.cloudhopper.smpp.SmppClient;
import com.cloudhopper.smpp.SmppSession;
import com.cloudhopper.smpp.SmppSessionConfiguration;
import com.cloudhopper.smpp.pdu.EnquireLink;
import com.cloudhopper.smpp.pdu.EnquireLinkResp;
import com.cloudhopper.smpp.type.SmppBindException;
import com.vennetics.bell.sam.adapters.messaging.smpp.throttle.ITokenBucket;

@RunWith(MockitoJUnitRunner.class)
public class SmppConnectionHandlerTest {

    // Object under test
    private SmppConnectionHandler objectUnderTest;

    // Mock Objects
    @Mock
    private SmppSessionHandler mockSmppSessionHandler;

    @Mock
    private SmppSessionConfiguration mockSmppSessionConfig;

    @Mock
    private SmppClient mockSmppClient;

    @Mock
    private SmppSession mockSmppSession;

    @Mock
    private ITokenBucket mockTokenBucket;

    @Before
    public void setUp() {
        objectUnderTest = new SmppConnectionHandler(mockSmppSessionHandler,
                                                    mockSmppSessionConfig,
                                                    mockSmppClient,
                                                    mockTokenBucket);
    }

    @Test
    public void testInitShouldInvokeBind() throws Exception {
        when(mockSmppClient.bind(mockSmppSessionConfig,
                                 mockSmppSessionHandler)).thenReturn(mockSmppSession);
        objectUnderTest.init();
        assertSame(objectUnderTest.getSmppSession(), mockSmppSession);
    }

    @Test
    public void testBindExceptionIsntRethrown() throws Exception {
        when(mockSmppClient.bind(mockSmppSessionConfig,
                                 mockSmppSessionHandler)).thenThrow(new SmppBindException(null));
        objectUnderTest.init();
        assertNull(objectUnderTest.getSmppSession());
    }

    @Test
    public void testPollWithNullSessionWillTriggerBind() throws Exception {
        assertNull(objectUnderTest.getSmppSession());
        when(mockSmppClient.bind(mockSmppSessionConfig,
                                 mockSmppSessionHandler)).thenReturn(mockSmppSession);
        objectUnderTest.pollAndReconnect();
        assertSame(objectUnderTest.getSmppSession(), mockSmppSession);
    }

    @Test
    public void testPollWithNotBindingNotBounddNotOpenSessionWillTriggerBind() throws Exception {
        ReflectionTestUtils.setField(objectUnderTest, "session", mockSmppSession);
        when(mockSmppSession.isOpen()).thenReturn(false);
        when(mockSmppSession.isBinding()).thenReturn(false);
        when(mockSmppSession.isBound()).thenReturn(false);
        when(mockSmppClient.bind(mockSmppSessionConfig,
                                 mockSmppSessionHandler)).thenReturn(mockSmppSession);
        objectUnderTest.pollAndReconnect();
        assertSame(objectUnderTest.getSmppSession(), mockSmppSession);
    }

    @Test
    public void testPollWithOpenSessionWillDoNothing() throws Exception {
        ReflectionTestUtils.setField(objectUnderTest, "session", mockSmppSession);
        when(mockSmppSession.isOpen()).thenReturn(true);
        when(mockSmppSession.isBinding()).thenReturn(false);
        when(mockSmppSession.isBound()).thenReturn(false);
        objectUnderTest.pollAndReconnect();
        assertSame(objectUnderTest.getSmppSession(), mockSmppSession);
        verifyZeroInteractions(mockSmppClient);
    }

    @Test
    public void testPollWithBindingSessionWillDoNothing() throws Exception {
        ReflectionTestUtils.setField(objectUnderTest, "session", mockSmppSession);
        when(mockSmppSession.isOpen()).thenReturn(false);
        when(mockSmppSession.isBinding()).thenReturn(true);
        when(mockSmppSession.isBound()).thenReturn(false);
        objectUnderTest.pollAndReconnect();
        assertSame(objectUnderTest.getSmppSession(), mockSmppSession);
        verifyZeroInteractions(mockSmppClient);
    }

    @Test
    public void testPollWithBoundSessionWillTriggerSuccessfulEnquireLink() throws Exception {
        ReflectionTestUtils.setField(objectUnderTest, "session", mockSmppSession);
        when(mockSmppSession.isBinding()).thenReturn(true);
        when(mockSmppSession.isBound()).thenReturn(true);
        when(mockSmppSession.isOpen()).thenReturn(true);
        final EnquireLinkResp enquireLinkResponse = new EnquireLinkResp();
        enquireLinkResponse.setCommandStatus(0);
        when(mockSmppSession.enquireLink(isA(EnquireLink.class),
                                         anyLong())).thenReturn(enquireLinkResponse);
        objectUnderTest.pollAndReconnect();
    }

    @Test
    public void testPollWithUnsuccessfulEnquireLinkWillTriggerRebind() throws Exception {
        ReflectionTestUtils.setField(objectUnderTest, "session", mockSmppSession);
        when(mockSmppSession.isBinding()).thenReturn(true);
        when(mockSmppSession.isBound()).thenReturn(true);
        when(mockSmppSession.isOpen()).thenReturn(true);
        final EnquireLinkResp enquireLinkResponse = new EnquireLinkResp();
        enquireLinkResponse.setCommandStatus(0);
        when(mockSmppSession.enquireLink(isA(EnquireLink.class),
                                         anyLong())).thenThrow(new RuntimeException());
        objectUnderTest.pollAndReconnect();
        verify(mockSmppSession).unbind(anyLong());
    }

    @Test
    public void testPollClosedSessionWillTriggerRebind() throws Exception {
        ReflectionTestUtils.setField(objectUnderTest, "session", mockSmppSession);
        when(mockSmppSession.isBinding()).thenReturn(true);
        when(mockSmppSession.isBound()).thenReturn(true);
        when(mockSmppSession.isOpen()).thenReturn(true);
        when(mockSmppSession.isClosed()).thenReturn(true);
        objectUnderTest.pollAndReconnect();
        verify(mockSmppSession).unbind(anyLong());
    }

    @Test
    public void testPollUnbindingSessionWillTriggerRebind() throws Exception {
        ReflectionTestUtils.setField(objectUnderTest, "session", mockSmppSession);
        when(mockSmppSession.isBinding()).thenReturn(true);
        when(mockSmppSession.isBound()).thenReturn(false);
        when(mockSmppSession.isOpen()).thenReturn(true);
        when(mockSmppSession.isClosed()).thenReturn(false);
        when(mockSmppSession.isUnbinding()).thenReturn(true);
        objectUnderTest.pollAndReconnect();
        verify(mockSmppSession).unbind(anyLong());
    }

    @Test
    public void testNothingHappensWhenAllStatesAreTrue() throws Exception {
        ReflectionTestUtils.setField(objectUnderTest, "session", mockSmppSession);
        when(mockSmppSession.isBinding()).thenReturn(true);
        when(mockSmppSession.isBound()).thenReturn(false);
        when(mockSmppSession.isOpen()).thenReturn(true);
        when(mockSmppSession.isClosed()).thenReturn(false);
        when(mockSmppSession.isUnbinding()).thenReturn(false);
        objectUnderTest.pollAndReconnect();
        verifyZeroInteractions(mockSmppClient);
    }
}
