package com.thebrideside.crm.crm.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.thebrideside.crm.crm.entity.Permissions;

public interface PermissionRepository extends JpaRepository<Permissions, Long> {

    Optional<Permissions> findById(Long id);

}
