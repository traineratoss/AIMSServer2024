package com.atoss.idea.management.system.service;

import com.atoss.idea.management.system.repository.dto.CategoryDTO;
import com.atoss.idea.management.system.repository.entity.Category;
import java.util.List;

public interface CategoryService {

    Category addCategory(CategoryDTO category) throws Exception;

    CategoryDTO getCategory(Long id) throws RuntimeException;

    List<CategoryDTO> getAllCategory() throws Exception;
}
