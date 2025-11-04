package com.thebrideside.crm.crm.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.thebrideside.crm.crm.dto.PermissionDTO;
import com.thebrideside.crm.crm.service.PermissionService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RestController
@RequestMapping("/api/permissions")
public class PermissionController {
    private final PermissionService permissionService;

    public PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @GetMapping
    public List<PermissionDTO> getAllPermission() {
        return permissionService.getAllPermission();
    }

    @PostMapping
    public PermissionDTO createPermission(@RequestBody PermissionDTO dto) {
        return permissionService.createPermission(dto);
    }

}
