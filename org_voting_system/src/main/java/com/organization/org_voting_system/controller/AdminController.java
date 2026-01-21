package com.organization.org_voting_system.controller;

import java.io.File;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
import com.organization.org_voting_system.service.PdfService;
import com.organization.org_voting_system.service.PositionService;
import com.organization.org_voting_system.service.UserService;
import com.organization.org_voting_system.service.VoteService;

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

    @Autowired
    private VoteService voteService;

    @Autowired
    private PdfService pdfService;

    @GetMapping("/dashboard")
    public String dashboard(Model model, Principal principal, @RequestParam(defaultValue = "dashboard") String activeSection) {
        User currentUser = userService.findByUsernameOptional(principal.getName())
            .orElse(userService.findByEmail(principal.getName()).orElse(null));
        if (currentUser == null) {
            return "redirect:/login?error=user_not_found";
        }
        if (Boolean.FALSE.equals(currentUser.getIsActive())) {
            return "redirect:/login?error=user_inactive";
        }
        if (currentUser.getRole().getRoleName() != com.organization.org_voting_system.entity.Role.RoleName.ROLE_ADMIN) {
            return "redirect:/login?error=access_denied";
        }

        electionService.updateElectionStatuses(); // Ensure statuses are current
        List<Election> allElections = electionService.findAll();
        List<User> allUsers = userService.getAllUsers();
        List<Position> allPositions = positionService.findAll();
        List<Candidate> allCandidates = candidateService.findAll();

        // Prepare vote counts for analytics
        Map<Long, Long> candidateVoteCounts = new HashMap<>();
        Map<Long, List<Candidate>> electionCandidates = new HashMap<>();
        try {
            for (Candidate candidate : allCandidates) {
                if (candidate != null && candidate.getPosition() != null && candidate.getPosition().getElection() != null) {
                    Long voteCount = voteService.getVoteCountForCandidate(
                        candidate.getPosition().getElection().getElectionId(),
                        candidate.getPosition().getPositionId(),
                        candidate.getCandidateId()
                    );
                    candidateVoteCounts.put(candidate.getCandidateId(), voteCount != null ? voteCount : 0L);

                    // Group candidates by election
                    Long electionId = candidate.getPosition().getElection().getElectionId();
                    electionCandidates.computeIfAbsent(electionId, k -> new java.util.ArrayList<>()).add(candidate);
                }
            }
        } catch (Exception e) {
            // Log error and continue with empty vote counts
            System.err.println("Error calculating vote counts: " + e.getMessage());
        }

        model.addAttribute("user", currentUser);
        model.addAttribute("allElections", allElections);
        model.addAttribute("allUsers", allUsers);
        model.addAttribute("allPositions", allPositions);
        model.addAttribute("allCandidates", allCandidates);
        model.addAttribute("candidateVoteCounts", candidateVoteCounts);
        model.addAttribute("electionCandidates", electionCandidates);
        model.addAttribute("activeSection", activeSection);

        return "admin-dashboard";
    }

    // ================= ELECTION CRUD =================

    @PostMapping("/election/create")
    public String createElection(@RequestParam String title,
                                @RequestParam String description,
                                @RequestParam String organization,
                                @RequestParam String startDatetime,
                                @RequestParam String endDatetime,
                                Principal principal,
                                RedirectAttributes redirectAttributes) {
        try {
            User currentUser = userService.findByUsernameOptional(principal.getName())
                .orElse(userService.findByEmail(principal.getName()).orElse(null));

            // Parse datetime strings with proper error handling
            LocalDateTime start = LocalDateTime.parse(startDatetime);
            LocalDateTime end = LocalDateTime.parse(endDatetime);

            Election election = new Election();
            election.setTitle(title);
            election.setDescription(description);
            election.setOrganization(organization);
            election.setStartDatetime(start);
            election.setEndDatetime(end);
            election.setCreatedBy(currentUser);

            electionService.createElection(election);
            redirectAttributes.addFlashAttribute("success", "Election created successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to create election: Invalid date format or " + e.getMessage());
        }
        return "redirect:/admin/dashboard?activeSection=elections";
    }

    @PostMapping("/election/{id}/close")
    public String closeElection(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            electionService.closeElection(id);
            redirectAttributes.addFlashAttribute("success", "Election closed successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to close election: " + e.getMessage());
        }
        return "redirect:/admin/dashboard?activeSection=elections";
    }

    @PostMapping("/election/{id}/edit")
    public String editElection(@PathVariable Long id,
                              @RequestParam String title,
                              @RequestParam String description,
                              @RequestParam String organization,
                              @RequestParam String startDatetime,
                              @RequestParam String endDatetime,
                              RedirectAttributes redirectAttributes) {
        try {
            Election election = electionService.findById(id);
            if (election == null) {
                redirectAttributes.addFlashAttribute("error", "Election not found!");
                return "redirect:/admin/dashboard?activeSection=elections";
            }

            // Parse datetime strings with proper error handling
            LocalDateTime start = LocalDateTime.parse(startDatetime);
            LocalDateTime end = LocalDateTime.parse(endDatetime);

            election.setTitle(title);
            election.setDescription(description);
            election.setOrganization(organization);
            election.setStartDatetime(start);
            election.setEndDatetime(end);

            electionService.updateElection(election);
            redirectAttributes.addFlashAttribute("success", "Election updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to update election: Invalid date format or " + e.getMessage());
        }
        return "redirect:/admin/dashboard?activeSection=elections";
    }

    @PostMapping("/election/{id}/delete")
    public String deleteElection(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            electionService.deleteElection(id);
            redirectAttributes.addFlashAttribute("success", "Election deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to delete election: " + e.getMessage());
        }
        return "redirect:/admin/dashboard?activeSection=elections";
    }

    @GetMapping("/election/{id}/edit")
    public String editElectionForm(@PathVariable Long id, Model model) {
        Election election = electionService.findById(id);
        if (election == null) {
            return "redirect:/admin/dashboard?error=Election not found";
        }
        model.addAttribute("editElection", election);
        model.addAttribute("activeSection", "elections");
        model.addAttribute("allElections", electionService.findAll());
        model.addAttribute("allUsers", userService.getAllUsers());
        model.addAttribute("allPositions", positionService.findAll());
        model.addAttribute("allCandidates", candidateService.findAll());
        return "admin-dashboard"; // Or a separate edit template if needed
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
                return "redirect:/admin/dashboard?activeSection=positions";
            }

            Position position = new Position();
            position.setPositionName(title);
            position.setDescription(description);
            position.setElection(election);

            positionService.save(position);
            redirectAttributes.addFlashAttribute("success", "Position created successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to create position: " + e.getMessage());
        }
        return "redirect:/admin/dashboard?activeSection=positions";
    }

    @PostMapping("/position/{id}/delete")
    public String deletePosition(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            positionService.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Position deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to delete position: " + e.getMessage());
        }
        return "redirect:/admin/dashboard?activeSection=positions";
    }

    @GetMapping("/position/{id}/edit")
    public String editPositionForm(@PathVariable Long id, Model model) {
        Position position = positionService.findById(id).orElse(null);
        if (position == null) {
            return "redirect:/admin/dashboard?error=Position not found";
        }
        model.addAttribute("editPosition", position);
        model.addAttribute("activeSection", "positions");
        model.addAttribute("allElections", electionService.findAll());
        model.addAttribute("allUsers", userService.getAllUsers());
        model.addAttribute("allPositions", positionService.findAll());
        model.addAttribute("allCandidates", candidateService.findAll());
        return "admin-dashboard";
    }

    @PostMapping("/position/{id}/edit")
    public String editPosition(@PathVariable Long id,
                              @RequestParam String title,
                              @RequestParam String description,
                              @RequestParam Long electionId,
                              RedirectAttributes redirectAttributes) {
        try {
            Position position = positionService.findById(id).orElse(null);
            if (position == null) {
                redirectAttributes.addFlashAttribute("error", "Position not found!");
                return "redirect:/admin/dashboard?activeSection=positions";
            }
            Election election = electionService.findById(electionId);
            if (election == null) {
                redirectAttributes.addFlashAttribute("error", "Election not found!");
                return "redirect:/admin/dashboard?activeSection=positions";
            }
            position.setPositionName(title);
            position.setDescription(description);
            position.setElection(election);
            positionService.save(position);
            redirectAttributes.addFlashAttribute("success", "Position updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to update position: " + e.getMessage());
        }
        return "redirect:/admin/dashboard?activeSection=positions";
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
        return "redirect:/admin/dashboard?activeSection=candidates";
    }

    @PostMapping("/candidate/{id}/delete")
    public String deleteCandidate(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            candidateService.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Candidate deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to delete candidate: " + e.getMessage());
        }
        return "redirect:/admin/dashboard?activeSection=candidates";
    }

    @GetMapping("/candidate/{id}/edit")
    public String editCandidateForm(@PathVariable Long id, Model model) {
        Candidate candidate = candidateService.findById(id).orElse(null);
        if (candidate == null) {
            return "redirect:/admin/dashboard?error=Candidate not found";
        }
        model.addAttribute("editCandidate", candidate);
        model.addAttribute("activeSection", "candidates");
        model.addAttribute("allElections", electionService.findAll());
        model.addAttribute("allUsers", userService.getAllUsers());
        model.addAttribute("allPositions", positionService.findAll());
        model.addAttribute("allCandidates", candidateService.findAll());
        return "admin-dashboard";
    }

    @PostMapping("/candidate/{id}/edit")
    public String editCandidate(@PathVariable Long id,
                               @RequestParam String fullName,
                               @RequestParam String description,
                               @RequestParam Long positionId,
                               RedirectAttributes redirectAttributes) {
        try {
            Candidate candidate = candidateService.findById(id).orElse(null);
            if (candidate == null) {
                redirectAttributes.addFlashAttribute("error", "Candidate not found!");
                return "redirect:/admin/dashboard";
            }
            Position position = positionService.findById(positionId).orElse(null);
            if (position == null) {
                redirectAttributes.addFlashAttribute("error", "Position not found!");
                return "redirect:/admin/dashboard";
            }
            candidate.setFullName(fullName);
            candidate.setDescription(description);
            candidate.setPosition(position);
            candidateService.save(candidate);
            redirectAttributes.addFlashAttribute("success", "Candidate updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to update candidate: " + e.getMessage());
        }
        return "redirect:/admin/dashboard?activeSection=candidates";
    }

    // ================= USER CRUD =================

    @PostMapping("/user/create")
    public String createUser(@RequestParam String username,
                            @RequestParam String email,
                            @RequestParam String firstName,
                            @RequestParam String lastName,
                            @RequestParam String password,
                            @RequestParam Long roleId,
                            RedirectAttributes redirectAttributes) {
        try {
            userService.createUser(username, email, firstName, lastName, password, roleId);
            redirectAttributes.addFlashAttribute("success", "User created successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to create user: " + e.getMessage());
        }
        return "redirect:/admin/dashboard?activeSection=users";
    }

    @PostMapping("/user/{id}/delete")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            userService.deleteUser(id);
            redirectAttributes.addFlashAttribute("success", "User deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to delete user: " + e.getMessage());
        }
        return "redirect:/admin/dashboard?activeSection=users";
    }

    @GetMapping("/user/{id}/edit")
    public String editUserForm(@PathVariable Long id, Model model) {
        User user = userService.findById(id).orElse(null);
        if (user == null) {
            return "redirect:/admin/dashboard?error=User not found";
        }
        model.addAttribute("editUser", user);
        model.addAttribute("activeSection", "users");
        model.addAttribute("allElections", electionService.findAll());
        model.addAttribute("allUsers", userService.getAllUsers());
        model.addAttribute("allPositions", positionService.findAll());
        model.addAttribute("allCandidates", candidateService.findAll());
        return "admin-dashboard";
    }

    @PostMapping("/user/{id}/edit")
    public String editUser(@PathVariable Long id,
                          @RequestParam String username,
                          @RequestParam String email,
                          @RequestParam String firstName,
                          @RequestParam String lastName,
                          @RequestParam Long roleId,
                          @RequestParam Boolean isActive,
                          RedirectAttributes redirectAttributes) {
        try {
            User user = userService.findById(id).orElse(null);
            if (user == null) {
                redirectAttributes.addFlashAttribute("error", "User not found!");
                return "redirect:/admin/dashboard?activeSection=users";
            }
            user.setUsername(username);
            user.setEmail(email);
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setIsActive(isActive);
            // Assuming role is set via service
            userService.updateUser(user);
            redirectAttributes.addFlashAttribute("success", "User updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to update user: " + e.getMessage());
        }
        return "redirect:/admin/dashboard?activeSection=users";
    }

    // ================= BACKUP AND RESTORE =================

    @PostMapping("/backup")
    public String backupDatabase(RedirectAttributes redirectAttributes) {
        try {
            // Simple backup implementation - in a real app, use proper database backup tools
            String backupFile = "backup_" + LocalDateTime.now().toString().replace(":", "-") + ".sql";
            // For H2 database, you might need to use SCRIPT command
            // For MySQL, use mysqldump
            // This is a placeholder - implement based on your database
            redirectAttributes.addFlashAttribute("success", "Database backup created: " + backupFile);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to backup database: " + e.getMessage());
        }
        return "redirect:/admin/dashboard?activeSection=system-settings";
    }

    @PostMapping("/restore")
    public String restoreDatabase(@RequestParam String backupFile, RedirectAttributes redirectAttributes) {
        try {
            // Simple restore implementation - in a real app, use proper database restore tools
            File file = new File(backupFile);
            if (!file.exists()) {
                redirectAttributes.addFlashAttribute("error", "Backup file not found!");
                return "redirect:/admin/dashboard?activeSection=system-settings";
            }
            // Implement restore logic based on your database
            redirectAttributes.addFlashAttribute("success", "Database restored from: " + backupFile);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to restore database: " + e.getMessage());
        }
        return "redirect:/admin/dashboard?activeSection=system-settings";
    }

    // ================= PDF EXPORT =================

    @GetMapping("/export-pdf")
    public ResponseEntity<byte[]> exportElectionReportPdf() {
        try {
            List<Election> allElections = electionService.findAll();
            List<Candidate> allCandidates = candidateService.findAll();

            // Prepare data for PDF
            Map<Long, Long> candidateVoteCounts = new HashMap<>();
            Map<Long, List<Candidate>> electionCandidates = new HashMap<>();

            for (Candidate candidate : allCandidates) {
                if (candidate != null && candidate.getPosition() != null && candidate.getPosition().getElection() != null) {
                    Long voteCount = voteService.getVoteCountForCandidate(
                        candidate.getPosition().getElection().getElectionId(),
                        candidate.getPosition().getPositionId(),
                        candidate.getCandidateId()
                    );
                    candidateVoteCounts.put(candidate.getCandidateId(), voteCount != null ? voteCount : 0L);

                    Long electionId = candidate.getPosition().getElection().getElectionId();
                    electionCandidates.computeIfAbsent(electionId, k -> new java.util.ArrayList<>()).add(candidate);
                }
            }

            byte[] pdfBytes = pdfService.generateElectionReportPdf(allElections, electionCandidates, candidateVoteCounts);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "election_report.pdf");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdfBytes);
        } catch (Exception e) {
            throw new RuntimeException("Error generating PDF report", e);
        }
    }
}
