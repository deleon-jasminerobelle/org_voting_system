package com.organization.org_voting_system.config;

import java.time.LocalDate;
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

        // ================= Create Roles =================
        Role adminRole = roleRepository.findByRoleName(Role.RoleName.ROLE_ADMIN)
                .orElseGet(() -> roleRepository.save(new Role(Role.RoleName.ROLE_ADMIN)));

        Role eoRole = roleRepository.findByRoleName(Role.RoleName.ROLE_ELECTION_OFFICER)
                .orElseGet(() -> roleRepository.save(new Role(Role.RoleName.ROLE_ELECTION_OFFICER)));

        Role voterRole = roleRepository.findByRoleName(Role.RoleName.ROLE_VOTER)
                .orElseGet(() -> roleRepository.save(new Role(Role.RoleName.ROLE_VOTER)));

        // ================= Create Sample Admin User (if not exists) =================
        if (!userRepository.existsByUsername("newadmin")) {
            User admin = new User();
            admin.setFirstName("NewAdmin");
            admin.setLastName("User");
            admin.setFullName("NewAdmin User");
            admin.setUsername("newadmin");
            admin.setEmail("newadmin@example.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole(adminRole);
            admin.setStudentNumber("ADMIN001");
            admin.setOrganization("Organization");
            admin.setSection("Admin");
            admin.setBirthdate(LocalDate.of(1990, 1, 1));
            admin.setCreatedAt(LocalDateTime.now());
            admin.setHasVoted(false);
            admin.setIsActive(true);
            userRepository.save(admin);
        }

        // ================= Create Sample Election Officer (if not exists) =================
        if (!userRepository.existsByUsername("neweo")) {
            User eo = new User();
            eo.setFirstName("NewElection");
            eo.setLastName("Officer");
            eo.setFullName("NewElection Officer");
            eo.setUsername("neweo");
            eo.setEmail("neweo@example.com");
            eo.setPassword(passwordEncoder.encode("eo123"));
            eo.setRole(eoRole);
            eo.setStudentNumber("EO001");
            eo.setOrganization("Organization");
            eo.setSection("EO");
            eo.setBirthdate(LocalDate.of(1990, 1, 1));
            eo.setCreatedAt(LocalDateTime.now());
            eo.setHasVoted(false);
            eo.setIsActive(true);
            userRepository.save(eo);
        }

        // ================= Create Sample Voter (if not exists) =================
        if (!userRepository.existsByUsername("newvoter")) {
            User voter = new User();
            voter.setFirstName("NewJohn");
            voter.setLastName("Doe");
            voter.setFullName("NewJohn Doe");
            voter.setUsername("newvoter");
            voter.setEmail("newvoter@example.com");
            voter.setPassword(passwordEncoder.encode("voter123"));
            voter.setRole(voterRole);
            voter.setStudentNumber("VOTER001");
            voter.setOrganization("Organization");
            voter.setSection("Voter");
            voter.setBirthdate(LocalDate.of(1995, 5, 15));
            voter.setCreatedAt(LocalDateTime.now());
            voter.setHasVoted(false);
            voter.setIsActive(true);
            userRepository.save(voter);
        }

        // ================= Create Sample Election =================
        if (electionRepository.count() == 0) {
            Election election = new Election();
            election.setTitle("Student Council Election 2024");
            election.setDescription("Election for student council positions");
            election.setOrganization("Organization");
            election.setStartDatetime(LocalDateTime.of(2024, 1, 15, 8, 0));
            election.setEndDatetime(LocalDateTime.of(2024, 1, 20, 17, 0));
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
            candidateRepository.save(new Candidate(president, "Juan Dela Cruz", "Experienced leader with vision for the future"));
            candidateRepository.save(new Candidate(president, "Maria Santos", "Dedicated to student welfare and academic excellence"));
            candidateRepository.save(new Candidate(vicePresident, "Pedro Reyes", "Focused on innovation and technology"));
            candidateRepository.save(new Candidate(vicePresident, "Ana Garcia", "Committed to environmental sustainability"));
            candidateRepository.save(new Candidate(secretary, "Carlos Lopez", "Organized and detail-oriented"));
            candidateRepository.save(new Candidate(secretary, "Sofia Martinez", "Excellent communication skills"));
        }
    }
}
