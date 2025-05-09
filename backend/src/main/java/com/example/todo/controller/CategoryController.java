package com.example.todo.controller;

import com.example.todo.entity.Category;
import com.example.todo.entity.User;
import com.example.todo.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public ResponseEntity<List<Category>> getCategories(@AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(categoryService.getCategoriesByUserId(currentUser.getId()));
    }

    @PostMapping
    public ResponseEntity<Category> createCategory(@Valid @RequestBody Category category,
            @AuthenticationPrincipal User currentUser) {
        category.setUserId(currentUser.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.createCategory(category));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Category> getCategoryById(@PathVariable Long id, @AuthenticationPrincipal User currentUser) {
        Category category = categoryService.getById(id);
        if (category == null || !category.getUserId().equals(currentUser.getId())) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(category);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Category> updateCategory(@PathVariable Long id, @Valid @RequestBody Category categoryDetails,
            @AuthenticationPrincipal User currentUser) {
        Category category = categoryService.getById(id);
        if (category == null || !category.getUserId().equals(currentUser.getId())) {
            return ResponseEntity.notFound().build();
        }
        categoryDetails.setUserId(currentUser.getId()); // Ensure userId is not changed
        return ResponseEntity.ok(categoryService.updateCategory(id, categoryDetails));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id, @AuthenticationPrincipal User currentUser) {
        Category category = categoryService.getById(id);
        if (category == null || !category.getUserId().equals(currentUser.getId())) {
            return ResponseEntity.notFound().build();
        }
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}