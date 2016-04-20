package com.vennetics.microservices.common.api.model.admin.v1.application;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;
import java.util.Objects;

/**
 * ApplicationList pojo for use in REST calls
 */
@XmlRootElement(name = "applicationList")
public final class ApplicationUserDetailsList {

    @XmlElement(name = "applications")
    private List<ApplicationUserDetails> applications;

    protected ApplicationUserDetailsList() {
        // For JSON deserialsation.
    }

    public ApplicationUserDetailsList(final List<ApplicationUserDetails> applications) {
        this.applications = applications;
    }

    public List<ApplicationUserDetails> getApplications() {
        return applications;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final ApplicationUserDetailsList that = (ApplicationUserDetailsList) o;
        return Objects.equals(applications, that.applications);
    }

    @Override
    public int hashCode() {
        return Objects.hash(applications);
    }

    @Override
    public String toString() {
        return "ApplicationList{" + "applications=" + applications + '}';
    }
}
