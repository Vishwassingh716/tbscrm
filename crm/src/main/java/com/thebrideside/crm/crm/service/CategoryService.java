package com.thebrideside.crm.crm.service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thebrideside.crm.crm.dto.CategoryDTO;
import com.thebrideside.crm.crm.entity.Category;
import com.thebrideside.crm.crm.entity.Employees;
import com.thebrideside.crm.crm.repository.CategoryRepository;
import com.thebrideside.crm.crm.repository.EmployeeRepository;

import lombok.RequiredArgsConstructor;
import jakarta.persistence.EntityNotFoundException;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final EmployeeRepository employeeRepository;
    private final ModelMapper modelMapper;

    public CategoryDTO createCategory(CategoryDTO dto) {
        // 🔹 Prevent duplicate category names
        if (categoryRepository.findByCategoryNameIgnoreCase(dto.getCategoryName()).isPresent()) {
            throw new IllegalArgumentException("Category with this name already exists: " + dto.getCategoryName());
        }

        // 🔹 Create new entity
        Category category = new Category();
        category.setCategoryName(dto.getCategoryName());

        // 🔹 Set employees (if any)
        if (dto.getEmployeeIds() != null && !dto.getEmployeeIds().isEmpty()) {
            Set<Employees> employees = dto.getEmployeeIds().stream()
                    .map(id -> employeeRepository.findById(id)
                            .orElseThrow(() -> new EntityNotFoundException("Employee not found with id: " + id)))
                    .collect(Collectors.toSet());
            category.setEmployees(employees);
        }

        // 🔹 Set manager (optional)
        if (dto.getManagerId() != null) {
            Employees manager = employeeRepository.findById(dto.getManagerId())
                    .orElseThrow(
                            () -> new EntityNotFoundException("Employee not found with id: " + dto.getManagerId()));
            category.setCategoryManager(manager);
        }

        // 🔹 Save
        Category saved = categoryRepository.save(category);

        // 🔹 Build response DTO
        CategoryDTO response = new CategoryDTO();
        response.setId(saved.getId());
        response.setCategoryName(saved.getCategoryName());

        if (saved.getCategoryManager() != null) {
            response.setManagerId(saved.getCategoryManager().getId());
            response.setManagerName(saved.getCategoryManager().getName());
            response.setManagerEmail(saved.getCategoryManager().getEmail());
        }

        // Return employee IDs instead of entity list
        if (saved.getEmployees() != null && !saved.getEmployees().isEmpty()) {
            response.setEmployeeIds(saved.getEmployees().stream()
                    .map(Employees::getId)
                    .collect(Collectors.toSet()));
        }

        return response;
    }

    @Transactional(readOnly = true)
    public List<CategoryDTO> getAllCategories() {
        return categoryRepository.findAll()
                .stream()
                .map(category -> {
                    CategoryDTO dto = new CategoryDTO();
                    dto.setId(category.getId());
                    dto.setCategoryName(category.getCategoryName());

                    if (category.getCategoryManager() != null) {
                        dto.setManagerId(category.getCategoryManager().getId());
                        dto.setManagerName(category.getCategoryManager().getName());
                        dto.setManagerEmail(category.getCategoryManager().getEmail());
                    }

                    if (category.getEmployees() != null && !category.getEmployees().isEmpty()) {
                        dto.setEmployeeIds(category.getEmployees().stream()
                                .map(Employees::getId)
                                .collect(Collectors.toSet()));
                    }

                    return dto;
                })
                .collect(Collectors.toList());
    }
}
