package com.vennetics.bell.sam.sms.common.address;

/**
 * Created on 08/02/2016.
 */
public class Address {

    private String suppliedAddress;

    private String e164Address;

    private boolean isRejected;

    public String getSuppliedAddress() {
        return suppliedAddress;
    }

    public void setSuppliedAddress(final String suppliedAddress) {
        this.suppliedAddress = suppliedAddress;
    }

    public String getE164Address() {
        return e164Address;
    }

    public void setE164Address(final String e164Address) {
        this.e164Address = e164Address;
    }

    public boolean isRejected() {
        return isRejected;
    }

    public void setRejected(final boolean rejected) {
        isRejected = rejected;
    }

}
