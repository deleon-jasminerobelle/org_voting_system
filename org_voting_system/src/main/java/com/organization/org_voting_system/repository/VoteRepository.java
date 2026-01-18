package com.organization.org_voting_system.repository;

import com.organization.org_voting_system.entity.Vote;
import com.organization.org_voting_system.entity.User;
import com.organization.org_voting_system.entity.Election;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {

    Optional<Vote> findByUserAndElection(User user, Election election);

    List<Vote> findByElection(Election election);

    boolean existsByUserAndElection(User user, Election election);
}
