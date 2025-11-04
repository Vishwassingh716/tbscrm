package com.thebrideside.crm.crm.controller;

import org.springframework.web.bind.annotation.RestController;

import com.thebrideside.crm.crm.entity.User;
import com.thebrideside.crm.crm.service.UserService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    // @GetMapping("/user")
    // public String getMethodName() {
    // return new String("helloo");
    // }

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // GET /api/users
    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

}
