package com.thebrideside.crm.crm.service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thebrideside.crm.crm.dto.TeamDTO;
import com.thebrideside.crm.crm.entity.Employees;
import com.thebrideside.crm.crm.entity.Teams;
import com.thebrideside.crm.crm.repository.EmployeeRepository;
import com.thebrideside.crm.crm.repository.TeamRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class TeamService {

    private final TeamRepository teamRepository;
    private final EmployeeRepository employeeRepository;
    private final ModelMapper modelMapper;

    // ✅ Create new Team
    public TeamDTO createTeam(TeamDTO dto) {
        if (dto.getTeamName() == null || dto.getTeamName().isEmpty()) {
            throw new IllegalArgumentException("Team name is required");
        }

        Teams team = new Teams();
        team.setTeamName(dto.getTeamName());

        // ✅ Assign Manager
        if (dto.getTeamManagerId() != null && dto.getTeamManagerId() > 0) {
            Employees manager = employeeRepository.findById(dto.getTeamManagerId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Team manager not found with id: " + dto.getTeamManagerId()));
            team.setTeamManager(manager);
        }

        // ✅ Assign Employees
        if (dto.getEmployeeId() != null && !dto.getEmployeeId().isEmpty()) {
            Set<Employees> employees = dto.getEmployeeId().stream()
                    .filter(id -> id != null && id > 0)
                    .map(id -> employeeRepository.findById(id)
                            .orElseThrow(() -> new EntityNotFoundException("Employee not found with id: " + id)))
                    .collect(Collectors.toSet());
            team.setEmployees(employees.stream().collect(Collectors.toList()));
        }

        team.setTotal(team.getEmployees() != null ? team.getEmployees().size() : 0);

        Teams saved = teamRepository.save(team);

        TeamDTO response = new TeamDTO();
        response.setId(saved.getId());
        response.setTeamName(saved.getTeamName());
        response.setTotal(saved.getTotal());

        if (saved.getTeamManager() != null) {
            response.setTeamManagerId(saved.getTeamManager().getId());
            response.setTeamManagerName(saved.getTeamManager().getName());
        }

        if (saved.getEmployees() != null && !saved.getEmployees().isEmpty()) {
            response.setEmployeeId(saved.getEmployees().stream()
                    .map(Employees::getId)
                    .collect(Collectors.toSet()));
        }

        return response;
    }

    // ✅ Get all Teams
    @Transactional(readOnly = true)
    public List<TeamDTO> getAllTeams() {
        return teamRepository.findAll()
                .stream()
                .map(team -> {
                    TeamDTO dto = new TeamDTO();
                    dto.setId(team.getId());
                    dto.setTeamName(team.getTeamName());
                    dto.setTotal(team.getTotal());

                    if (team.getTeamManager() != null) {
                        dto.setTeamManagerId(team.getTeamManager().getId());
                        dto.setTeamManagerName(team.getTeamManager().getName());
                    }

                    if (team.getEmployees() != null && !team.getEmployees().isEmpty()) {
                        dto.setEmployeeId(team.getEmployees().stream()
                                .map(Employees::getId)
                                .collect(Collectors.toSet()));
                    }

                    return dto;
                })
                .collect(Collectors.toList());
    }
}
