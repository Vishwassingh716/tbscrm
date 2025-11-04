package com.thebrideside.crm.crm.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.thebrideside.crm.crm.dto.EmployeeDTO;
import com.thebrideside.crm.crm.service.EmployeeService;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping
    public List<EmployeeDTO> getAllEmployees() {
        return employeeService.getAllEmployees();
    }

    @PostMapping
    public EmployeeDTO createEmployee(@RequestBody EmployeeDTO dto) {
        return employeeService.createEmployee(dto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmployeeDTO> getEmployeeById(@PathVariable Long id) {
        return ResponseEntity.ok(employeeService.getEmployeeById(id));
    }

    @GetMapping("/team/{teamId}")
    public ResponseEntity<List<EmployeeDTO>> getEmployeesByTeamId(@PathVariable Long teamId) {
        return ResponseEntity.ok(employeeService.getEmployeesByTeamId(teamId));
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<EmployeeDTO>> getEmployeesByCategoryId(@PathVariable Long categoryId) {
        return ResponseEntity.ok(employeeService.getEmployeesByCategoryId(categoryId));
    }

    @GetMapping("/role/{roleId}")
    public ResponseEntity<List<EmployeeDTO>> getEmployeesByRoleId(@PathVariable Long roleId) {
        return ResponseEntity.ok(employeeService.getEmployeesByRoleId(roleId));
    }

    @GetMapping("category/{categoryId}/role/{roleId}")
    public ResponseEntity<List<EmployeeDTO>> getEmployeesByCategoryAndRole(
            @PathVariable Long categoryId,
            @PathVariable Long roleId) {
        return ResponseEntity.ok(employeeService.getEmployeesByCategoryAndRole(categoryId, roleId));
    }

    @GetMapping("/category/{categoryId}/team/{teamId}")
    public ResponseEntity<List<EmployeeDTO>> getEmployeesByCategoryAndTeam(
            @PathVariable Long categoryId,
            @PathVariable Long teamId) {
        return ResponseEntity.ok(employeeService.getEmployeesByCategoryAndRole(categoryId, teamId));
    }

    // ✅ Get by Category + Team + Role
    @GetMapping("category/{categoryId}/team/{teamId}/role/{roleId}")
    public ResponseEntity<List<EmployeeDTO>> getEmployeesByCategoryTeamAndRole(
            @PathVariable Long categoryId,
            @PathVariable Long teamId,
            @PathVariable Long roleId) {
        return ResponseEntity.ok(employeeService.getEmployeesByCategoryTeamAndRole(categoryId, teamId, roleId));
    }
}