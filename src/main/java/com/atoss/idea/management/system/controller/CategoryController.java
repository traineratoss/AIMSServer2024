package com.atoss.idea.management.system.controller;

import com.atoss.idea.management.system.repository.dto.CategoryDTO;
import com.atoss.idea.management.system.repository.entity.Category;
import com.atoss.idea.management.system.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/categories")
public class CategoryController {
    private final CategoryService categoryService;

    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping
    public Category addCategory(@RequestBody CategoryDTO category) throws Exception {
        return categoryService.addCategory(category);
    }

    @GetMapping("/{id}")
    public CategoryDTO getCategory(@PathVariable("id") Long id) throws RuntimeException {
        return categoryService.getCategory(id);
    }

    @GetMapping
    public List<CategoryDTO> getAllCategory() throws Exception {
        return categoryService.getAllCategory();
    }
}
