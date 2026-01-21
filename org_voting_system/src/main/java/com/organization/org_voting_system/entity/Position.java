package com.organization.org_voting_system.entity;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "positions")
public class Position {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "position_id")
    private Long positionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "election_id", nullable = false)
    private Election election;

    @Column(name = "position_name", nullable = false)
    private String positionName;

    @Column(name = "description")
    private String description;

    @Column(name = "max_votes", nullable = false)
    private Integer maxVotes = 1;

    @OneToMany(mappedBy = "position", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Candidate> candidates;

    // Constructors
    public Position() {}

    public Position(Election election, String positionName, Integer maxVotes) {
        this.election = election;
        this.positionName = positionName;
        this.maxVotes = maxVotes;
    }

    // Getters and Setters
    public Long getPositionId() { return positionId; }
    public void setPositionId(Long positionId) { this.positionId = positionId; }

    public Election getElection() { return election; }
    public void setElection(Election election) { this.election = election; }

    public String getPositionName() { return positionName; }
    public void setPositionName(String positionName) { this.positionName = positionName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Integer getMaxVotes() { return maxVotes; }
    public void setMaxVotes(Integer maxVotes) { this.maxVotes = maxVotes; }

    public List<Candidate> getCandidates() { return candidates; }
    public void setCandidates(List<Candidate> candidates) { this.candidates = candidates; }
}
