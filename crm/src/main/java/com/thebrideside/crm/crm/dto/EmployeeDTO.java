package com.thebrideside.crm.crm.dto;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmployeeDTO {
    private long id;
    private String employeeId;
    private String name;
    private String email;
    private Long phoneNum;
    private LocalDateTime dateOfJoining;
    private Long roleId;
    private String roleName;
    private String companyKey;
    private Long userId; // Reference to crm_user
    private Long teamId;
    private String teamName; // optional for GET responses

    private Long categoryId;
    private String categoryName; // optional for GET responses

    private boolean isAdmin; // ✅ for GET response (auto-set; not manually required in POST)

}
