package com.vennetics.bell.sam.adapters.messaging.smpp.support.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "smpp")
public class SmppProperties {

    private SmppConnectionProperties primarySmppConnection;

    private SmppConnectionProperties secondarySmppConnection;

    private AddressAttributes sourceAddress;

    private AddressAttributes destinationAddress;

    private String addressRange;

    private int priority;

    private String bindType;

    private int windowSize;

    private String systemId;

    private String password;

    private long requestExpiryTimeout;

    private long windowMonitorInterval;

    private boolean receiptingEnabled;

    private int maxConcurrentSessions;

    private long enquireLinkPollingInterval;
    
    private ThrottlingRates outBoundThrottlingRate;

    private ThrottlingRates inBoundThrottlingRate;

    private long enquireLinkTimeoutMilliseconds;

    private long submitMessageTimeoutMilliseconds;
    
    private long smppUnbindTimeoutInMilliseconds;

    public SmppConnectionProperties getPrimarySmppConnection() {
        return primarySmppConnection;
    }

    public void setPrimarySmppConnection(final SmppConnectionProperties primarySmppConnection) {
        this.primarySmppConnection = primarySmppConnection;
    }

    public SmppConnectionProperties getSecondarySmppConnection() {
        return secondarySmppConnection;
    }

    public void setSecondarySmppConnection(final SmppConnectionProperties secondarySmppConnection) {
        this.secondarySmppConnection = secondarySmppConnection;
    }

    public String getBindType() {
        return bindType;
    }

    public void setBindType(final String bindType) {
        this.bindType = bindType;
    }

    public int getWindowSize() {
        return windowSize;
    }

    public void setWindowSize(final int windowSize) {
        this.windowSize = windowSize;
    }

    public String getSystemId() {
        return systemId;
    }

