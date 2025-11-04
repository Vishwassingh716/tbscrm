package com.thebrideside.crm.crm.dto;

import java.util.Set;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoleDTO {
    private Long id;
    private String roleName;
    // Each role can have multiple permissions
    private Set<PermissionDTO> permissions;
}
