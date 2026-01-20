package com.organization.org_voting_system.config;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {
        String role = authentication.getAuthorities().iterator().next().getAuthority();
        switch (role) {
            case "ROLE_VOTER" -> response.sendRedirect("/voter/dashboard");
            case "ROLE_ADMIN" -> response.sendRedirect("/admin/dashboard");
            case "ROLE_ELECTION_OFFICER" -> response.sendRedirect("/election-officer/dashboard");
            default -> response.sendRedirect("/");
        }
    }
}
