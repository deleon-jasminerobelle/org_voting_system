package com.organization.org_voting_system.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.organization.org_voting_system.entity.Candidate;
import com.organization.org_voting_system.entity.Election;
import com.organization.org_voting_system.entity.Position;

@Repository
public interface CandidateRepository extends JpaRepository<Candidate, Long> {

    List<Candidate> findByPosition(Position position);

    @Query("SELECT c FROM Candidate c JOIN FETCH c.position p JOIN FETCH p.election")
    List<Candidate> findAllWithPosition();
    
    @Query("SELECT c FROM Candidate c WHERE c.position.election = :election")
    List<Candidate> findByPositionElection(@Param("election") Election election);
}
