package com.atoss.idea.management.system.service.implementation;

import com.atoss.idea.management.system.exception.CategoryAlreadyExistsException;
import com.atoss.idea.management.system.exception.CategoryNotFoundException;
import com.atoss.idea.management.system.repository.CategoryRepository;
import com.atoss.idea.management.system.repository.dto.CategoryDTO;
import com.atoss.idea.management.system.repository.entity.Category;
import com.atoss.idea.management.system.service.CategoryService;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@Log4j2
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    private final CategoryRepository categoryRepository;
    private final ModelMapper  modelMapper;

    /**
     * Constructor
     * @param categoryRepository accessing CRUD Repository for Category Entity
     * @param modelMapper mapping Entity-DTO relationship
     */
    public CategoryServiceImpl(CategoryRepository categoryRepository, ModelMapper modelMapper) {
        this.categoryRepository = categoryRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public CategoryDTO addCategory(CategoryDTO categoryDTO) {
        Category savedCategory = modelMapper.map(categoryDTO, Category.class);
        Optional<Category> existCategory = Optional.ofNullable(categoryRepository.findByText(savedCategory.getText()));

        if (existCategory.isPresent()) {
            if (log.isErrorEnabled()) {
                log.error("Attempted to add a category that already exists: {}", categoryDTO.getText());
            }
            throw new CategoryAlreadyExistsException("Category already exists");
        } else {
            Category category = categoryRepository.save(savedCategory);
            if (log.isInfoEnabled()) {
                log.info("Category successfully added: {}", categoryDTO.getText());
            }
            return modelMapper.map(category, CategoryDTO.class);
        }
    }

    @Override
    public CategoryDTO getCategory(Long id) {
        if (categoryRepository.findById(id).isPresent()) {
            Category category = categoryRepository.findById(id).get();

            if (log.isInfoEnabled()) {
                log.info("Category found with id: {}", id);
            }
            return modelMapper.map(category, CategoryDTO.class);
        } else {
            if (log.isErrorEnabled()) {
                log.error("No category found with id: {}", id);
            }
            throw new CategoryNotFoundException("No category found.");
        }
    }

    @Override
    public List<CategoryDTO> getAllCategory() {

        if (categoryRepository.findAll().size() > 0) {

            List<Category> categories = categoryRepository.findAll();

            if (log.isInfoEnabled()) {
                log.info("Successfully retrieved {} categories.", categories.size());
            }
            return Arrays.asList(modelMapper.map(categories, CategoryDTO[].class));
        } else {
            if (log.isErrorEnabled()) {
                log.error("No categories found.");
            }
            throw new CategoryNotFoundException("No categories found.");
        }
    }
}
