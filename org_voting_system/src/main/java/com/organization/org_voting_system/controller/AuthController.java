package com.organization.org_voting_system.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.organization.org_voting_system.entity.User;
import com.organization.org_voting_system.service.UserService;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String register(User user, Model model, RedirectAttributes redirectAttributes) {
        try {
            if (userService.existsByStudentNumber(user.getStudentNumber())) {
                model.addAttribute("error", "Student Number already registered");
                return "register";
            }
            if (user.getUsername() == null || user.getUsername().isEmpty()) {
                user.setUsername(user.getStudentNumber());
            }
            if (userService.existsByUsername(user.getUsername())) {
                model.addAttribute("error", "Username already exists");
                return "register";
            }
            if (userService.existsByEmail(user.getEmail())) {
                model.addAttribute("error", "Email already registered");
                return "register";
            }
            userService.registerUser(user);
            redirectAttributes.addFlashAttribute("success", "Registration successful! Please login.");
            return "redirect:/login";
        } catch (Exception e) {
            model.addAttribute("error", "Registration failed: " + e.getMessage());
            return "register";
        }
    }

    @GetMapping("/forgot-password")
    public String forgotPassword() {
        return "forgot-password";
    }

    @PostMapping("/forgot-password")
    public String resetPassword(@RequestParam String email, Model model) {
        // TODO: Implement password reset logic
        model.addAttribute("message", "Password reset link sent to your email");
        return "forgot-password";
    }
}
