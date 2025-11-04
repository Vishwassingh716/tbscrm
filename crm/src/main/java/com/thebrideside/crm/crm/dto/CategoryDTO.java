package com.thebrideside.crm.crm.dto;

import java.util.Set;

import com.thebrideside.crm.crm.entity.Employees;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryDTO {
    private Long id;
    private String categoryName;

    // Manager info (flattened)
    private Long managerId;
    private String managerName;
    private String managerEmail;

    // Summary of employees in this category
    private Set<Long> employeeIds;
}
