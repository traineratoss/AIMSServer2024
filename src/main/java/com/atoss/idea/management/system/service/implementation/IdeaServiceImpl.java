package com.atoss.idea.management.system.service.implementation;

import com.atoss.idea.management.system.exception.IdeaNotFoundException;
import com.atoss.idea.management.system.exception.UserNotFoundException;
import com.atoss.idea.management.system.exception.FieldValidationException;
import com.atoss.idea.management.system.repository.CategoryRepository;
import com.atoss.idea.management.system.repository.IdeaRepository;
import com.atoss.idea.management.system.repository.IdeaRepositoryCustom;
import com.atoss.idea.management.system.repository.UserRepository;
import com.atoss.idea.management.system.repository.dto.*;
import com.atoss.idea.management.system.repository.entity.*;
import com.atoss.idea.management.system.service.IdeaService;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Log4j2
public class IdeaServiceImpl implements IdeaService {

    private final IdeaRepository ideaRepository;

    private final IdeaRepositoryCustom ideaRepositoryCustom;

    private final UserRepository userRepository;

    private final CategoryRepository categoryRepository;

    private final ModelMapper modelMapper;

    public IdeaServiceImpl(IdeaRepository ideaRepository,
                           IdeaRepositoryCustom ideaRepositoryCustom,
                           UserRepository userRepository,
                           CategoryRepository categoryRepository,
                           ModelMapper modelMapper) {
        this.ideaRepository = ideaRepository;
        this.ideaRepositoryCustom = ideaRepositoryCustom;
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
        IdeaResponseDTO responseDTO = modelMapper.map(ideaRepository.save(savedIdea), IdeaResponseDTO.class);
        responseDTO.setUsername(username);
        return responseDTO;
    }

    @Override
    public IdeaResponseDTO getIdeaById(Long id) throws FieldValidationException {
        if (ideaRepository.existsById(id)) {
            IdeaResponseDTO responseDTO = modelMapper.map(ideaRepository.findIdeaById(id), IdeaResponseDTO.class);
            responseDTO.setUsername(ideaRepository.findIdeaById(id).getUser().getUsername());
            return responseDTO;
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
            IdeaResponseDTO responseDTO = modelMapper.map(ideaRepository.save(idea), IdeaResponseDTO.class);
            responseDTO.setUsername(ideaRepository.findIdeaById(id).getUser().getUsername());
            return responseDTO;
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
        Page<Idea> ideas = ideaRepository.findAll(pageable);

        if (ideas.isEmpty()) {
            throw new FieldValidationException("No ideas found.");
        }

        List<IdeaResponseDTO> ideaResponseDTOs = ideas.getContent()
                .stream()
                .map(idea -> {
                    IdeaResponseDTO responseDTO = modelMapper.map(idea, IdeaResponseDTO.class);
                    responseDTO.setUsername(idea.getUser().getUsername());
                    return responseDTO;
                })
                .toList();

        return new PageImpl<>(ideaResponseDTOs, pageable, ideas.getTotalElements());
    }

    @Override
    public Page<IdeaResponseDTO> getAllIdeasByUserId(Long id, Pageable pageable) {
        if (id < 0) {
            throw new FieldValidationException("Please enter a valid ID.");
        }

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User doesn't exist."));

        if (user.getIdeas().isEmpty()) {
            throw new FieldValidationException("No ideas found.");
        }

        List<IdeaResponseDTO> ideaResponseDTOs = ideaRepository.findAllByUserId(id, pageable)
                .stream()
                .map(idea -> {
                    IdeaResponseDTO responseDTO = modelMapper.map(idea, IdeaResponseDTO.class);
                    responseDTO.setUsername(user.getUsername());
                    return responseDTO;
                })
                .collect(Collectors.toList());

        return new PageImpl<>(ideaResponseDTOs, pageable, ideaResponseDTOs.size());
    }

    @Override
    public IdeaPageDTO filterIdeasByAll(String title,
                                        String text,
                                        List<Status> statuses,
                                        List<String> categories,
                                        List<String> users,
                                        Pageable pageable) {
        IdeaPageDTO ideaList = ideaRepositoryCustom.findIdeasByParameters(title, text, statuses, categories, users, pageable);
        List<IdeaResponseDTO> result = ideaList.getPagedIdeas().stream()
                .map(idea -> {
                    IdeaResponseDTO responseDTO = modelMapper.map(idea, IdeaResponseDTO.class);
                    responseDTO.setUsername(idea.getUser().getUsername());
                    return responseDTO;
                })
                .toList();
        return new IdeaPageDTO(ideaList.getTotal(), new PageImpl(result, pageable, result.size()));
    }
}

