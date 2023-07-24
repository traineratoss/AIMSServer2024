package com.atoss.idea.management.system.service.implementation;

import com.atoss.idea.management.system.exception.IdeaNotFoundException;
import com.atoss.idea.management.system.exception.UserNotFoundException;
import com.atoss.idea.management.system.exception.FieldValidationException;
import com.atoss.idea.management.system.repository.CategoryRepository;
import com.atoss.idea.management.system.repository.IdeaRepository;
import com.atoss.idea.management.system.repository.UserRepository;
import com.atoss.idea.management.system.repository.dto.IdeaResponseDTO;
import com.atoss.idea.management.system.repository.dto.IdeaRequestDTO;
import com.atoss.idea.management.system.repository.dto.CategoryDTO;
import com.atoss.idea.management.system.repository.dto.IdeaUpdateDTO;
import com.atoss.idea.management.system.repository.entity.Category;
import com.atoss.idea.management.system.repository.entity.Idea;
import com.atoss.idea.management.system.repository.entity.Image;
import com.atoss.idea.management.system.repository.entity.User;
import com.atoss.idea.management.system.service.IdeaService;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.atoss.idea.management.system.repository.entity.Status;

import java.util.ArrayList;
import java.util.Date;
import java.util.stream.Collectors;

@Service
@Log4j2
public class IdeaServiceImpl implements IdeaService {

    private final IdeaRepository ideaRepository;

    private final UserRepository userRepository;

    private final CategoryRepository categoryRepository;

    private final ModelMapper modelMapper;

    public IdeaServiceImpl(IdeaRepository ideaRepository,
                           UserRepository userRepository,
                           CategoryRepository categoryRepository,
                           ModelMapper modelMapper) {
        this.ideaRepository = ideaRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public IdeaResponseDTO addIdea(IdeaRequestDTO idea, String username) {
        if (idea.getTitle() == null || idea.getTitle().isEmpty()) {
            throw new FieldValidationException("Please enter a valid title for the idea.");
        }
        if (idea.getStatus() == null) {
            throw new FieldValidationException("Please enter a valid status for the idea.");
        }
        if (idea.getCategoryList() == null || idea.getCategoryList().size() <= 0) {
            throw new FieldValidationException("Please enter a valid category for the idea.");
        }
        if (idea.getText() == null || idea.getText().isEmpty()) {
            throw new FieldValidationException("Please enter a valid text for the idea.");
        }
        Idea savedIdea = new Idea();
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException("No user found by this username."));
        savedIdea.setUser(user);
        if (idea.getImage() != null) {
            Image image = modelMapper.map(idea.getImage(), Image.class);
            savedIdea.setImage(image);
        }
        savedIdea.setStatus(idea.getStatus());
        savedIdea.setText(idea.getText());
        savedIdea.setTitle(idea.getTitle());
        savedIdea.setCategoryList(new ArrayList<>());
        for (CategoryDTO categoryDTO : idea.getCategoryList()) {
            Category category = categoryRepository.findByText(modelMapper.map(categoryDTO, Category.class).getText());
            if (category == null) {
                savedIdea.getCategoryList().add(modelMapper.map(categoryDTO, Category.class));
            } else {
                savedIdea.getCategoryList().add(category);
            }
        }
        savedIdea.setDate(new Date());
        user.getIdeas().add(savedIdea);
        return modelMapper.map(ideaRepository.save(savedIdea), IdeaResponseDTO.class);
    }

    @Override
    public IdeaResponseDTO getIdeaById(Long id) {
        if (ideaRepository.existsById(id)) {
            return modelMapper.map(ideaRepository.findIdeaById(id), IdeaResponseDTO.class);
        } else {
            throw new IdeaNotFoundException("Idea doesn't exist.");
        }
    }

