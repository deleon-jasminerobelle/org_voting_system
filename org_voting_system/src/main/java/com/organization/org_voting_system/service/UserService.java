package com.organization.org_voting_system.service;

import java.time.LocalDateTime;
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

    /**
     * ✅ REGISTER USER
     * All newly registered users are AUTOMATICALLY VOTERS
     */
    public User registerUser(User user) {

        // Encode password
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Build full name
        String firstName = user.getFirstName() != null ? user.getFirstName() : "";
        String middleName = user.getMiddleName();
        String lastName = user.getLastName() != null ? user.getLastName() : "";

        user.setFullName(
            firstName +
            (middleName != null && !middleName.trim().isEmpty() ? " " + middleName : "") +
            " " + lastName
        );

        // ✅ ALWAYS ASSIGN ROLE_VOTER
        Role voterRole = roleRepository
                .findByRoleName(Role.RoleName.ROLE_VOTER)
                .orElseGet(() -> {
                    Role role = new Role(Role.RoleName.ROLE_VOTER);
                    return roleRepository.save(role);
                });

        user.setRole(voterRole);

        // Default values
        user.setIsActive(true);
        user.setHasVoted(false);
        user.setCreatedAt(LocalDateTime.now());

        return userRepository.save(user);
    }

    // ================= FINDERS =================

    public Optional<User> findByUsernameOptional(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> findByStudentNumber(String studentNumber) {
        return userRepository.findByStudentNumber(studentNumber);
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    // ================= CRUD =================

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User updateUser(User user) {
        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    // ================= VALIDATION =================

    public boolean existsByStudentNumber(String studentNumber) {
        return userRepository.existsByStudentNumber(studentNumber);
    }

    public boolean existsByUsername(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

    public boolean existsByEmail(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    public User createUser(String username, String email, String firstName, String lastName, String password, Long roleId) {
        if (existsByUsername(username)) {
            throw new RuntimeException("Username already exists");
        }
        if (existsByEmail(email)) {
            throw new RuntimeException("Email already exists");
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setPassword(password); // Will be encoded in registerUser

        // Build full name
        String middleName = ""; // Assuming no middle name for admin created users
        user.setFullName(firstName + " " + lastName);

        // Assign role based on roleId
        Role role = roleRepository.findById(roleId).orElseThrow(() -> new RuntimeException("Role not found"));
        user.setRole(role);

        // Default values
        user.setIsActive(true);
        user.setHasVoted(false);
        user.setCreatedAt(LocalDateTime.now());

        // Encode password
        user.setPassword(passwordEncoder.encode(password));

        return userRepository.save(user);
    }
}
