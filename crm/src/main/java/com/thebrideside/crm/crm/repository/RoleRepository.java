package com.thebrideside.crm.crm.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.thebrideside.crm.crm.entity.Roles;

public interface RoleRepository extends JpaRepository<Roles, Long> {
    Optional<Roles> findById(Long Id);
}
