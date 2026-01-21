package com.organization.org_voting_system.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "votes", uniqueConstraints = @UniqueConstraint(columnNames = {"election_id", "position_id", "voter_id"}))
public class Vote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vote_id")
    private Long voteId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "election_id", nullable = false)
    private Election election;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "position_id", nullable = false)
    private Position position;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "candidate_id", nullable = false)
    private Candidate candidate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "voter_id", nullable = false)
    private User voter;

    @Column(name = "vote_hash", nullable = false)
    private String voteHash;

    @Column(name = "voter_token", nullable = false)
    private String voterToken;

    @Column(name = "voted_at", nullable = false, updatable = false)
    private LocalDateTime votedAt = LocalDateTime.now();

    // Constructors
    public Vote() {}

    public Vote(Election election, Position position, Candidate candidate, User voter, String voteHash, String voterToken) {
        this.election = election;
        this.position = position;
        this.candidate = candidate;
        this.voter = voter;
        this.voteHash = voteHash;
        this.voterToken = voterToken;
    }

    // Getters and Setters
    public Long getVoteId() { return voteId; }
    public void setVoteId(Long voteId) { this.voteId = voteId; }

    public Election getElection() { return election; }
    public void setElection(Election election) { this.election = election; }

    public Position getPosition() { return position; }
    public void setPosition(Position position) { this.position = position; }

    public Candidate getCandidate() { return candidate; }
    public void setCandidate(Candidate candidate) { this.candidate = candidate; }

    public User getVoter() { return voter; }
    public void setVoter(User voter) { this.voter = voter; }

    public String getVoteHash() { return voteHash; }
    public void setVoteHash(String voteHash) { this.voteHash = voteHash; }

    public String getVoterToken() { return voterToken; }
    public void setVoterToken(String voterToken) { this.voterToken = voterToken; }

    public LocalDateTime getVotedAt() { return votedAt; }
    public void setVotedAt(LocalDateTime votedAt) { this.votedAt = votedAt; }
}
