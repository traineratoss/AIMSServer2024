package com.atoss.idea.management.system.service.implementation;

import com.atoss.idea.management.system.exception.CategoryAlreadyExistsException;
import com.atoss.idea.management.system.exception.CategoryNotFoundException;
import com.atoss.idea.management.system.exception.ValidationException;
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
    public Category addCategory(CategoryDTO categoryDTO) throws Exception {
        Category savedCategory = modelMapper.map(categoryDTO, Category.class);
        Optional<Category> existCategory = Optional.ofNullable(categoryRepository.findByText(savedCategory.getText()));

        if (existCategory.isPresent()) {

            throw new CategoryAlreadyExistsException();
        } else {
            savedCategory.setText(categoryDTO.getText());
            return modelMapper.map(categoryRepository.save(savedCategory), Category.class);
        }
    }

    @Override
    public CategoryDTO getCategory(Long id) throws RuntimeException {
        if (id > 0) {
            return modelMapper.map(categoryRepository.findCategoryById(id), CategoryDTO.class);
        } else {
            throw new ValidationException();
        }
    }

    @Override
    public List<CategoryDTO> getAllCategory() throws Exception {
        if (categoryRepository.findAll().size() > 0) {
            return Arrays.asList(modelMapper.map(categoryRepository.findAll(), CategoryDTO[].class));
        } else {
            throw new CategoryNotFoundException();
        }

    }
}
