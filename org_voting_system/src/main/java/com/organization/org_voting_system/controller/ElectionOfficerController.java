package com.organization.org_voting_system.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.organization.org_voting_system.entity.Election;
import com.organization.org_voting_system.entity.User;
import com.organization.org_voting_system.service.ElectionService;
import com.organization.org_voting_system.service.UserService;

@Controller
@RequestMapping("/election-officer")
// @PreAuthorize("hasRole('ELECTION_OFFICER')")
public class ElectionOfficerController {

    @Autowired
    private UserService userService;

    @Autowired
    private ElectionService electionService;

    @GetMapping("/dashboard")
    public String dashboard(Model model, Principal principal) {
        User currentUser = userService.findByUsername(principal.getName());
        if (currentUser == null) {
            return "redirect:/login?error=user_not_found";
        }

        List<Election> allElections = electionService.findAll();

        model.addAttribute("user", currentUser);
        model.addAttribute("allElections", allElections);

        return "election-officer.html";
    }
}
