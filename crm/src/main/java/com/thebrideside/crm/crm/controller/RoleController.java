package com.thebrideside.crm.crm.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.thebrideside.crm.crm.dto.RoleDTO;
import com.thebrideside.crm.crm.service.RoleService;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/roles")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping
    public List<RoleDTO> getAllRoles() {
        return roleService.getAllRoles();
    }

    @PostMapping
    public RoleDTO postMethodName(@RequestBody RoleDTO dto) {
        // TODO: process POST request

        return roleService.createRole(dto);
    }

}
