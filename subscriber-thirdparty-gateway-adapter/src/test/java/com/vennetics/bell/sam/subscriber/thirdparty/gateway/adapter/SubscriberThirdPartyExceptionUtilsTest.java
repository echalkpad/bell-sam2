package com.vennetics.bell.sam.subscriber.thirdparty.gateway.adapter;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import generated.ca.bell.wsdl.thirdparty.subscriber.v1_0.faults.PolicyException;
import generated.ca.bell.wsdl.thirdparty.subscriber.v1_0.faults.ServiceException;

public class SubscriberThirdPartyExceptionUtilsTest {

    private static final String VARIABLE_2 = "VARIABLE_2";

    private static final String VARIABLE_1 = "VARIABLE_1";

    private static final String TEXT = "text";

    private static final String MESSAGE_ID = "messageId";

    @Test
    public void shouldSupportPolicyExceptionWithNullVariables() {

        final PolicyException result = SubscriberThirdPartyExceptionUtils.newPolicyException(MESSAGE_ID,
                                                                                             TEXT,
                                                                                             null);
        assertThat(result.getFaultInfo().getMessageId(), equalTo(MESSAGE_ID));
        assertThat(result.getFaultInfo().getText(), equalTo(TEXT));
        assertThat(result.getFaultInfo().getVariables(), empty());
    }

    @Test
    public void shouldSupporServiceExceptionWithNullVariables() {

        final ServiceException result = SubscriberThirdPartyExceptionUtils.newServiceException(MESSAGE_ID,
                                                                                               TEXT,
                                                                                               null);
        assertThat(result.getFaultInfo().getMessageId(), equalTo(MESSAGE_ID));
        assertThat(result.getFaultInfo().getText(), equalTo(TEXT));
        assertThat(result.getFaultInfo().getVariables(), empty());
    }

    @Test
    public void shouldSupportPolicyExceptionWithVariables() {

        final PolicyException result = SubscriberThirdPartyExceptionUtils.newPolicyException(MESSAGE_ID,
                                                                                             TEXT,
                                                                                             new String[] {
                                                                                                     VARIABLE_1,
                                                                                                     VARIABLE_2 });
        assertThat(result.getFaultInfo().getMessageId(), equalTo(MESSAGE_ID));
        assertThat(result.getFaultInfo().getText(), equalTo(TEXT));
        assertThat(result.getFaultInfo().getVariables(),
                   containsInAnyOrder(VARIABLE_1, VARIABLE_2));
    }

    @Test
    public void shouldSupporServiceExceptionWithVariables() {

        final ServiceException result = SubscriberThirdPartyExceptionUtils.newServiceException(MESSAGE_ID,
                                                                                               TEXT,
                                                                                               new String[] {
                                                                                                       VARIABLE_1,
                                                                                                       VARIABLE_2 });
        assertThat(result.getFaultInfo().getMessageId(), equalTo(MESSAGE_ID));
        assertThat(result.getFaultInfo().getText(), equalTo(TEXT));
        assertThat(result.getFaultInfo().getVariables(),
                   containsInAnyOrder(VARIABLE_1, VARIABLE_2));
    }

}
