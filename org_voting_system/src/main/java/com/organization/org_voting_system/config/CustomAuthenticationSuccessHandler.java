package com.organization.org_voting_system.config;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomAuthenticationSuccessHandler
        implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) throws IOException {

        System.out.println("Authorities:");
        authentication.getAuthorities().forEach(a -> System.out.println(a.getAuthority()));

        if (authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            response.sendRedirect("/admin/dashboard");
            return;
        }

        if (authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_VOTER"))) {
            response.sendRedirect("/voter/dashboard");
            return;
        }

        if (authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ELECTION_OFFICER"))) {
            response.sendRedirect("/election-officer/dashboard");
            return;
        }

        response.sendRedirect("/login?error=role");
    }
}
