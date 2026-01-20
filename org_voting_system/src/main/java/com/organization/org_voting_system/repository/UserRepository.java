package com.organization.org_voting_system.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.organization.org_voting_system.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByStudentNumber(String studentNumber);

    Optional<User> findByUsername(String username);

    @Query("SELECT u FROM User u JOIN FETCH u.role WHERE u.email = ?1")
    Optional<User> findByEmail(String email);

    boolean existsByStudentNumber(String studentNumber);

    boolean existsByEmail(String email);
}
