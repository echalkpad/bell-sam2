package com.vennetics.bell.sam.adapters.messaging.smpp.support;

import com.cloudhopper.commons.util.windowing.DefaultWindowFuture;
import com.cloudhopper.smpp.pdu.PduRequest;
import com.cloudhopper.smpp.pdu.PduResponse;

public class TestWindowFuture extends DefaultWindowFuture<Integer, PduRequest, PduResponse> {

    private boolean successResponse;
    private boolean awaitResponse;

    public TestWindowFuture(final boolean successResponse,
                            final boolean awaitResponse,
                            final PduRequest pduRequest) {
        super(null, null, null, null, pduRequest, 0, 0, 0, 0, 0, 0);
        this.successResponse = successResponse;
        this.awaitResponse = awaitResponse;
    }

    @Override
    public boolean isSuccess() {
        return successResponse;
    }

    @Override
    public boolean await() {
        return awaitResponse;
    }

}
