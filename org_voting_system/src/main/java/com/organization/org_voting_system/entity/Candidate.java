package com.organization.org_voting_system.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "candidates")
public class Candidate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "candidate_id")
    private Long candidateId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "position_id", nullable = false)
    private Position position;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(length = 1000)
    private String description;

    @Column(length = 500)
    private String platform;

    // Constructors
    public Candidate() {}

    public Candidate(Position position, String fullName, String description) {
        this.position = position;
        this.fullName = fullName;
        this.description = description;
    }

    public Candidate(Position position, String fullName, String description, String platform) {
        this.position = position;
        this.fullName = fullName;
        this.description = description;
        this.platform = platform;
    }

    // Getters and Setters
    public Long getCandidateId() { return candidateId; }
    public void setCandidateId(Long candidateId) { this.candidateId = candidateId; }

    public Position getPosition() { return position; }
    public void setPosition(Position position) { this.position = position; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getPlatform() { return platform; }
    public void setPlatform(String platform) { this.platform = platform; }
}
