package com.thebrideside.crm.crm.service;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.thebrideside.crm.crm.dto.PermissionDTO;
import com.thebrideside.crm.crm.entity.Permissions;

import com.thebrideside.crm.crm.repository.PermissionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PermissionService {

    private final PermissionRepository permissionRepository;
    private final ModelMapper modelMapper;

    public PermissionDTO createPermission(PermissionDTO dto) {
        Permissions permission = modelMapper.map(dto, Permissions.class);
        Permissions saved = permissionRepository.save(permission);
        return modelMapper.map(saved, PermissionDTO.class);
    }

    public List<PermissionDTO> getAllPermission() {
        return permissionRepository.findAll()
                .stream()
                .map(permission -> modelMapper.map(permission, PermissionDTO.class))
                .collect(Collectors.toList());
    }

}
