package com.atoss.idea.management.system.service;

import com.atoss.idea.management.system.repository.dto.CategoryDTO;
import java.util.List;

public interface CategoryService {

    CategoryDTO addCategory(CategoryDTO category);

    CategoryDTO getCategory(Long id);

    List<CategoryDTO> getAllCategory();
}
