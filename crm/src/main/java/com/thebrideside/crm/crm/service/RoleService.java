package com.thebrideside.crm.crm.service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.thebrideside.crm.crm.dto.RoleDTO;
import com.thebrideside.crm.crm.entity.Roles;
import com.thebrideside.crm.crm.repository.PermissionRepository;
import com.thebrideside.crm.crm.repository.RoleRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import com.thebrideside.crm.crm.entity.Permissions;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final ModelMapper modelMapper;

    public RoleDTO createRole(RoleDTO dto) {

        Roles role = new Roles();

        if (dto.getPermissions() != null && !dto.getPermissions().isEmpty()) {
            Set<Permissions> permissions = dto
                    .getPermissions().stream().map(p -> permissionRepository.findById(p.getId())
                            .orElseThrow(
                                    () -> new EntityNotFoundException("Permission not found with id: " + p.getId())))
                    .collect(Collectors.toSet());

            role.setPermissions(permissions);
        }
        role.setRoleName(dto.getRoleName());
        Roles saved = roleRepository.save(role);

        return modelMapper.map(saved, RoleDTO.class);
    }

    public List<RoleDTO> getAllRoles() {
        return roleRepository.findAll().stream()
                .map(role -> modelMapper.map(role, RoleDTO.class))
                .collect(Collectors.toList());
    }

}
