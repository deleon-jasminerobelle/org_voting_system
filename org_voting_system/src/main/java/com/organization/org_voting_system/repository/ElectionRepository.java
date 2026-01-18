package com.organization.org_voting_system.repository;

import com.organization.org_voting_system.entity.Election;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ElectionRepository extends JpaRepository<Election, Long> {

    List<Election> findByStatus(Election.Status status);
}
