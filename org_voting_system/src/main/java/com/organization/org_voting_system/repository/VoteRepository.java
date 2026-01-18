package com.organization.org_voting_system.repository;

import com.organization.org_voting_system.entity.Vote;
import com.organization.org_voting_system.entity.User;
import com.organization.org_voting_system.entity.Election;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {

    Optional<Vote> findByUserAndElection(User user, Election election);

    List<Vote> findByElection(Election election);

    boolean existsByUserAndElection(User user, Election election);
    
    // Additional methods for voter functionality
    boolean existsByVoterUserIdAndElectionElectionIdAndPositionPositionId(Long userId, Long electionId, Long positionId);
    
    @Query("SELECT DISTINCT v.election FROM Vote v WHERE v.voter.userId = :userId")
    List<Election> findDistinctElectionsByVoterUserId(@Param("userId") Long userId);
    
    @Query("SELECT COUNT(v) FROM Vote v WHERE v.election.electionId = :electionId AND v.position.positionId = :positionId")
    Long countVotesByElectionAndPosition(@Param("electionId") Long electionId, @Param("positionId") Long positionId);
    
    @Query("SELECT COUNT(v) FROM Vote v WHERE v.election.electionId = :electionId AND v.position.positionId = :positionId AND v.candidate.candidateId = :candidateId")
    Long countVotesByElectionPositionAndCandidate(@Param("electionId") Long electionId, @Param("positionId") Long positionId, @Param("candidateId") Long candidateId);
}
