package com.vennetics.bell.sam.adapters.messaging.smpp.support;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.SocketUtils;

import com.cloudhopper.smpp.SmppConstants;
import com.cloudhopper.smpp.simulator.SmppSimulatorServer;
import com.cloudhopper.smpp.util.DaemonExecutors;
import com.vennetics.bell.sam.adapters.messaging.smpp.ISmppAdapter;
import com.vennetics.bell.sam.adapters.messaging.smpp.SmppInboundEvent;
import com.vennetics.bell.sam.adapters.messaging.smpp.SubmitSmFailed;
import com.vennetics.bell.sam.adapters.messaging.smpp.SubmitSmResponse;
import com.vennetics.bell.sam.adapters.messaging.smpp.support.autoconfigure.SmppProperties;

import rx.Observable;
import rx.subjects.Subject;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = TestConfiguration.class)
@IntegrationTest({ "server.port=0", "management.port=0", "smpp.bindType=TRANSCEIVER",
        "smpp.primarySmppConnection.host=localhost", "smpp.windowSize=1",
        "smpp.primarySmppConnection.connectionTimeout=10000", "smpp.maxConcurrentSessions=1",
        "smpp.requestExpiryTimeout=5000", "smpp.systemId=smppclient1", "smpp.password=password",
        "smpp.enquireLinkPollingInterval=15000", "smpp.sourceAddress.ton=3",
        "smpp.sourceAddress.npi=0", "smpp.DESTINATION_ADDRESS.ton=1",
        "smpp.DESTINATION_ADDRESS.npi=1", "smpp.defaultRegion=CA",
        "smpp.enquireLinkTimeoutMilliseconds=5000",
        "smpp.outBoundThrottlingRate.intervalInMilliseconds=10000",
        "smpp.outBoundThrottlingRate.maxTasksPerInterval=100",
        "smpp.inBoundThrottlingRate.intervalInMilliseconds=10000",
        "smpp.inBoundThrottlingRate.maxTasksPerInterval=100",
        "smpp.smppUnbindTimeoutInMilliseconds=5000",
        "smpp.submitMessageTimeoutMilliseconds=10000"})
public class SmppAdapterIntegrationTest {

    private static SmppSimulatorServer server;
    private static int serverPort;

    @Autowired
    private ISmppAdapter smppAdapter;

    @Autowired
    private SmppProperties smppProperties;

    @Autowired
    private Subject<SmppInboundEvent, SmppInboundEvent> inboundMessageBus;

    private static final String SENDER_ADDRESS = "1234567";
    private static final String DESTINATION_ADDRESS = "+19999999999";
    private static final String MESSAGE = "Message";

    @BeforeClass
    public static void startSimulator() {
        server = new SmppSimulatorServer(DaemonExecutors.newCachedDaemonThreadPool());
        serverPort = SocketUtils.findAvailableTcpPort();
        server.getHandler().setDefaultPduProcessor(new SmppEchoSimulatorProcessor());
        server.start(serverPort);
        System.setProperty("smpp.primarySmppConnection.port", Long.valueOf(serverPort).toString());
    }

    @AfterClass
    public static void stopSimulator() {
        server.stop();
    }

    @Test
    public void testEventHandlerIsSame() throws Exception {
        final Observable<SmppInboundEvent> observable = smppAdapter.getEventBus();
        assertSame(inboundMessageBus, observable);
    }

    @Test
    public void testBytePropertiesAreAsExpected() {
        assertEquals(smppProperties.getSourceAddress().getTon(), SmppConstants.TON_NETWORK);
    }

    @Test
    public void testSubmitSmBinaryResponsesAreBroadcast() throws Exception {
        registerServerEchoProcessor();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final Observable<SmppInboundEvent> observable = smppAdapter.getEventBus();
        observable.subscribe(event -> {
            if (event instanceof SubmitSmResponse) {
                countDownLatch.countDown();
            }
        });
        smppAdapter.submitSmBinary(SENDER_ADDRESS, DESTINATION_ADDRESS, MESSAGE.getBytes());
        assertTrue(countDownLatch.await(1, TimeUnit.SECONDS));
    }

    @Test
    public void testSubmitSmBinaryNackResponsesAreBroadcast() throws Exception {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final Observable<SmppInboundEvent> observable = smppAdapter.getEventBus();
        observable.subscribe(event -> {
            System.err.println(" >> " + event);
            if (event instanceof SubmitSmFailed) {
                countDownLatch.countDown();
            }
        });
        smppAdapter.submitSmBinary(SENDER_ADDRESS,
                                   SmppEchoSimulatorProcessor.ADDRESS_THAT_RETURNS_NACK,
                                   MESSAGE.getBytes());
        assertTrue(countDownLatch.await(2, TimeUnit.SECONDS));
    }

    @Test
    public void testSubmitSmTextResponsesAreBroadcast() throws Exception {
        registerServerEchoProcessor();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final Observable<SmppInboundEvent> observable = smppAdapter.getEventBus();
        observable.subscribe(event -> {
            if (event instanceof SubmitSmResponse) {
                countDownLatch.countDown();
            }
        });
        smppAdapter.submitSmText(SENDER_ADDRESS, DESTINATION_ADDRESS, MESSAGE);
        assertTrue(countDownLatch.await(1, TimeUnit.SECONDS));
    }

    @Test
    public void testSubmitSmTextNackResponsesAreBroadcast() throws Exception {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final Observable<SmppInboundEvent> observable = smppAdapter.getEventBus();
        observable.subscribe(event -> {
            System.err.println(" >> " + event);
            if (event instanceof SubmitSmFailed) {
                countDownLatch.countDown();
            }
        });
        smppAdapter.submitSmText(SENDER_ADDRESS,
                                 SmppEchoSimulatorProcessor.ADDRESS_THAT_RETURNS_NACK, MESSAGE);
        assertTrue(countDownLatch.await(2, TimeUnit.SECONDS));
    }

    /**
     * This PDU Processor accepts bind, submit sm and enquire link requests and
     * sends appropriate responses.
     *
     * @throws Exception
     */
    private void registerServerEchoProcessor() throws Exception {
        server.getHandler().setDefaultPduProcessor(new SmppEchoSimulatorProcessor());
    }

}
