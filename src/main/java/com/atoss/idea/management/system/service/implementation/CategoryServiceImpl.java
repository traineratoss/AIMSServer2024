package com.atoss.idea.management.system.service.implementation;

import com.atoss.idea.management.system.exception.CategoryAlreadyExistsException;
import com.atoss.idea.management.system.exception.CategoryNotFoundException;
import com.atoss.idea.management.system.exception.ValidationException;
import com.atoss.idea.management.system.repository.CategoryRepository;
import com.atoss.idea.management.system.repository.dto.CategoryDTO;
import com.atoss.idea.management.system.repository.entity.Category;
import com.atoss.idea.management.system.service.CategoryService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import java.util.Arrays;
import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final ModelMapper  modelMapper;

    public CategoryServiceImpl(CategoryRepository categoryRepository, ModelMapper modelMapper) {
        this.categoryRepository = categoryRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public Category addCategory(CategoryDTO categoryDTO) throws Exception {
        Category savedCategory = modelMapper.map(categoryDTO, Category.class);
        Category existCategory = categoryRepository.findByText(savedCategory.getText());

        if (existCategory.getText().equals(categoryDTO.getText())) {

            throw new CategoryAlreadyExistsException();
        } else {
            savedCategory.setText(categoryDTO.getText());
            return modelMapper.map(categoryRepository.save(savedCategory), Category.class);
        }
    }

    @Override
    public CategoryDTO getCategory(long id) throws RuntimeException {
        if (id > 0) {
            return modelMapper.map(categoryRepository.findById(id), CategoryDTO.class);
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
