package com.organization.org_voting_system.controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public String handleException(Exception e, Model model) {
        model.addAttribute("errorMessage", "An unexpected error occurred: " + e.getMessage());
        model.addAttribute("errorCode", "500");
        return "error";
    }

    @ExceptionHandler(RuntimeException.class)
    public String handleRuntimeException(RuntimeException e, Model model) {
        model.addAttribute("errorMessage", "A runtime error occurred: " + e.getMessage());
        model.addAttribute("errorCode", "500");
        return "error";
    }
}
