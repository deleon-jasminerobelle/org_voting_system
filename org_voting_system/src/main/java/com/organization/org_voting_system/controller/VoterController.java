package com.organization.org_voting_system.controller;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.organization.org_voting_system.entity.Election;
import com.organization.org_voting_system.entity.User;
import com.organization.org_voting_system.service.ElectionService;
import com.organization.org_voting_system.service.UserService;
import com.organization.org_voting_system.service.VoteService;

@Controller
@RequestMapping("/voter")
@PreAuthorize("hasRole('VOTER')")
public class VoterController {

    @Autowired
    private ElectionService electionService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private VoteService voteService;

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
        List<Election> activeElections = new ArrayList<>();
        try {
            activeElections = electionService.getActiveElections();
        } catch (Exception e) {
            // ignore
        }
        List<Election> upcomingElections = new ArrayList<>();
        try {
            upcomingElections = electionService.getUpcomingElections();
        } catch (Exception e) {
            // ignore
        }
        List<Election> votedElections = new ArrayList<>();
        try {
            votedElections = voteService.getElectionsVotedByUser(currentUser.getUserId());
        } catch (Exception e) {
            // ignore
        }

        model.addAttribute("user", currentUser);
        model.addAttribute("activeElections", activeElections);
        model.addAttribute("upcomingElections", upcomingElections);
        model.addAttribute("votedElections", votedElections);
        model.addAttribute("currentTime", LocalDateTime.now());

        return "voter/voter-dashboard";
    }

    @GetMapping("/home")
    public String home(Model model, Principal principal) {
        User currentUser = userService.findByUsername(principal.getName());
        if (currentUser == null) {
            return "redirect:/login?error=user_not_found";
        }
        List<Election> activeElections = electionService.getActiveElections();
        List<Election> upcomingElections = electionService.getUpcomingElections();

        model.addAttribute("user", currentUser);
        model.addAttribute("activeElections", activeElections);
        model.addAttribute("upcomingElections", upcomingElections);
        model.addAttribute("currentTime", LocalDateTime.now());

        return "voter/home";
    }

    @GetMapping("vote-now")
    public String voteNow(Model model, Principal principal) {
        User currentUser = userService.findByUsername(principal.getName());
        if (currentUser == null) {
            return "redirect:/login?error=user_not_found";
        }
        List<Election> activeElections = electionService.getActiveElections();
        List<Election> upcomingElections = electionService.getUpcomingElections();

        model.addAttribute("user", currentUser);
        model.addAttribute("activeElections", activeElections);
        model.addAttribute("upcomingElections", upcomingElections);
        model.addAttribute("currentTime", LocalDateTime.now());

        return "voter/vote-now";
    }

    @GetMapping("/leading")
    public String leading(Model model, Principal principal) {
        User currentUser = userService.findByUsername(principal.getName());
        if (currentUser == null) {
            return "redirect:/login?error=user_not_found";
        }
        List<Election> activeElections = electionService.getActiveElections();

        model.addAttribute("user", currentUser);
        model.addAttribute("activeElections", activeElections);

        return "voter/leading";
    }

    @GetMapping("/voting-status")
    public String votingStatus(Model model, Principal principal) {
        User currentUser = userService.findByUsername(principal.getName());
        if (currentUser == null) {
            return "redirect:/login?error=user_not_found";
        }
        List<Election> votedElections = voteService.getElectionsVotedByUser(currentUser.getUserId());

        model.addAttribute("user", currentUser);
        model.addAttribute("votedElections", votedElections);

        return "voter/voting-status";
    }

    @GetMapping("/election/{electionId}")
    public String viewElection(@PathVariable Long electionId, Model model, Principal principal) {
        User currentUser = userService.findByUsername(principal.getName());
        if (currentUser == null) {
            return "redirect:/login?error=user_not_found";
        }
        Election election = electionService.findById(electionId);

        if (election == null) {
            return "redirect:/voter/dashboard?error=election_not_found";
        }

        // Check if election is active
        if (!election.getStatus().equals(Election.Status.ACTIVE)) {
            return "redirect:/voter/dashboard?error=election_not_active";
        }

        // Check if user has already voted
        boolean hasVoted = voteService.hasUserVotedInElection(currentUser.getUserId(), electionId);

        model.addAttribute("election", election);
        model.addAttribute("hasVoted", hasVoted);
        model.addAttribute("user", currentUser);

        return "voter/election";
    }

    @PostMapping("/vote")
    public String submitVote(@RequestParam Long electionId, 
                           @RequestParam Long positionId,
                           @RequestParam Long candidateId,
                           Principal principal,
                           RedirectAttributes redirectAttributes) {
        try {
            User currentUser = userService.findByUsername(principal.getName());
            
            // Validate election is active
            Election election = electionService.findById(electionId);
            if (!election.getStatus().equals(Election.Status.ACTIVE)) {
                redirectAttributes.addFlashAttribute("error", "Election is not active");
                return "redirect:/voter/dashboard";
            }
            
            // Check if user has already voted for this position
            if (voteService.hasUserVotedForPosition(currentUser.getUserId(), electionId, positionId)) {
                redirectAttributes.addFlashAttribute("error", "You have already voted for this position");
                return "redirect:/voter/election/" + electionId;
            }
            
            // Submit vote
            voteService.submitVote(electionId, positionId, candidateId, currentUser.getUserId());
            
            redirectAttributes.addFlashAttribute("success", "Your vote has been submitted successfully");
            return "redirect:/voter/election/" + electionId;
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to submit vote: " + e.getMessage());
            return "redirect:/voter/election/" + electionId;
        }
    }

    @GetMapping("/voting-history")
    public String votingHistory(Model model, Principal principal) {
        User currentUser = userService.findByUsername(principal.getName());
        List<Election> votedElections = voteService.getElectionsVotedByUser(currentUser.getUserId());
        
        model.addAttribute("user", currentUser);
        model.addAttribute("votedElections", votedElections);
        
        return "voter/voting-history";
    }
}