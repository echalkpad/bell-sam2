package com.vennetics.microservices.common.api.model.admin.v1.application;

import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * Role pojo for use in REST calls
 */
@XmlRootElement(name = "role")
public final class Role {

    @NotBlank
    @XmlElement(name = "roleId")
    private String roleId;
    @NotBlank
    @XmlElement(name = "description")
    private String description;
    @NotEmpty
    @XmlElement(name = "serviceTypeIds")
    private Set<String> serviceTypeIds;
    @NotEmpty
    @XmlElement(name = "applicationIds")
    private Set<UUID> applicationIds;

    public Role() {
        // For JSON deserialsation.
    }

    public Role(final String roleId,
                final String description,
                final Set<String> serviceTypeIds,
                final Set<UUID> applicationIds) {
        this.roleId = roleId;
        this.description = description;
        this.serviceTypeIds = serviceTypeIds;
        this.applicationIds = applicationIds;
    }

    public String getRoleId() {
        return roleId;
    }

    public String getDescription() {
        return description;
    }

    public Set<String> getServiceTypeIds() {
        return serviceTypeIds;
    }

    public Set<UUID> getApplicationIds() {
        return applicationIds;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Role role = (Role) o;
        return Objects.equals(roleId, role.roleId)
            && Objects.equals(description, role.description)
            && Objects.equals(serviceTypeIds, role.serviceTypeIds)
            && Objects.equals(applicationIds, role.applicationIds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(roleId, description, serviceTypeIds, applicationIds);
    }

    @Override
    public String toString() {
        return "Role{" + "roleId='" + roleId + '\''
            + ", description='" + description + '\''
            + ", serviceTypeIds=" + serviceTypeIds
            + ", applicationIds=" + applicationIds + '}';
    }
}
