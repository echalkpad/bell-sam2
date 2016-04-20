package com.vennetics.microservices.common.api.model.admin.v1.application;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;
import java.util.Objects;

/**
 * ProtocolTypeList pojo for use in REST calls
 */
@XmlRootElement(name = "protocolTypeList")
public final class ProtocolTypeList {

    @XmlElement(name = "protocolTypes")
    private List<ProtocolType> protocolTypes;

    public ProtocolTypeList() {
        // For JSON deserialsation.
    }

    public ProtocolTypeList(final List<ProtocolType> protocolTypes) {
        this.protocolTypes = protocolTypes;
    }

    public List<ProtocolType> getProtocolTypes() {
        return protocolTypes;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final ProtocolTypeList that = (ProtocolTypeList) o;
        return Objects.equals(protocolTypes, that.protocolTypes);
    }

    @Override public int hashCode() {
        return Objects.hash(protocolTypes);
    }

    @Override public String toString() {
        return "ProtocolTypeList{" + "protocolTypes=" + protocolTypes + '}';
    }
}
