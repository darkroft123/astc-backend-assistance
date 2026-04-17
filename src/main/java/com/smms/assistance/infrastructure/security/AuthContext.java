package com.smms.assistance.infrastructure.security;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.jwt.JsonWebToken;
import java.util.UUID;

@RequestScoped
public class AuthContext {

    @Inject
    JsonWebToken jwt;

    public UUID getUserId() {
        String sub = jwt.getSubject();
        if (sub == null) {
            throw new SecurityException("Missing subject in JWT");
        }
        return UUID.fromString(sub);
    }

    public String getEmail() {
        return jwt.getClaim("email");
    }

    public String getRole() {
        return jwt.getClaim("role");
    }

    public void assertTeamMember() {
        if (!"TEAM_MEMBER".equals(getRole())) {
            throw new SecurityException("Access denied: requires TEAM_MEMBER role");
        }
    }
}