    @Override
    public IdeaResponseDTO updateIdeaById(Long id, IdeaUpdateDTO ideaUpdateDTO) {
        if (ideaRepository.existsById(id)) {
            Idea idea = ideaRepository.findById(id).orElseThrow(
                    () -> new IdeaNotFoundException("Idea doesn't exist."));
            if (ideaUpdateDTO.getText() != null) {
                idea.setText(ideaUpdateDTO.getText());
            }
            if (ideaUpdateDTO.getStatus() != null) {
                idea.setStatus(ideaUpdateDTO.getStatus());
            }
            if (ideaUpdateDTO.getImage() != null) {
                idea.setImage(ideaUpdateDTO.getImage());
            }
            if (ideaUpdateDTO.getTitle() != null) {
                idea.setTitle(ideaUpdateDTO.getTitle());
            }
            if (ideaUpdateDTO.getCategoryList() != null) {
                idea.setCategoryList(new ArrayList<>());
                for (Category category:ideaUpdateDTO.getCategoryList()) {
                    idea.getCategoryList().add(category);
                }
            }
            return modelMapper.map(ideaRepository.save(idea), IdeaResponseDTO.class);
        } else if (ideaUpdateDTO == null) {
            throw new FieldValidationException("Please enter at least one field to update.");
        } else {
            throw new IdeaNotFoundException("Idea doesn't exist.");
        }
    }

    @Override
    public void deleteIdeaById(Long id) {
        if (ideaRepository.existsById(id)) {
            ideaRepository.deleteById(id);
        } else {
            throw new IdeaNotFoundException("Idea doesn't exist.");
        }
    }

    @Override
    public Page<IdeaResponseDTO> getAllIdeas(Pageable pageable) {
        if (ideaRepository.findAll().size() == 0) {
            throw new FieldValidationException("No ideas found.");
        }
        return new PageImpl<IdeaResponseDTO>(
                ideaRepository.findAll(pageable)
                        .stream()
                        .map(user -> modelMapper.map(user, IdeaResponseDTO.class))
                        .toList()
        );
    }

    @Override
    public Page<IdeaResponseDTO> getAllIdeasByUserId(Long id, Pageable pageable) {
        if (id < 0) {
            throw new FieldValidationException("Please enter a valid ID.");
        }
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException("User doesn't exist.");
        }
        if (userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User doesn't exist."))
                .getIdeas().isEmpty()) {
            throw new FieldValidationException("No ideas found.");
        }
        return new PageImpl<IdeaResponseDTO>(
                ideaRepository.findAllByUserId(id, pageable)
                        .stream()
                        .map(idea -> modelMapper.map(idea, IdeaResponseDTO.class))
                        .collect(Collectors.toList()));
    }

    @Override
    public Page<IdeaResponseDTO> filterIdeasByTitle(String title, Pageable pageable) {
        if (!title.isEmpty()) {
            return new PageImpl<IdeaResponseDTO>(
                    ideaRepository.findAllByTitle(title, pageable)
                            .stream()
                            .map(idea -> modelMapper.map(idea, IdeaResponseDTO.class))
                            .collect(Collectors.toList()));
        } else {
            throw new FieldValidationException("Please enter a valid title for the filter search.");
        }
    }

    @Override
    public Page<IdeaResponseDTO> filterIdeasByText(String text, Pageable pageable) {
        if (!text.isEmpty()) {
            return new PageImpl<IdeaResponseDTO>(
                    ideaRepository.findAllByText(text, pageable)
                            .stream()
                            .map(idea -> modelMapper.map(idea, IdeaResponseDTO.class))
                            .collect(Collectors.toList()));
        } else {
            throw new FieldValidationException("Please enter a valid text for the filter search.");
        }
    }

    @Override
    public Page<IdeaResponseDTO> filterIdeasByStatus(Status status, Pageable pageable) {
        if (status != null) {
            return new PageImpl<IdeaResponseDTO>(
                    ideaRepository.findAllByStatus(status, pageable)
                            .stream()
                            .map(idea -> modelMapper.map(idea, IdeaResponseDTO.class))
                            .collect(Collectors.toList()));
        } else {
            throw new FieldValidationException("Please enter a valid status for the filter search.");
        }
    }

    @Override
    public Page<IdeaResponseDTO> filterIdeasByCategory(String category, Pageable pageable) {
        if (!category.isEmpty()) {
            return new PageImpl<IdeaResponseDTO>(
                    ideaRepository.findAllByCategoryName(category, pageable)
                            .stream()
                            .map(idea -> modelMapper.map(idea, IdeaResponseDTO.class))
                            .collect(Collectors.toList()));
        } else {
            throw new FieldValidationException("Please enter a valid category for the filter search.");
        }
    }

    @Override
    public Page<IdeaResponseDTO> filterIdeasByAll(String title, String text, Status status, String category, Pageable pageable) {
        return new PageImpl<>(
                ideaRepository.findIdeasByParameters(title, text, status, category, pageable)
                        .stream()
                        .map(idea -> modelMapper.map(idea, IdeaResponseDTO.class))
                        .collect(Collectors.toList()));
    }
}