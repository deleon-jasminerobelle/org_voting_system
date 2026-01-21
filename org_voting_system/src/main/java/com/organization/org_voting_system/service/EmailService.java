package com.organization.org_voting_system.service;

import org.springframework.stereotype.Service;

@Service
public class EmailService {

    public void sendPasswordResetEmail(String email, String resetUrl) {
        // TODO: Implement email sending logic
        // For now, just log the email details
        System.out.println("Sending password reset email to: " + email);
        System.out.println("Reset URL: " + resetUrl);
    }
}
