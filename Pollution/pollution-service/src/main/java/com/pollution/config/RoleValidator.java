package com.pollution.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

@Component
public class RoleValidator {
    public boolean hasRole(HttpServletRequest request, String expectedRole) {
        String role = request.getHeader("X-User-Role");
        return role != null && role.equalsIgnoreCase(expectedRole);
    }
}
