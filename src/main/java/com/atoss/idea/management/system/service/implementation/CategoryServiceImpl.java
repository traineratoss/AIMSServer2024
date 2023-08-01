package com.atoss.idea.management.system.service.implementation;

import com.atoss.idea.management.system.exception.CategoryAlreadyExistsException;
import com.atoss.idea.management.system.exception.CategoryNotFoundException;
import com.atoss.idea.management.system.repository.CategoryRepository;
import com.atoss.idea.management.system.repository.dto.CategoryDTO;
import com.atoss.idea.management.system.repository.entity.Category;
import com.atoss.idea.management.system.service.CategoryService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    private final CategoryRepository categoryRepository;
    private final ModelMapper  modelMapper;

    public CategoryServiceImpl(CategoryRepository categoryRepository, ModelMapper modelMapper) {
        this.categoryRepository = categoryRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public CategoryDTO addCategory(CategoryDTO categoryDTO) {

        Category savedCategory = modelMapper.map(categoryDTO, Category.class);
        Optional<Category> existCategory = Optional.ofNullable(categoryRepository.findByText(savedCategory.getText()));

        if (existCategory.isPresent()) {
            throw new CategoryAlreadyExistsException("Category already exists");
        } else {
            savedCategory.setText(categoryDTO.getText());
            return modelMapper.map(categoryRepository.save(savedCategory), CategoryDTO.class);
        }
    }

    @Override
    public CategoryDTO getCategory(Long id) {

        if (categoryRepository.findById(id).isPresent()) {
            return modelMapper.map(categoryRepository.findById(id).get(), CategoryDTO.class);
        } else {
            throw new CategoryNotFoundException("No category found.");
        }
    }

    @Override
    public List<CategoryDTO> getAllCategory() {

        if (categoryRepository.findAll().size() > 0) {
            return Arrays.asList(modelMapper.map(categoryRepository.findAll(), CategoryDTO[].class));
        } else {
            throw new CategoryNotFoundException("No categories found.");
        }

    }
}
