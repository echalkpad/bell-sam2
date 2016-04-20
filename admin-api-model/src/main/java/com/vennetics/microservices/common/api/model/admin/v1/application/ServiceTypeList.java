package com.vennetics.microservices.common.api.model.admin.v1.application;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;
import java.util.Objects;

/**
 * ServiceTypeList pojo for use in REST calls
 */
@XmlRootElement(name = "serviceTypeList")
public final class ServiceTypeList {

    @XmlElement(name = "serviceTypes")
    private List<ServiceType> serviceTypes;

    public ServiceTypeList() {
        // For JSON deserialsation.
    }

    public ServiceTypeList(final List<ServiceType> serviceTypes) {
        this.serviceTypes = serviceTypes;
    }

    public List<ServiceType> getServiceTypes() {
        return serviceTypes;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final ServiceTypeList that = (ServiceTypeList) o;
        return Objects.equals(serviceTypes, that.serviceTypes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(serviceTypes);
    }

    @Override
    public String toString() {
        return "ServiceTypeList{" + "serviceTypes=" + serviceTypes + '}';
    }
}
