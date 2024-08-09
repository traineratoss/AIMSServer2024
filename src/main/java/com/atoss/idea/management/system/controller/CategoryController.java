package com.atoss.idea.management.system.controller;


import com.atoss.idea.management.system.repository.dto.CategoryDTO;
import com.atoss.idea.management.system.service.CategoryService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Log4j2
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
        if (log.isInfoEnabled()) {
            log.info("Received request to add a new category");
        }
        CategoryDTO categoryDTO = categoryService.addCategory(category);
        if (log.isInfoEnabled()) {
            log.info("Successfully added new category");
        }
        return new ResponseEntity<>(categoryDTO, HttpStatus.OK);
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
        if (log.isInfoEnabled()) {
            log.info("Received request to retrieve the category");
        }
        CategoryDTO categoryDTO = categoryService.getCategory(id);
        if (log.isInfoEnabled()) {
            log.info("Retrieved the category successfully");
        }
        return new ResponseEntity<>(categoryDTO, HttpStatus.OK);
    }

    /**
     * Gets all the categories from the database
     *
     * @return it returns all the categories and an HTTP status code
     *         of 200 (OK) if the operation is successful.
     */
    @GetMapping("/all")
    public ResponseEntity<List<CategoryDTO>> getAllCategory()  {
        if (log.isInfoEnabled()) {
            log.info("Received request to retrieve all categories");
        }
        List<CategoryDTO> categoryDTOList = categoryService.getAllCategory();
        if (log.isInfoEnabled()) {
            log.info("Retrieved all categories successfully");
        }

        return new ResponseEntity<>(categoryDTOList, HttpStatus.OK);
    }
}
