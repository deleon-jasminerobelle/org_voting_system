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
import com.organization.org_voting_system.repository.VoteRepository;

@Service
public class VoteService {

    @Autowired
    private VoteRepository voteRepository;

    public Vote castVote(User user, Candidate candidate, Election election, Position position) throws Exception {
        // Null checks for type safety
        if (user == null || candidate == null || election == null || position == null) {
            throw new IllegalArgumentException("User, candidate, election, and position cannot be null");
        }

        // Check if user has already voted in this election for this position
        if (voteRepository.existsByUserAndElection(user, election)) {
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
        return voteRepository.existsByUserAndElection(user, election);
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
}
