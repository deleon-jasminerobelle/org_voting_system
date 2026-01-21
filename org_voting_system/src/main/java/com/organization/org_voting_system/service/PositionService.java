package com.organization.org_voting_system.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.organization.org_voting_system.entity.Election;
import com.organization.org_voting_system.entity.Position;
import com.organization.org_voting_system.repository.PositionRepository;

@Service
public class PositionService {

    @Autowired
    private PositionRepository positionRepository;

    // ================= CRUD =================

    public List<Position> findAll() {
        return positionRepository.findAll();
    }

    public Optional<Position> findById(Long id) {
        return positionRepository.findById(id);
    }

    public List<Position> findByElection(Election election) {
        return positionRepository.findByElection(election);
    }

    public Position save(Position position) {
        return positionRepository.save(position);
    }

    public void deleteById(Long id) {
        positionRepository.deleteById(id);
    }

    public boolean existsById(Long id) {
        return positionRepository.existsById(id);
    }
}
