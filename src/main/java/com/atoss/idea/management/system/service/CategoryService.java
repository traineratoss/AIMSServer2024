package com.atoss.idea.management.system.service;

import com.atoss.idea.management.system.repository.dto.CategoryDTO;
import com.atoss.idea.management.system.repository.entity.Category;
import java.util.List;

public interface CategoryService {

    Category addCategory(CategoryDTO category) throws Exception;

    CategoryDTO getCategory(long id) throws Exception;

    List<CategoryDTO> getAllCategory() throws Exception;
}
