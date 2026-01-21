package com.organization.org_voting_system.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.organization.org_voting_system.entity.Election;
import com.organization.org_voting_system.entity.User;
import com.organization.org_voting_system.service.ElectionService;
import com.organization.org_voting_system.service.UserService;

@Controller
@RequestMapping("/election-officer")
@PreAuthorize("hasRole('ELECTION_OFFICER')")
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

        return "election-officer";
    }



    @GetMapping("/list-voters")
    public String listVoters(Model model, Principal principal) {
        User currentUser = userService.findByUsername(principal.getName());
        if (currentUser == null) {
            return "redirect:/login?error=user_not_found";
        }

        List<User> allUsers = userService.getAllUsers();

        model.addAttribute("user", currentUser);
        model.addAttribute("allUsers", allUsers);

        return "election-officer";
    }

    @GetMapping("/list-elected")
    public String listElected(Model model, Principal principal) {
        User currentUser = userService.findByUsername(principal.getName());
        if (currentUser == null) {
            return "redirect:/login?error=user_not_found";
        }

        // Assuming we need to get elected candidates or something similar
        // For now, placeholder
        model.addAttribute("user", currentUser);

        return "election-officer";
    }

    @GetMapping("/backup-restore")
    public String backupRestore(Model model, Principal principal) {
        User currentUser = userService.findByUsername(principal.getName());
        if (currentUser == null) {
            return "redirect:/login?error=user_not_found";
        }

        model.addAttribute("user", currentUser);

        return "election-officer";
    }



    @PostMapping("/election/{id}/close")
    public String closeElection(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            electionService.closeElection(id);
            redirectAttributes.addFlashAttribute("success", "Election closed successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to close election: " + e.getMessage());
        }
        return "redirect:/election-officer/dashboard";
    }

    // AJAX endpoints for dynamic content
    @GetMapping("/api/dashboard")
    @ResponseBody
    public String getDashboardContent(Principal principal) {
        // Return HTML fragment for dashboard
        return "<h2>All Elections</h2><table id='elections-table' class='elections-table'><thead><tr><th data-sort='title'>Title</th><th data-sort='status'>Status</th><th data-sort='startDate'>Start Date</th><th data-sort='endDate'>End Date</th><th>Actions</th></tr></thead><tbody>" +
               electionService.findAll().stream().map(election ->
                   "<tr data-election-id='" + election.getElectionId() + "'>" +
                   "<td>" + election.getTitle() + "</td>" +
                   "<td>" + election.getStatus() + "</td>" +
                   "<td>" + election.getStartDatetime().toString() + "</td>" +
                   "<td>" + election.getEndDatetime().toString() + "</td>" +
                   "<td>" + (election.getStatus().name().equals("ACTIVE") ? "<button class='close-election-btn' data-election-id='" + election.getElectionId() + "'>Close Election</button>" : "<span class='no-action'>No Action</span>") + "</td>" +
                   "</tr>"
               ).reduce("", String::concat) +
               "</tbody></table>";
    }

    @GetMapping("/api/election-management")
    @ResponseBody
    public String getElectionManagementContent(Principal principal) {
        return "<h2>Election Management</h2><p>Manage elections here. Add, edit, or delete elections.</p><button>Add New Election</button>";
    }

    @GetMapping("/api/list-voters")
    @ResponseBody
    public String getListVotersContent(Principal principal) {
        return "<div class='voters-container'>" +
               "<h2>List of Registered Voters</h2>" +
               "<div class='stats-card'>" +
               "<div class='stat-item'>" +
               "<h3>" + userService.getAllUsers().stream().filter(u -> u.getRole().getRoleName().name().equals("ROLE_VOTER")).count() + "</h3>" +
               "<p>Total Voters</p>" +
               "</div>" +
               "<div class='stat-item'>" +
               "<h3>" + userService.getAllUsers().stream().filter(u -> u.getRole().getRoleName().name().equals("ROLE_VOTER") && u.getHasVoted()).count() + "</h3>" +
               "<p>Have Voted</p>" +
               "</div>" +
               "<div class='stat-item'>" +
               "<h3>" + userService.getAllUsers().stream().filter(u -> u.getRole().getRoleName().name().equals("ROLE_VOTER") && !u.getHasVoted()).count() + "</h3>" +
               "<p>Not Voted Yet</p>" +
               "</div>" +
               "</div>" +
               "<table class='voters-table'>" +
               "<thead>" +
               "<tr>" +
               "<th>Student Number</th>" +
               "<th>Full Name</th>" +
               "<th>Email</th>" +
               "<th>Status</th>" +
               "</tr>" +
               "</thead>" +
               "<tbody>" +
               userService.getAllUsers().stream().filter(u -> u.getRole().getRoleName().name().equals("ROLE_VOTER")).map(user ->
                   "<tr>" +
                   "<td>" + user.getStudentNumber() + "</td>" +
                   "<td>" + user.getFullName() + "</td>" +
                   "<td>" + user.getEmail() + "</td>" +
                   "<td><span class='status-badge " + (user.getHasVoted() ? "voted" : "not-voted") + "'>" + (user.getHasVoted() ? "Voted" : "Not Voted") + "</span></td>" +
                   "</tr>"
               ).reduce("", String::concat) +
               "</tbody>" +
               "</table>" +
               "</div>";
    }

    @GetMapping("/api/list-elected")
    @ResponseBody
    public String getListElectedContent(Principal principal) {
        return "<div class='elected-container'>" +
               "<h2>Election Results</h2>" +
               "<div class='results-summary'>" +
               "<p>Current election results and elected candidates will be displayed here.</p>" +
               "<div class='result-card'>" +
               "<h3>Positions</h3>" +
               "<p>Elected candidates by position will be listed here.</p>" +
               "</div>" +
               "</div>" +
               "<div class='actions'>" +
               "<button class='btn-primary'>Generate Report</button>" +
               "<button class='btn-secondary'>Export Results</button>" +
               "</div>" +
               "</div>";
    }

    @GetMapping("/api/backup-restore")
    @ResponseBody
    public String getBackupRestoreContent(Principal principal) {
        return "<div class='backup-container'>" +
               "<div class='backup-section'>" +
               "<h3>Create System Backup</h3>" +
               "<p>Create a complete backup of all system data including elections, users, votes, and configurations.</p>" +
               "<button class='btn-primary'>Create Backup</button>" +
               "<div class='backup-status' id='backup-status'>No recent backups</div>" +
               "</div>" +
               "<div class='restore-section'>" +
               "<h3>Restore from Backup</h3>" +
               "<p>Restore system data from a previously created backup file. This will overwrite current data.</p>" +
               "<input type='file' id='backup-file' accept='.zip,.sql' style='margin-bottom: 15px;'>" +
               "<button class='btn-secondary'>Restore Backup</button>" +
               "<div class='restore-status' id='restore-status'>No restore operations</div>" +
               "</div>" +
               "<div class='warning'>" +
               "<p><strong>⚠️ Warning:</strong> Restoring from backup will permanently overwrite current system data. Make sure to create a backup before restoring.</p>" +
               "</div>" +
               "</div>";
    }

    @GetMapping("/api/user-management")
    @ResponseBody
    public String getUserManagementContent(Principal principal) {
        return "<h2>User Management</h2><table><thead><tr><th>Username</th><th>Role</th><th>Actions</th></tr></thead><tbody>" +
               userService.getAllUsers().stream().map(user ->
                   "<tr><td>" + user.getUsername() + "</td><td>" + user.getRole().getRoleName() + "</td><td><button>Edit</button><button>Delete</button></td></tr>"
               ).reduce("", String::concat) +
               "</tbody></table>";
    }
}
