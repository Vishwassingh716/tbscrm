package com.thebrideside.crm.crm.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.thebrideside.crm.crm.entity.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findById(Long id);

    Optional<Category> findByCategoryNameIgnoreCase(String categoryName);

    // 👇 Eagerly fetch employees and manager in one query
    @EntityGraph(attributePaths = { "employees", "categoryManager" })
    List<Category> findAll();
}
