package com.atoss.idea.management.system.controller;


import com.atoss.idea.management.system.repository.dto.CategoryDTO;
import com.atoss.idea.management.system.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/aims/api/v1/ideas/categories")
public class CategoryController {
    private final CategoryService categoryService;

       
    /**
     * Constructor
     *
     * @param categoryService Dependency injection through constructor
     */
    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    /**
     * Creates a new category by processing the provided CategoryDTO object and adds it to the database
     *
     * @param category the CategoryDTO object representing the category to be created and added.
     * @return it returns the category that's been added and an HTTP status code
     *         of 200 (OK) if the operation is successful.
     */
    @PostMapping("/add")
    public ResponseEntity<CategoryDTO> addCategory(@RequestBody CategoryDTO category) {
        return new ResponseEntity<>(categoryService.addCategory(category), HttpStatus.OK);
    }

    /**
     * Gets a category from the database by the provided category id
     *
     * @param id it is for getting the category by id
     * @return it returns the selected category by the id of the category and an HTTP status code
     *         of 200 (OK) if the operation is successful.
     */
    @GetMapping("/get")
    public ResponseEntity<CategoryDTO> getCategory(@RequestParam Long id) {
        return new ResponseEntity<>(categoryService.getCategory(id), HttpStatus.OK);
    }

    /**
     * Gets all the categories from the database
     *
     * @return it returns all the categories and an HTTP status code
     *         of 200 (OK) if the operation is successful.
     */
    @GetMapping("/all")
    public ResponseEntity<List<CategoryDTO>> getAllCategory()  {
        return new ResponseEntity<>(categoryService.getAllCategory(), HttpStatus.OK);
    }
}
