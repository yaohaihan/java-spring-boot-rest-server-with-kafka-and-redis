package org.example.service.model;

import java.util.Set;

public class UserAuthorizationResponse {

    public Set<String> roles;

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }
}
