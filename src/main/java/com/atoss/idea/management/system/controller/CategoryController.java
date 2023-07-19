package com.atoss.idea.management.system.controller;

import com.atoss.idea.management.system.repository.dto.CategoryDTO;
import com.atoss.idea.management.system.repository.entity.Category;
import com.atoss.idea.management.system.service.implementation.CategoryServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.List;

@RestController
@RequestMapping("/categories")
public class CategoryController {
    private final CategoryServiceImpl categoryServiceImpl;

    @Autowired
    public CategoryController(CategoryServiceImpl categoryServiceImpl) {
        this.categoryServiceImpl = categoryServiceImpl;
    }

    @PostMapping
    public Category addCategory(@RequestBody CategoryDTO category) throws Exception {
        return categoryServiceImpl.addCategory(category);
    }

    @GetMapping("/{id}")
    public CategoryDTO getCategory(@PathVariable("id") long id) throws RuntimeException {
        return categoryServiceImpl.getCategory(id);
    }

    @GetMapping
    public List<CategoryDTO> getAllCategory() throws Exception {
        return categoryServiceImpl.getAllCategory();
    }
}
