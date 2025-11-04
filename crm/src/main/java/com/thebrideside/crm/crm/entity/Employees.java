package com.thebrideside.crm.crm.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "crm_employee")
@Getter
@Setter
public class Employees {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relationship with User
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = true)
    private Teams team;

    @Column(name = "employee_id", nullable = false, unique = true)
    private String employeeId;

    @Column(name = "employee_name", nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "phone_num", nullable = false)
    private Long phoneNum;

    @Column(name = "date_of_joining", nullable = false)
    private LocalDateTime dateOfJoining;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = true)
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = true)
    private Roles role;

    @Column(name = "company_key", nullable = true)
    private String companyKey;

    @Column(name = "is_admin", nullable = true)
    private boolean isAdmin = false;
}

// entity