package com.vennetics.microservices.common.api.model.admin.v1.application;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;
import java.util.Objects;

/**
 * RoleList pojo for use in REST calls
 */
@XmlRootElement(name = "roleList")
public final class RoleList {

    @XmlElement(name = "roles")
    private List<Role> roles;

    public RoleList() {
        // For JSON deserialsation.
    }

    public RoleList(final List<Role> roles) {
        this.roles = roles;
    }

    public List<Role> getRoles() {
        return roles;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final RoleList roleList = (RoleList) o;
        return Objects.equals(roles, roleList.roles);
    }

    @Override
    public int hashCode() {
        return Objects.hash(roles);
    }

    @Override
    public String toString() {
        return "RoleList{" + "roles=" + roles + '}';
    }
}
