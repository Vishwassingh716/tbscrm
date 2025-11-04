package com.thebrideside.crm.crm.entity;

import java.time.OffsetDateTime;

import org.hibernate.annotations.Immutable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;

@Entity
@Table(name = "crm_user")
@Getter
@Immutable
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 128, nullable = false)
    private String password;

    @Column(name = "last_login")
    private OffsetDateTime lastLogin;

    @Column(name = "is_superuser", nullable = false)
    private boolean isSuperuser;

    @Column(nullable = false, unique = true, length = 254)
    private String email;

    @Column(name = "first_name", length = 150)
    private String firstName;

    @Column(name = "last_name", length = 150)
    private String lastName;

    @Column(name = "is_active", nullable = false)
    private boolean isActive;

    @Column(name = "is_staff", nullable = false)
    private boolean isStaff;

    @Column(name = "date_joined", nullable = false)
    private OffsetDateTime dateJoined;
}
