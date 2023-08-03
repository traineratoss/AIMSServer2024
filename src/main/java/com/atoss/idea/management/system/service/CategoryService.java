package com.atoss.idea.management.system.service;

import com.atoss.idea.management.system.repository.dto.CategoryDTO;
import java.util.List;

public interface CategoryService {

    /**
     * Creates a category
     *
     * @param category it is for the category we receive from the client
     * @return it returns an CategoryDTO that represents an added category
     */
    CategoryDTO addCategory(CategoryDTO category);

    /**
     * Gets the category that is requested by the id of the category
     *
     * @param id it is for getting the category by id
     * @return it returns an CategoryDTO
     */
    CategoryDTO getCategory(Long id);

    /**
     * Gets all the categories from the database
     *
     * @return it returns a list of categories
     */
    List<CategoryDTO> getAllCategory();
}
