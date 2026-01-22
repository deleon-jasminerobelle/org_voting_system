package com.organization.org_voting_system.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.organization.org_voting_system.entity.Candidate;
import com.organization.org_voting_system.entity.Election;
import com.organization.org_voting_system.entity.Position;
import com.organization.org_voting_system.repository.CandidateRepository;

@Service
public class CandidateService {

    @Autowired
    private CandidateRepository candidateRepository;

    // ================= CRUD =================

    public List<Candidate> findAll() {
        return candidateRepository.findAllWithPosition();
    }

    public Optional<Candidate> findById(Long id) {
        return candidateRepository.findById(id);
    }

    public List<Candidate> findByPosition(Position position) {
        return candidateRepository.findByPosition(position);
    }

    public Candidate save(Candidate candidate) {
        return candidateRepository.save(candidate);
    }

    public void deleteById(Long id) {
        candidateRepository.deleteById(id);
    }

    public boolean existsById(Long id) {
        return candidateRepository.existsById(id);
    }
    
    public List<Candidate> findByElection(Election election) {
        return candidateRepository.findByPositionElection(election);
    }
}
