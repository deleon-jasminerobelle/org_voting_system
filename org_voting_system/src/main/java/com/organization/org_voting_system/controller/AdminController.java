package com.organization.org_voting_system.controller;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.organization.org_voting_system.entity.Candidate;
import com.organization.org_voting_system.entity.Election;
import com.organization.org_voting_system.entity.Position;
import com.organization.org_voting_system.entity.User;
import com.organization.org_voting_system.service.CandidateService;
import com.organization.org_voting_system.service.ElectionService;
import com.organization.org_voting_system.service.PositionService;
import com.organization.org_voting_system.service.UserService;

@Controller
@RequestMapping("/admin")
// @PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private ElectionService electionService;

    @Autowired
    private PositionService positionService;

    @Autowired
    private CandidateService candidateService;

    @GetMapping("/dashboard")
    public String dashboard(Model model, Principal principal) {
        User currentUser = userService.findByUsernameOptional(principal.getName())
            .orElse(userService.findByEmail(principal.getName()).orElse(null));
        if (currentUser == null) {
            return "redirect:/login?error=user_not_found";
        }
        if (Boolean.FALSE.equals(currentUser.getIsActive())) {
            return "redirect:/login?error=user_inactive";
        }

        List<Election> allElections = electionService.findAll();
        List<User> allUsers = userService.getAllUsers();
        List<Position> allPositions = positionService.findAll();
        List<Candidate> allCandidates = candidateService.findAll();

        model.addAttribute("user", currentUser);
        model.addAttribute("allElections", allElections);
        model.addAttribute("allUsers", allUsers);
        model.addAttribute("allPositions", allPositions);
        model.addAttribute("allCandidates", allCandidates);

        return "admin-dashboard";
    }

    // ================= ELECTION CRUD =================

    @PostMapping("/election/create")
    public String createElection(@RequestParam String title,
                                @RequestParam String description,
                                @RequestParam String startDatetime,
                                @RequestParam String endDatetime,
                                Principal principal,
                                RedirectAttributes redirectAttributes) {
        try {
            User currentUser = userService.findByUsernameOptional(principal.getName())
                .orElse(userService.findByEmail(principal.getName()).orElse(null));

            Election election = new Election();
            election.setTitle(title);
            election.setDescription(description);
            election.setStartDatetime(LocalDateTime.parse(startDatetime));
            election.setEndDatetime(LocalDateTime.parse(endDatetime));
            election.setCreatedBy(currentUser);

            electionService.createElection(election);
            redirectAttributes.addFlashAttribute("success", "Election created successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to create election: " + e.getMessage());
        }
        return "redirect:/admin/dashboard";
    }

    @PostMapping("/election/{id}/close")
    public String closeElection(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            electionService.closeElection(id);
            redirectAttributes.addFlashAttribute("success", "Election closed successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to close election: " + e.getMessage());
        }
        return "redirect:/admin/dashboard";
    }

    @PostMapping("/election/{id}/delete")
    public String deleteElection(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            electionService.deleteElection(id);
            redirectAttributes.addFlashAttribute("success", "Election deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to delete election: " + e.getMessage());
        }
        return "redirect:/admin/dashboard";
    }

    // ================= POSITION CRUD =================

    @PostMapping("/position/create")
    public String createPosition(@RequestParam String title,
                                @RequestParam String description,
                                @RequestParam Long electionId,
                                RedirectAttributes redirectAttributes) {
        try {
            Election election = electionService.findById(electionId);
            if (election == null) {
                redirectAttributes.addFlashAttribute("error", "Election not found!");
                return "redirect:/admin/dashboard";
            }

            Position position = new Position();
            position.setPositionName(title);
            position.setElection(election);

            positionService.save(position);
            redirectAttributes.addFlashAttribute("success", "Position created successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to create position: " + e.getMessage());
        }
        return "redirect:/admin/dashboard";
    }

    @PostMapping("/position/{id}/delete")
    public String deletePosition(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            positionService.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Position deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to delete position: " + e.getMessage());
        }
        return "redirect:/admin/dashboard";
    }

    // ================= CANDIDATE CRUD =================

    @PostMapping("/candidate/create")
    public String createCandidate(@RequestParam String fullName,
                                 @RequestParam String description,
                                 @RequestParam Long positionId,
                                 RedirectAttributes redirectAttributes) {
        try {
            Position position = positionService.findById(positionId).orElse(null);
            if (position == null) {
                redirectAttributes.addFlashAttribute("error", "Position not found!");
                return "redirect:/admin/dashboard";
            }

            Candidate candidate = new Candidate();
            candidate.setFullName(fullName);
            candidate.setDescription(description);
            candidate.setPosition(position);

            candidateService.save(candidate);
            redirectAttributes.addFlashAttribute("success", "Candidate added successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to add candidate: " + e.getMessage());
        }
        return "redirect:/admin/dashboard";
    }

    @PostMapping("/candidate/{id}/delete")
    public String deleteCandidate(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            candidateService.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Candidate deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to delete candidate: " + e.getMessage());
        }
        return "redirect:/admin/dashboard";
    }
}
