package com.organization.org_voting_system.config;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.organization.org_voting_system.entity.Candidate;
import com.organization.org_voting_system.entity.Election;
import com.organization.org_voting_system.entity.Position;
import com.organization.org_voting_system.entity.Role;
import com.organization.org_voting_system.entity.User;
import com.organization.org_voting_system.repository.CandidateRepository;
import com.organization.org_voting_system.repository.ElectionRepository;
import com.organization.org_voting_system.repository.PositionRepository;
import com.organization.org_voting_system.repository.RoleRepository;
import com.organization.org_voting_system.repository.UserRepository;

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ElectionRepository electionRepository;

    @Autowired
    private PositionRepository positionRepository;

    @Autowired
    private CandidateRepository candidateRepository;

    @Override
    public void run(String... args) throws Exception {
        // Update role names in database directly
        roleRepository.updateRoleName("ADMIN", "ROLE_ADMIN");
        roleRepository.updateRoleName("ELECTION_OFFICER", "ROLE_ELECTION_OFFICER");
        roleRepository.updateRoleName("VOTER", "ROLE_VOTER");

        // Create or update roles
        Role adminRole = roleRepository.findByRoleName(Role.RoleName.ROLE_ADMIN).orElse(new Role(Role.RoleName.ROLE_ADMIN));
        roleRepository.save(adminRole);

        Role eoRole = roleRepository.findByRoleName(Role.RoleName.ROLE_ELECTION_OFFICER).orElse(new Role(Role.RoleName.ROLE_ELECTION_OFFICER));
        roleRepository.save(eoRole);

        Role voterRole = roleRepository.findByRoleName(Role.RoleName.ROLE_VOTER).orElse(new Role(Role.RoleName.ROLE_VOTER));
        roleRepository.save(voterRole);

        // Create or update admin user
        User admin = userRepository.findByEmail("admin@example.com").orElse(new User());
        boolean isNewAdmin = admin.getUserId() == null;
        admin.setFirstName("Admin");
        admin.setLastName("User");
        admin.setFullName("Admin User");
        admin.setUsername("admin");
        admin.setEmail("admin@example.com");
        // Always set password for admin
        admin.setPassword(passwordEncoder.encode("password"));
        admin.setRole(adminRole);
        admin.setStudentNumber("ADMIN001");
        admin.setOrganization("Organization");
        admin.setSection("Admin");
        admin.setBirthdate(java.time.LocalDate.of(1990, 1, 1));
        admin.setCreatedAt(java.time.LocalDateTime.now());
        admin.setHasVoted(false);
        admin.setIsActive(true);
        userRepository.save(admin);

        // Create election officer user if not exist
        if (!userRepository.existsByEmail("eo@example.com")) {
            User eo = new User();
            eo.setFirstName("Election");
            eo.setLastName("Officer");
            eo.setFullName("Election Officer");
            eo.setUsername("eo");
            eo.setEmail("eo@example.com");
            eo.setPassword(passwordEncoder.encode("password"));
            eo.setRole(eoRole);
            eo.setStudentNumber("EO001");
            eo.setOrganization("Organization");
            eo.setSection("EO");
            eo.setBirthdate(java.time.LocalDate.of(1990, 1, 1));
            eo.setCreatedAt(java.time.LocalDateTime.now());
            eo.setHasVoted(false);
            eo.setIsActive(true);
            userRepository.save(eo);
        }

        // Create or update voter user
        User voter = userRepository.findByEmail("voter@example.com").orElse(new User());
        boolean isNewVoter = voter.getUserId() == null;
        voter.setFirstName("John");
        voter.setLastName("Doe");
        voter.setFullName("John Doe");
        voter.setUsername("voter");
        voter.setEmail("voter@example.com");
        if (isNewVoter) {
            voter.setPassword(passwordEncoder.encode("password"));
        }
        voter.setRole(voterRole);
        voter.setStudentNumber("VOTER001");
        voter.setOrganization("Organization");
        voter.setSection("Voter");
        voter.setBirthdate(java.time.LocalDate.of(1995, 5, 15));
        voter.setCreatedAt(java.time.LocalDateTime.now());
        voter.setHasVoted(false);
        voter.setIsActive(true);
        userRepository.save(voter);

        // Create sample election if not exist
        if (electionRepository.count() == 0) {
            Election election = new Election();
            election.setTitle("Student Council Election 2024");
            election.setDescription("Election for student council positions");
            election.setStartDatetime(java.time.LocalDateTime.of(2024, 1, 15, 8, 0));
            election.setEndDatetime(java.time.LocalDateTime.of(2024, 1, 20, 17, 0));
            election.setStatus(Election.Status.ACTIVE);
            election.setCreatedAt(LocalDateTime.now());
            electionRepository.save(election);

            // Create positions
            Position president = new Position(election, "President", 1);
            positionRepository.save(president);

            Position vicePresident = new Position(election, "Vice President", 1);
            positionRepository.save(vicePresident);

            Position secretary = new Position(election, "Secretary", 1);
            positionRepository.save(secretary);

            // Create candidates
            Candidate candidate1 = new Candidate(president, "Juan Dela Cruz", "Experienced leader with vision for the future");
            candidateRepository.save(candidate1);

            Candidate candidate2 = new Candidate(president, "Maria Santos", "Dedicated to student welfare and academic excellence");
            candidateRepository.save(candidate2);

            Candidate candidate3 = new Candidate(vicePresident, "Pedro Reyes", "Focused on innovation and technology");
            candidateRepository.save(candidate3);

            Candidate candidate4 = new Candidate(vicePresident, "Ana Garcia", "Committed to environmental sustainability");
            candidateRepository.save(candidate4);

            Candidate candidate5 = new Candidate(secretary, "Carlos Lopez", "Organized and detail-oriented");
            candidateRepository.save(candidate5);

            Candidate candidate6 = new Candidate(secretary, "Sofia Martinez", "Excellent communication skills");
            candidateRepository.save(candidate6);
        }
    }
}
