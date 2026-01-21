package com.organization.org_voting_system.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.organization.org_voting_system.entity.Election;
import com.organization.org_voting_system.entity.Position;

@Repository
public interface PositionRepository extends JpaRepository<Position, Long> {
    List<Position> findByElection(Election election);
}
