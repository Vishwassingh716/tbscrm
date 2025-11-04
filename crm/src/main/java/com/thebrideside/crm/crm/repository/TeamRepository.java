package com.thebrideside.crm.crm.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.thebrideside.crm.crm.entity.Teams;

public interface TeamRepository extends JpaRepository<Teams, Long> {
    Optional<Teams> findById(Long id);
}
