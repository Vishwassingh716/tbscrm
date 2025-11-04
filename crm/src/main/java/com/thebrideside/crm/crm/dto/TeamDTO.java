package com.thebrideside.crm.crm.dto;

import java.util.List;
import java.util.Set;

import com.thebrideside.crm.crm.entity.Employees;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TeamDTO {

    private Long id;
    private String teamName;
    private Long teamManagerId;
    private String teamManagerName;
    private Set<Long> employeeId;
    private int total;

}
