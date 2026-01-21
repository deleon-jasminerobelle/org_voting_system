package com.organization.org_voting_system.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.organization.org_voting_system.entity.Election;
import com.organization.org_voting_system.repository.ElectionRepository;

@Service
@Transactional
public class ElectionService {

    @Autowired
    private ElectionRepository electionRepository;

    public Election createElection(Election election) {
        return electionRepository.save(election);
    }

    public List<Election> findAll() {
        return electionRepository.findAll();
    }

    public List<Election> getAllElections() {
        return electionRepository.findAll();
    }

    public Optional<Election> getElectionById(Long id) {
        return electionRepository.findById(id);
    }

    public List<Election> getElectionsByStatus(Election.Status status) {
        return electionRepository.findByStatus(status);
    }

    public Election updateElection(Election election) {
        return electionRepository.save(election);
    }

    public void deleteElection(Long id) {
        electionRepository.deleteById(id);
    }

    public void updateElectionStatuses() {
        LocalDateTime now = LocalDateTime.now();
        List<Election> elections = electionRepository.findAll();

        for (Election election : elections) {
            // Skip updating if election is already manually closed
            if (election.getStatus() == Election.Status.CLOSED) {
                continue;
            }
            if (election.getStartDatetime() == null || election.getEndDatetime() == null) {
                continue;
            }
            if (now.isBefore(election.getStartDatetime())) {
                election.setStatus(Election.Status.UPCOMING);
            } else if (now.isAfter(election.getStartDatetime()) && now.isBefore(election.getEndDatetime())) {
                election.setStatus(Election.Status.ACTIVE);
            } else if (now.isAfter(election.getEndDatetime())) {
                election.setStatus(Election.Status.CLOSED);
            }
            electionRepository.save(election);
        }
    }
    
    // Additional methods for voter functionality
    public List<Election> getActiveElections() {
        updateElectionStatuses(); // Ensure statuses are current
        return electionRepository.findByStatusWithPositions(Election.Status.ACTIVE);
    }
    
    public List<Election> getUpcomingElections() {
        updateElectionStatuses(); // Ensure statuses are current
        return electionRepository.findByStatus(Election.Status.UPCOMING);
    }
    
    public Election findById(Long id) {
        return electionRepository.findById(id).orElse(null);
    }

    @Transactional
    public void closeElection(Long id) {
        Election election = electionRepository.findById(id).orElse(null);
        if (election != null) {
            election.setStatus(Election.Status.CLOSED);
            electionRepository.save(election);
        }
    }
}
