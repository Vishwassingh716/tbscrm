package com.thebrideside.crm.crm.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.thebrideside.crm.crm.entity.Employees;

@Repository
public interface EmployeeRepository extends JpaRepository<Employees, Long> {
    Optional<Employees> findById(Long id);

    Employees findByEmployeeId(String employeeId);

    List<Employees> findByTeamId(Long teamId);

    List<Employees> findByCategoryId(Long categoryId);

    List<Employees> findByRoleId(Long roleId);

    List<Employees> findByCategoryIdAndRoleId(Long categoryId, Long roleId);

    List<Employees> findByCategoryIdAndTeamIdAndRoleId(Long categoryId, Long teamId, Long roleId);

    List<Employees> findByCategoryIdAndTeamId(Long categoryId, Long teamId);
}