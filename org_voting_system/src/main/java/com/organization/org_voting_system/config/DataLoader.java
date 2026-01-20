package com.organization.org_voting_system.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.organization.org_voting_system.entity.Role;
import com.organization.org_voting_system.entity.User;
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

    @Override
    public void run(String... args) throws Exception {
        // Create roles if not exist
        if (roleRepository.findByName("ADMIN") == null) {
            Role adminRole = new Role(Role.RoleName.ADMIN);
            roleRepository.save(adminRole);
        }
        if (roleRepository.findByName("ELECTION_OFFICER") == null) {
            Role eoRole = new Role(Role.RoleName.ELECTION_OFFICER);
            roleRepository.save(eoRole);
        }
        if (roleRepository.findByName("VOTER") == null) {
            Role voterRole = new Role(Role.RoleName.VOTER);
            roleRepository.save(voterRole);
        }

        // Create admin user if not exist
        if (!userRepository.existsByEmail("admin@example.com")) {
            User admin = new User();
            admin.setFirstName("Admin");
            admin.setLastName("User");
            admin.setFullName("Admin User");
            admin.setUsername("admin");
            admin.setEmail("admin@example.com");
            admin.setPassword(passwordEncoder.encode("adminpass"));
            admin.setRole(roleRepository.findByName("ADMIN"));
            admin.setStudentNumber("ADMIN001");
            admin.setOrganization("Organization");
            admin.setSection("Admin");
            admin.setBirthdate(java.time.LocalDate.of(1990, 1, 1));
            admin.setCreatedAt(java.time.LocalDateTime.now());
            admin.setHasVoted(false);
            admin.setIsActive(true);
            userRepository.save(admin);
        }

        // Create election officer user if not exist
        if (!userRepository.existsByEmail("eo@example.com")) {
            User eo = new User();
            eo.setFirstName("Election");
            eo.setLastName("Officer");
            eo.setFullName("Election Officer");
            eo.setUsername("eo");
            eo.setEmail("eo@example.com");
            eo.setPassword(passwordEncoder.encode("eopass"));
            eo.setRole(roleRepository.findByName("ELECTION_OFFICER"));
            eo.setStudentNumber("EO001");
            eo.setOrganization("Organization");
            eo.setSection("EO");
            eo.setBirthdate(java.time.LocalDate.of(1990, 1, 1));
            eo.setCreatedAt(java.time.LocalDateTime.now());
            eo.setHasVoted(false);
            eo.setIsActive(true);
            userRepository.save(eo);
        }

        // Create voter user if not exist
        if (!userRepository.existsByEmail("voter@example.com")) {
            User voter = new User();
            voter.setFirstName("John");
            voter.setLastName("Doe");
            voter.setFullName("John Doe");
            voter.setUsername("voter");
            voter.setEmail("voter@example.com");
            voter.setPassword(passwordEncoder.encode("voterpass"));
            voter.setRole(roleRepository.findByName("VOTER"));
            voter.setStudentNumber("VOTER001");
            voter.setOrganization("Organization");
            voter.setSection("Voter");
            voter.setBirthdate(java.time.LocalDate.of(1995, 5, 15));
            voter.setCreatedAt(java.time.LocalDateTime.now());
            voter.setHasVoted(false);
            voter.setIsActive(true);
            userRepository.save(voter);
        }
    }
}
