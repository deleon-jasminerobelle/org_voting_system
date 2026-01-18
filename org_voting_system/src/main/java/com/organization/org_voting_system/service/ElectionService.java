package com.organization.org_voting_system.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.organization.org_voting_system.entity.Election;
import com.organization.org_voting_system.repository.ElectionRepository;

@Service
public class ElectionService {

    @Autowired
    private ElectionRepository electionRepository;

    public Election createElection(Election election) {
        return electionRepository.save(election);
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
}
