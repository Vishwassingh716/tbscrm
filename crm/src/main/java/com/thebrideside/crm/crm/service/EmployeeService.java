package com.thebrideside.crm.crm.service;

import com.thebrideside.crm.crm.dto.EmployeeDTO;
import com.thebrideside.crm.crm.entity.Category;
import com.thebrideside.crm.crm.entity.Employees;
import com.thebrideside.crm.crm.entity.Roles;
import com.thebrideside.crm.crm.entity.Teams;
import com.thebrideside.crm.crm.entity.User;
import com.thebrideside.crm.crm.repository.CategoryRepository;
import com.thebrideside.crm.crm.repository.EmployeeRepository;
import com.thebrideside.crm.crm.repository.RoleRepository;
import com.thebrideside.crm.crm.repository.TeamRepository;
import com.thebrideside.crm.crm.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final TeamRepository teamRepository;
    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;

    // ✅ Example: replace with your real company key logic
    private static final String VALID_COMPANY_KEY = "TBS-SECRET-2025";

    public EmployeeDTO createEmployee(EmployeeDTO dto) {
        // Map base properties
        Employees emp = new Employees();
        emp.setEmployeeId(dto.getEmployeeId());
        emp.setName(dto.getName());
        emp.setEmail(dto.getEmail());
        emp.setPhoneNum(dto.getPhoneNum());
        emp.setDateOfJoining(dto.getDateOfJoining());
        emp.setCompanyKey(dto.getCompanyKey());

        // ✅ Auto-set isAdmin based on companyKey validation
        if (dto.getCompanyKey() != null && dto.getCompanyKey().equals(VALID_COMPANY_KEY)) {
            emp.setAdmin(true);
        } else {
            emp.setAdmin(false);
        }

        // ✅ Fetch and assign Role
        if (dto.getRoleId() != null) {
            Roles role = roleRepository.findById(dto.getRoleId())
                    .orElseThrow(() -> new EntityNotFoundException("Role not found with id: " + dto.getRoleId()));
            emp.setRole(role);
        }

        // ✅ Fetch and assign User
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + dto.getUserId()));
        emp.setUser(user);

        // ✅ Fetch and assign Team (optional)
        if (dto.getTeamId() != null) {
            Teams team = teamRepository.findById(dto.getTeamId())
                    .orElseThrow(() -> new EntityNotFoundException("Team not found with id: " + dto.getTeamId()));
            emp.setTeam(team);
        }

        // ✅ Fetch and assign Category (optional)
        if (dto.getCategoryId() != null) {
            Category category = categoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(
                            () -> new EntityNotFoundException("Category not found with id: " + dto.getCategoryId()));
            emp.setCategory(category);
        }

        // ✅ Save employee
        Employees saved = employeeRepository.save(emp);

        // ✅ Map back to DTO
        EmployeeDTO response = modelMapper.map(saved, EmployeeDTO.class);
        response.setRoleId(saved.getRole() != null ? saved.getRole().getId() : null);
        response.setRoleName(saved.getRole() != null ? saved.getRole().getRoleName() : null);
        response.setAdmin(saved.isAdmin());
        response.setCompanyKey(null); // optional security: hide key in response

        if (saved.getTeam() != null) {
            response.setTeamId(saved.getTeam().getId());
            response.setTeamName(saved.getTeam().getTeamName());
        }

        if (saved.getCategory() != null) {
            response.setCategoryId(saved.getCategory().getId());
            response.setCategoryName(saved.getCategory().getCategoryName());
        }

        return response;
    }

    // ✅ Get all employees
    public List<EmployeeDTO> getAllEmployees() {
        return employeeRepository.findAll()
                .stream()
                .map(emp -> {
                    EmployeeDTO dto = modelMapper.map(emp, EmployeeDTO.class);
                    dto.setRoleId(emp.getRole() != null ? emp.getRole().getId() : null);
                    dto.setRoleName(emp.getRole() != null ? emp.getRole().getRoleName() : null);
                    dto.setAdmin(emp.isAdmin());

                    if (emp.getTeam() != null) {
                        dto.setTeamId(emp.getTeam().getId());
                        dto.setTeamName(emp.getTeam().getTeamName());
                    }

                    if (emp.getCategory() != null) {
                        dto.setCategoryId(emp.getCategory().getId());
                        dto.setCategoryName(emp.getCategory().getCategoryName());
                    }

                    return dto;
                })
                .collect(Collectors.toList());
    }

    public EmployeeDTO getEmployeeById(Long id) {
        Employees emp = employeeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Employee not found with id: " + id));
        return mapToDTO(emp);
    }

    public List<EmployeeDTO> getEmployeesByCategoryId(Long categoryId) {
        return employeeRepository.findByCategoryId(categoryId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<EmployeeDTO> getEmployeesByTeamId(Long teamId) {
        return employeeRepository.findByTeamId(teamId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<EmployeeDTO> getEmployeesByRoleId(Long roleId) {
        return employeeRepository.findByRoleId(roleId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<EmployeeDTO> getEmployeesByCategoryAndRole(Long categoryId, Long roleId) {
        return employeeRepository.findByCategoryIdAndRoleId(categoryId, roleId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<EmployeeDTO> getEmployeesByCategoryTeamAndRole(Long categoryId, Long teamId, Long roleId) {
        return employeeRepository.findByCategoryIdAndTeamIdAndRoleId(categoryId, teamId, roleId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<EmployeeDTO> getEmployeesByCategoryTeam(Long categoryId, Long teamId) {
        return employeeRepository.findByCategoryIdAndTeamId(categoryId, teamId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private EmployeeDTO mapToDTO(Employees e) {
        EmployeeDTO dto = new EmployeeDTO();
        dto.setId(e.getId());
        dto.setEmployeeId(e.getEmployeeId());
        dto.setName(e.getName());
        dto.setEmail(e.getEmail());
        dto.setPhoneNum(e.getPhoneNum());
        dto.setDateOfJoining(e.getDateOfJoining());
        dto.setCompanyKey(null); // hide from response
        dto.setAdmin(e.isAdmin());

        if (e.getRole() != null) {
            dto.setRoleId(e.getRole().getId());
            dto.setRoleName(e.getRole().getRoleName());
        }

        if (e.getTeam() != null) {
            dto.setTeamId(e.getTeam().getId());
            dto.setTeamName(e.getTeam().getTeamName());
        }

        if (e.getCategory() != null) {
            dto.setCategoryId(e.getCategory().getId());
            dto.setCategoryName(e.getCategory().getCategoryName());
        }

        if (e.getUser() != null) {
            dto.setUserId(e.getUser().getId());
        }

        return dto;
    }
}
