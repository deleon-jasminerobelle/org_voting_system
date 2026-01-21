package com.organization.org_voting_system.service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.organization.org_voting_system.entity.Candidate;
import com.organization.org_voting_system.entity.Election;
import com.organization.org_voting_system.entity.Position;
import com.organization.org_voting_system.entity.User;
import com.organization.org_voting_system.entity.Vote;
import com.organization.org_voting_system.repository.CandidateRepository;
import com.organization.org_voting_system.repository.ElectionRepository;
import com.organization.org_voting_system.repository.UserRepository;
import com.organization.org_voting_system.repository.VoteRepository;

@Service
public class VoteService {

    @Autowired
    private VoteRepository voteRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ElectionRepository electionRepository;
    
    @Autowired
    private CandidateRepository candidateRepository;

    public Vote castVote(User user, Candidate candidate, Election election, Position position) throws Exception {
        // Null checks for type safety
        if (user == null || candidate == null || election == null || position == null) {
            throw new IllegalArgumentException("User, candidate, election, and position cannot be null");
        }

        // Check if user has already voted in this election for this position
        if (voteRepository.existsByVoterAndElection(user, election)) {
            throw new Exception("User has already voted in this election");
        }

        // Create vote hash for confidentiality
        String voteData = user.getUserId() + ":" + candidate.getCandidateId() + ":" + election.getElectionId() + ":" + System.currentTimeMillis();
        String voteHash = generateHash(voteData);

        Vote vote = new Vote(election, position, candidate, user, voteHash);
        return voteRepository.save(vote);
    }

    public List<Vote> getVotesByElection(Election election) {
        return voteRepository.findByElection(election);
    }

    public boolean hasUserVoted(User user, Election election) {
        return voteRepository.existsByVoterAndElection(user, election);
    }

    private String generateHash(String data) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hash = md.digest(data.getBytes());
        StringBuilder sb = new StringBuilder();
        for (byte b : hash) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
    
    // Additional methods for voter functionality
    public boolean hasUserVotedInElection(Long userId, Long electionId) {
        User user = userRepository.findById(userId).orElse(null);
        Election election = electionRepository.findById(electionId).orElse(null);
        
        if (user == null || election == null) {
            return false;
        }
        
        return voteRepository.existsByVoterAndElection(user, election);
    }
    
    public boolean hasUserVotedForPosition(Long userId, Long electionId, Long positionId) {
        return voteRepository.existsByVoterUserIdAndElectionElectionIdAndPositionPositionId(userId, electionId, positionId);
    }
    
    public void submitVote(Long electionId, Long positionId, Long candidateId, Long userId) throws Exception {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new Exception("User not found"));
        Election election = electionRepository.findById(electionId)
            .orElseThrow(() -> new Exception("Election not found"));
        Candidate candidate = candidateRepository.findById(candidateId)
            .orElseThrow(() -> new Exception("Candidate not found"));
        
        // Validate election is active
        if (!election.getStatus().equals(Election.Status.ACTIVE)) {
            throw new Exception("Election is not active");
        }
        
        // Check if user has already voted for this position
        if (hasUserVotedForPosition(userId, electionId, positionId)) {
            throw new Exception("User has already voted for this position");
        }
        
        // Create vote hash for security
        String voteData = userId + ":" + candidateId + ":" + electionId + ":" + positionId + ":" + System.currentTimeMillis();
        String voteHash = generateHash(voteData);
        
        Vote vote = new Vote(election, candidate.getPosition(), candidate, user, voteHash);
        voteRepository.save(vote);
    }
    
    public List<Election> getElectionsVotedByUser(Long userId) {
        return voteRepository.findDistinctElectionsByVoterUserId(userId);
    }

    public Long getVoteCountForCandidate(Long electionId, Long positionId, Long candidateId) {
        return voteRepository.countVotesByElectionPositionAndCandidate(electionId, positionId, candidateId);
    }
}