    public void setSystemId(final String systemId) {
        this.systemId = systemId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(final String password) {
        this.password = password;
    }

    public long getRequestExpiryTimeout() {
        return requestExpiryTimeout;
    }

    public void setRequestExpiryTimeout(final long requestExpiryTimeout) {
        this.requestExpiryTimeout = requestExpiryTimeout;
    }

    public long getWindowMonitorInterval() {
        return windowMonitorInterval;
    }

    public void setWindowMonitorInterval(final long windowMonitorInterval) {
        this.windowMonitorInterval = windowMonitorInterval;
    }

    public int getMaxConcurrentSessions() {
        return maxConcurrentSessions;
    }

    public void setMaxConcurrentSessions(final int maxConcurrentSessions) {
        this.maxConcurrentSessions = maxConcurrentSessions;
    }

    public long getEnquireLinkPollingInterval() {
        return enquireLinkPollingInterval;
    }

    public void setEnquireLinkPollingInterval(final long enquireLinkPollingInterval) {
        this.enquireLinkPollingInterval = enquireLinkPollingInterval;
    }

    public boolean isReceiptingEnabled() {
        return receiptingEnabled;
    }

    public void setReceiptingEnabled(final boolean receiptingEnabled) {
        this.receiptingEnabled = receiptingEnabled;
    }

    public String getAddressRange() {
        return addressRange;
    }

    public void setAddressRange(final String addressRange) {
        this.addressRange = addressRange;
    }

    public AddressAttributes getSourceAddress() {
        return sourceAddress;
    }

    public void setSourceAddress(final AddressAttributes sourceAddress) {
        this.sourceAddress = sourceAddress;
    }

    public AddressAttributes getDestinationAddress() {
        return destinationAddress;
    }

    public void setDestinationAddress(final AddressAttributes destinationAddress) {
        this.destinationAddress = destinationAddress;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(final int priority) {
        this.priority = priority;
    }
    
    public ThrottlingRates getOutBoundThrottlingRate() {
        return outBoundThrottlingRate;
    }

    public void setOutBoundThrottlingRate(final ThrottlingRates outBoundThrottlingRate) {
        this.outBoundThrottlingRate = outBoundThrottlingRate;
    }

    public ThrottlingRates getInBoundThrottlingRate() {
        return inBoundThrottlingRate;
    }

    public void setInBoundThrottlingRate(final ThrottlingRates inBoundThrottlingRate) {
        this.inBoundThrottlingRate = inBoundThrottlingRate;
    }

    public long getEnquireLinkTimeoutMilliseconds() {
        return enquireLinkTimeoutMilliseconds;
    }

    public void setEnquireLinkTimeoutMilliseconds(final long enquireLinkTimeoutMilliseconds) {
        this.enquireLinkTimeoutMilliseconds = enquireLinkTimeoutMilliseconds;
    }

    public long getSubmitMessageTimeoutMilliseconds() {
        return submitMessageTimeoutMilliseconds;
    }

    public void setSubmitMessageTimeoutMilliseconds(final long submitMessageTimeoutMilliseconds) {
        this.submitMessageTimeoutMilliseconds = submitMessageTimeoutMilliseconds;
    }

    public long getSmppUnbindTimeoutInMilliseconds() {
        return smppUnbindTimeoutInMilliseconds;
    }

    public void setSmppUnbindTimeoutInMilliseconds(final long smppUnbindTimeoutInMilliseconds) {
        this.smppUnbindTimeoutInMilliseconds = smppUnbindTimeoutInMilliseconds;
    }

    public static class ThrottlingRates {

        private long intervalInMilliseconds;

        private int maxTasksPerInterval;

        private long throttlingRequestTimeoutMilliseconds;

        public long getIntervalInMilliseconds() {
            return intervalInMilliseconds;
        }

        public void setIntervalInMilliseconds(final long intervalInMilliseconds) {
            this.intervalInMilliseconds = intervalInMilliseconds;
        }

        public int getMaxTasksPerInterval() {
            return maxTasksPerInterval;
        }

        public void setMaxTasksPerInterval(final int maxTasksPerInterval) {
            this.maxTasksPerInterval = maxTasksPerInterval;
        }

        public long getThrottlingRequestTimeoutMilliseconds() {
            return throttlingRequestTimeoutMilliseconds;
        }

        public void setThrottlingRequestTimeoutMilliseconds(final long throttlingRequestTimeoutMilliseconds) {
            this.throttlingRequestTimeoutMilliseconds = throttlingRequestTimeoutMilliseconds;
        }

        @Override
        public String toString() {
            return "ThrottlingRates [" + "intervalInMilliseconds=" + intervalInMilliseconds
                            + ", maxTasksPerInterval=" + maxTasksPerInterval
                            + ", throttlingRequestTimeoutMilliseconds="
                            + throttlingRequestTimeoutMilliseconds + ']';
        }
    }


    public static class AddressAttributes {

        private byte ton;

        private byte npi;

        public byte getTon() {
            return ton;
        }

        public void setTon(final byte ton) {
            this.ton = ton;
        }

        public byte getNpi() {
            return npi;
        }

        public void setNpi(final byte npi) {
            this.npi = npi;
        }

        @Override
        public String toString() {
            return "AddressAttributes [" + "ton=" + ton + ", npi=" + npi + ']';
        }
    }

    public static class SmppConnectionProperties {

        private int port;

        private String host;

        private long connectionTimeout;

        public int getPort() {
            return port;
        }

        public void setPort(final int port) {
            this.port = port;
        }

        public String getHost() {
            return host;
        }

        public void setHost(final String host) {
            this.host = host;
        }

        public long getConnectionTimeout() {
            return connectionTimeout;
        }

        public void setConnectionTimeout(final long connectionTimeout) {
            this.connectionTimeout = connectionTimeout;
        }

        @Override
        public String toString() {
            return "SmppConnectionProperties [" + "port=" + port + ", host='" + host + '\''
                            + ", connectionTimeout=" + connectionTimeout + ']';
        }
    }

    @Override
    public String toString() {
        return "SmppProperties [" + "primarySmppConnection=" + primarySmppConnection
                        + ", secondarySmppConnection=" + secondarySmppConnection
                        + ", sourceAddress=" + sourceAddress + ", destinationAddress="
                        + destinationAddress + ", addressRange='" + addressRange + '\''
                        + ", priority=" + priority + ", bindType='" + bindType + '\''
                        + ", windowSize=" + windowSize + ", systemId='" + systemId + '\''
                        + ", requestExpiryTimeout=" + requestExpiryTimeout
                        + ", windowMonitorInterval=" + windowMonitorInterval
                        + ", receiptingEnabled=" + receiptingEnabled + ", maxConcurrentSessions="
                        + maxConcurrentSessions + ", enquireLinkPollingInterval="
                        + enquireLinkPollingInterval + ']';
    }
}
