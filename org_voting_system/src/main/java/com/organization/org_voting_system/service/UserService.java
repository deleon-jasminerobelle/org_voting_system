package com.organization.org_voting_system.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.organization.org_voting_system.entity.Role;
import com.organization.org_voting_system.entity.User;
import com.organization.org_voting_system.repository.RoleRepository;
import com.organization.org_voting_system.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User registerUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        String firstName = user.getFirstName() != null ? user.getFirstName() : "";
        String middleName = user.getMiddleName();
        String lastName = user.getLastName() != null ? user.getLastName() : "";
        user.setFullName(firstName + (middleName != null && !middleName.trim().isEmpty() ? " " + middleName : "") + " " + lastName);
        Role voterRole = roleRepository.findByName("VOTER");
        if (voterRole == null) {
            voterRole = new Role(Role.RoleName.VOTER);
            roleRepository.save(voterRole);
        }
        user.setRole(voterRole);
        user.setCreatedAt(java.time.LocalDateTime.now());
        user.setHasVoted(false);
        user.setIsActive(true);
        return userRepository.save(user);
    }

    public Optional<User> findByStudentNumber(String studentNumber) {
        return userRepository.findByStudentNumber(studentNumber);
    }

    public Optional<User> findByUsernameOptional(String username) {
        return userRepository.findByUsername(username);
    }
    
    // Method for voter functionality
    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User updateUser(User user) {
        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public boolean existsByStudentNumber(String studentNumber) {
        return userRepository.existsByStudentNumber(studentNumber);
    }

    public boolean existsByUsername(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

    public boolean existsByEmail(String email) {
        return userRepository.findByEmail(email).isPresent();
    }
}
