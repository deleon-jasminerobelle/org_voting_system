package com.organization.org_voting_system.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.sql.Connection;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.organization.org_voting_system.entity.Candidate;
import com.organization.org_voting_system.entity.Election;
import com.organization.org_voting_system.entity.User;
import com.organization.org_voting_system.service.CandidateService;
import com.organization.org_voting_system.service.ElectionService;
import com.organization.org_voting_system.service.PdfService;
import com.organization.org_voting_system.service.UserService;
import com.organization.org_voting_system.service.VoteService;

@Controller
@RequestMapping("/election-officer")
@PreAuthorize("hasRole('ELECTION_OFFICER')")
public class ElectionOfficerController {

    @Autowired
    private UserService userService;

    @Autowired
    private ElectionService electionService;

    @Autowired
    private VoteService voteService;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private PdfService pdfService;

    @Autowired
    private CandidateService candidateService;

    @Value("${spring.datasource.url}")
    private String datasourceUrl;

    @Value("${spring.datasource.username}")
    private String datasourceUsername;

    @Value("${spring.datasource.password}")
    private String datasourcePassword;

    @Value("${mysqldump.path:mysqldump}")
    private String mysqldumpPath;

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

    // ================= BACKUP AND RESTORE =================

    @PostMapping("/backup")
    public String backupDatabase(RedirectAttributes redirectAttributes) {
        try {
            // Use absolute path to ensure backup directory is created in the correct location
            Path backupDir = Paths.get(System.getProperty("user.dir")).resolve("backups").toAbsolutePath();
            Files.createDirectories(backupDir);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
            String backupFileName = "backup_" + LocalDateTime.now().format(formatter) + ".sql";
            Path backupFilePath = backupDir.resolve(backupFileName);

            String dbName = extractDatabaseName(datasourceUrl);
            String host = extractHostFromUrl(datasourceUrl);
            String port = extractPortFromUrl(datasourceUrl);

            java.util.List<String> command = new java.util.ArrayList<>();
            command.add(mysqldumpPath);
            command.add("-h");
            command.add(host);
            command.add("-P");
            command.add(port);
            command.add("-u");
            command.add(datasourceUsername);
            
            if (datasourcePassword != null && !datasourcePassword.isEmpty()) {
                command.add("-p" + datasourcePassword);
            }
            
            command.add("--single-transaction");
            command.add("--routines");
            command.add("--triggers");
            command.add(dbName);

            ProcessBuilder processBuilder = new ProcessBuilder(command);
            processBuilder.directory(backupDir.toFile());
            processBuilder.redirectErrorStream(true);

            Process process = processBuilder.start();
            
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                 java.io.FileWriter writer = new java.io.FileWriter(backupFilePath.toFile())) {
                String line;
                while ((line = reader.readLine()) != null) {
                    writer.write(line);
                    writer.write("\n");
                }
            }

            int exitCode = process.waitFor();
            
            if (exitCode == 0) {
                long fileSize = Files.size(backupFilePath);
                redirectAttributes.addFlashAttribute("success", 
                    "Database backup created successfully: " + backupFileName + " (" + formatFileSize(fileSize) + ")");
            } else {
                String errorMsg = "Backup failed with exit code: " + exitCode;
                try (BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
                    String errorLine = errorReader.readLine();
                    if (errorLine != null) {
                        errorMsg += " - " + errorLine;
                    }
                }
                redirectAttributes.addFlashAttribute("error", errorMsg);
            }

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to backup database: " + e.getMessage());
        }
        return "redirect:/election-officer/backup-restore";
    }

    @PostMapping("/restore")
    public String restoreDatabase(@RequestParam("file") MultipartFile uploadedFile, 
                                 @RequestParam(value = "mergeMode", defaultValue = "merge") String mergeMode,
                                 RedirectAttributes redirectAttributes) {
        try {
            if (uploadedFile.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Please select a file to restore!");
                return "redirect:/election-officer/backup-restore";
            }

            String filename = uploadedFile.getOriginalFilename();
            if (filename == null || (!filename.endsWith(".sql") && !filename.endsWith(".zip"))) {
                redirectAttributes.addFlashAttribute("error", "Invalid file format! Please upload .sql or .zip file.");
                return "redirect:/election-officer/backup-restore";
            }

            if (filename.endsWith(".sql")) {
                String sqlContent = new String(uploadedFile.getBytes(), StandardCharsets.UTF_8);
                
                if ("overwrite".equals(mergeMode)) {
                    clearDatabase();
                    redirectAttributes.addFlashAttribute("info", "Cleared existing data. Restoring from backup...");
                }
                
                executeSqlStatements(sqlContent);
                redirectAttributes.addFlashAttribute("success", "Database restored successfully from: " + filename);
            } else {
                redirectAttributes.addFlashAttribute("error", "ZIP file restore not yet implemented. Please use .sql files.");
            }

        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("error", "Failed to read file: " + e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to restore database: " + e.getMessage());
        }
        return "redirect:/election-officer/backup-restore";
    }

    // ================= HELPER METHODS =================

    private String formatFileSize(long bytes) {
        if (bytes <= 0) return "0 B";
        final String[] units = new String[]{"B", "KB", "MB", "GB"};
        int digitGroups = (int) (Math.log10(bytes) / Math.log10(1024));
        return String.format("%.2f %s", bytes / Math.pow(1024, digitGroups), units[digitGroups]);
    }

    private String extractDatabaseName(String jdbcUrl) {
        int lastSlash = jdbcUrl.lastIndexOf("/");
        int questionMark = jdbcUrl.indexOf("?", lastSlash);
        if (questionMark == -1) {
            return jdbcUrl.substring(lastSlash + 1);
        }
        return jdbcUrl.substring(lastSlash + 1, questionMark);
    }

    private String extractHostFromUrl(String jdbcUrl) {
        int start = jdbcUrl.indexOf("://") + 3;
        int end = jdbcUrl.indexOf(":", start);
        return jdbcUrl.substring(start, end);
    }

    private String extractPortFromUrl(String jdbcUrl) {
        int start = jdbcUrl.indexOf(":", jdbcUrl.indexOf("://") + 3) + 1;
        int end = jdbcUrl.indexOf("/", start);
        return jdbcUrl.substring(start, end);
    }

    private void clearDatabase() throws Exception {
        try (Connection conn = dataSource.getConnection()) {
            try (Statement stmt = conn.createStatement()) {
                String[] clearCommands = {
                    "DELETE FROM votes",
                    "DELETE FROM password_reset_tokens",
                    "DELETE FROM candidates",
                    "DELETE FROM positions",
                    "DELETE FROM elections",
                    "DELETE FROM users",
                    "DELETE FROM roles"
                };
                
                for (String sql : clearCommands) {
                    try {
                        stmt.execute(sql);
                    } catch (Exception e) {
                        System.out.println("Warning while clearing: " + e.getMessage());
                    }
                }
            }
        }
    }

    private void executeSqlStatements(String sqlContent) throws Exception {
        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false);
            
            try (Statement stmt = conn.createStatement()) {
                String[] statements = sqlContent.split(";");
                
                int executedCount = 0;
                int skippedCount = 0;
                
                for (String sql : statements) {
                    sql = sql.trim();
                    
                    if (sql.isEmpty() || sql.startsWith("--") || sql.startsWith("/*") || 
                        sql.startsWith("/*!") || sql.startsWith("SET ") || 
                        sql.startsWith("LOCK TABLES") || sql.startsWith("UNLOCK TABLES")) {
                        skippedCount++;
                        continue;
                    }
                    
                    try {
                        stmt.execute(sql);
                        executedCount++;
                        System.out.println("Executed SQL: " + sql.substring(0, Math.min(80, sql.length())).replaceAll("\\s+", " "));
                    } catch (Exception e) {
                        String errorMsg = e.getMessage() != null ? e.getMessage().toLowerCase() : "";
                        
                        if (errorMsg.contains("already exists") || 
                            errorMsg.contains("duplicate entry") || 
                            errorMsg.contains("duplicate key") ||
                            errorMsg.contains("foreign key constraint") ||
                            errorMsg.contains("primary key")) {
                            System.out.println("Skipping duplicate/constraint error: " + e.getMessage());
                            skippedCount++;
                        } else {
                            System.err.println("ERROR: " + e.getMessage());
                            System.err.println("Failed SQL: " + sql);
                            throw e;
                        }
                    }
                }
                
                System.out.println("Restore summary - Executed: " + executedCount + ", Skipped: " + skippedCount);
            }
            
            conn.commit();
        }
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
        List<User> allVoters = userService.getAllUsers().stream()
            .filter(u -> u.getRole().getRoleName().name().equals("ROLE_VOTER"))
            .toList();
        
        // Count voters who have actually voted (by checking votes table)
        long votedCount = allVoters.stream()
            .filter(u -> voteService.getElectionsVotedByUser(u.getUserId()).size() > 0)
            .count();
        
        long notVotedCount = allVoters.size() - votedCount;
        
        return "<div class='voters-container'>" +
               "<h2>List of Registered Voters</h2>" +
               "<div class='stats-card'>" +
               "<div class='stat-item'>" +
               "<h3>" + allVoters.size() + "</h3>" +
               "<p>Total Voters</p>" +
               "</div>" +
               "<div class='stat-item'>" +
               "<h3>" + votedCount + "</h3>" +
               "<p>Have Voted</p>" +
               "</div>" +
               "<div class='stat-item'>" +
               "<h3>" + notVotedCount + "</h3>" +
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
               allVoters.stream().map(user -> {
                   boolean hasVoted = voteService.getElectionsVotedByUser(user.getUserId()).size() > 0;
                   return "<tr>" +
                       "<td>" + user.getStudentNumber() + "</td>" +
                       "<td>" + user.getFullName() + "</td>" +
                       "<td>" + user.getEmail() + "</td>" +
                       "<td><span class='status-badge " + (hasVoted ? "voted" : "not-voted") + "'>" + (hasVoted ? "Voted" : "Not Voted") + "</span></td>" +
                       "</tr>";
               }).reduce("", String::concat) +
               "</tbody>" +
               "</table>" +
               "</div>";
    }

    @GetMapping("/api/list-elected")
    @ResponseBody
    public String getListElectedContent(Principal principal) {
        List<Election> elections = electionService.findAll();
        StringBuilder html = new StringBuilder();
        
        html.append("<div class='elected-container'>");
        html.append("<h2>Election Results</h2>");
        html.append("<div class='results-summary'>");
        
        if (elections.isEmpty()) {
            html.append("<p>No elections found.</p>");
        } else {
            for (Election election : elections) {
                html.append("<div class='result-card'>");
                html.append("<h3>").append(election.getTitle()).append("</h3>");
                html.append("<p><strong>Status:</strong> ").append(election.getStatus()).append("</p>");
                
                // Get positions and candidates for this election
                List<Candidate> candidates = candidateService.findByElection(election);
                if (!candidates.isEmpty()) {
                    html.append("<table class='results-table' style='width:100%; border-collapse: collapse; margin-top: 10px;'>");
                    html.append("<tr style='background-color: #f5f5f5; border-bottom: 2px solid #ddd;'>");
                    html.append("<th style='padding: 10px; text-align: left; border: 1px solid #ddd;'>Candidate</th>");
                    html.append("<th style='padding: 10px; text-align: left; border: 1px solid #ddd;'>Position</th>");
                    html.append("<th style='padding: 10px; text-align: center; border: 1px solid #ddd;'>Votes</th>");
                    html.append("</tr>");
                    
                    for (Candidate candidate : candidates) {
                        long voteCount = voteService.getVoteCountForCandidate(candidate.getCandidateId());
                        html.append("<tr style='border-bottom: 1px solid #ddd;'>");
                        html.append("<td style='padding: 10px; border: 1px solid #ddd;'>").append(candidate.getFullName()).append("</td>");
                        html.append("<td style='padding: 10px; border: 1px solid #ddd;'>").append(candidate.getPosition().getPositionName()).append("</td>");
                        html.append("<td style='padding: 10px; text-align: center; border: 1px solid #ddd; font-weight: bold;'>").append(voteCount).append("</td>");
                        html.append("</tr>");
                    }
                    html.append("</table>");
                } else {
                    html.append("<p>No candidates found for this election.</p>");
                }
                html.append("</div>");
            }
        }
        
        html.append("</div>");
        html.append("<div class='actions' style='margin-top: 20px;'>");
        html.append("<button class='btn-primary' onclick='window.location.href=\"/election-officer/export-pdf\"' style='padding: 12px 24px; background-color: #8b0000; color: white; border: none; border-radius: 6px; font-weight: 600; cursor: pointer; font-size: 14px;'>Generate PDF Report</button>");
        html.append("</div>");
        html.append("</div>");
        
        return html.toString();
    }

    @GetMapping("/api/backup-restore")
    @ResponseBody
    public String getBackupRestoreContent(Principal principal) {
        return "<div class='backup-container'>" +
               "<div class='backup-section'>" +
               "<h3>Create System Backup</h3>" +
               "<p>Create a complete backup of all system data including elections, users, votes, and configurations.</p>" +
               "<form action='/election-officer/backup' method='POST' style='display: inline;'>" +
               "<button type='submit' class='btn-primary' style='padding: 10px 20px; background-color: #8b0000; color: white; border: none; border-radius: 5px; cursor: pointer; font-weight: bold;'>Create Backup</button>" +
               "</form>" +
               "<div class='backup-status' id='backup-status'>No recent backups</div>" +
               "</div>" +
               "<div class='restore-section'>" +
               "<h3>Restore from Backup</h3>" +
               "<p>Restore system data from a previously created backup file. This will overwrite current data.</p>" +
               "<form action='/election-officer/restore' method='POST' enctype='multipart/form-data'>" +
               "<div style='margin-bottom: 15px;'>" +
               "<input type='file' name='file' id='backup-file' accept='.sql' required style='padding: 8px; border: 1px solid #ddd; border-radius: 4px; width: 100%; box-sizing: border-box;'>" +
               "</div>" +
               "<div style='margin-bottom: 15px;'>" +
               "<label style='display: block; margin-bottom: 10px; font-weight: 500;'>Restore Mode:</label>" +
               "<label style='display: inline-block; margin-right: 20px;'><input type='radio' name='mergeMode' value='merge' checked> Merge (Keep existing data)</label>" +
               "<label style='display: inline-block;'><input type='radio' name='mergeMode' value='overwrite'> Overwrite (Clear all data first)</label>" +
               "</div>" +
               "<button type='submit' class='btn-secondary' style='padding: 10px 20px; background-color: #666; color: white; border: none; border-radius: 5px; cursor: pointer; font-weight: bold;'>Restore Backup</button>" +
               "</form>" +
               "<div class='restore-status' id='restore-status'>No restore operations</div>" +
               "</div>" +
               "<div class='warning'>" +
               "<p><strong>⚠️ Warning:</strong> Restoring from backup will overwrite current system data. Overwrite mode will clear all existing data. Make sure to create a backup before restoring.</p>" +
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
            headers.setContentDispositionFormData("attachment", "election_results.pdf");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdfBytes);
        } catch (Exception e) {
            throw new RuntimeException("Error generating PDF report", e);
        }
    }
}
