package com.organization.org_voting_system.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.organization.org_voting_system.entity.Election;

@Repository
public interface ElectionRepository extends JpaRepository<Election, Long> {

    List<Election> findByStatus(Election.Status status);

    @Query("SELECT DISTINCT e FROM Election e LEFT JOIN FETCH e.positions WHERE e.status = :status")
    List<Election> findByStatusWithPositions(Election.Status status);
}
