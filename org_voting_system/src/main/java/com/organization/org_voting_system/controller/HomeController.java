package com.organization.org_voting_system.controller;

import java.security.Principal;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home(Authentication authentication, Principal principal) {
        if (authentication != null && authentication.isAuthenticated()) {
            // Check user role and redirect accordingly
            if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
                return "redirect:/admin/dashboard";
            } else if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ELECTION_OFFICER"))) {
                return "redirect:/election-officer/dashboard";
            } else if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_VOTER"))) {
                return "redirect:/voter/dashboard";
            }
        }
        return "landing";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/privacy-policy")
    public String privacyPolicy() {
        return "privacy-policy";
    }

    @GetMapping("/terms-of-service")
    public String termsOfService() {
        return "terms-of-service";
    }
}
