package com.thebrideside.crm.crm.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.thebrideside.crm.crm.dto.CategoryDTO;
import com.thebrideside.crm.crm.dto.TeamDTO;
import com.thebrideside.crm.crm.service.TeamService;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("api/teams")
public class TeamController {

    private final TeamService teamservice;

    public TeamController(TeamService teamService) {
        this.teamservice = teamService;
    }

    @GetMapping
    public List<TeamDTO> getAllTeams() {
        return teamservice.getAllTeams();
    }

    @PostMapping
    public TeamDTO postMethodName(@RequestBody TeamDTO dto) {
        // TODO: process POST request

        return teamservice.createTeam(dto);
    }

}
