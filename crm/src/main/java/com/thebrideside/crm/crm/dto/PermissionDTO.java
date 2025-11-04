package com.thebrideside.crm.crm.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PermissionDTO {
    private Long id;
    private String permissionName;
    private String description;
}
