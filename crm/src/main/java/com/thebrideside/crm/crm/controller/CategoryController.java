package com.thebrideside.crm.crm.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.thebrideside.crm.crm.dto.CategoryDTO;
import com.thebrideside.crm.crm.service.CategoryService;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {
    private final CategoryService categoryservice;

    public CategoryController(CategoryService categoryservice) {
        this.categoryservice = categoryservice;
    }

    @GetMapping
    public List<CategoryDTO> getAllCategory() {
        return categoryservice.getAllCategories();
    }

    @PostMapping
    public CategoryDTO postMethodName(@RequestBody CategoryDTO dto) {
        // TODO: process POST request

        return categoryservice.createCategory(dto);
    }

}
