package com.thebrideside.crm.crm.entity;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "teams")
@Getter
@Setter
public class Teams {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "team_name", nullable = false, unique = true)
    private String teamName;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_manager", nullable = true, unique = true)
    private Employees teamManager;

    @OneToMany(mappedBy = "team")
    private List<Employees> employees;

    @Column(name = "total_members", nullable = false)
    private int total;

}
