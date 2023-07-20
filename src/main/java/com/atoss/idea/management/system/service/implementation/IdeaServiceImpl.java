package com.atoss.idea.management.system.service.implementation;

import com.atoss.idea.management.system.exception.ValidationException;
import com.atoss.idea.management.system.repository.IdeaRepository;
import com.atoss.idea.management.system.repository.UserRepository;
import com.atoss.idea.management.system.repository.dto.IdeaDTO;
import com.atoss.idea.management.system.repository.dto.IdeaUpdateDTO;
import com.atoss.idea.management.system.repository.entity.Category;
import com.atoss.idea.management.system.repository.entity.Idea;
import com.atoss.idea.management.system.service.IdeaService;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Log4j2
public class IdeaServiceImpl implements IdeaService {

    private final IdeaRepository ideaRepository;

    private final UserRepository userRepository;

    private final ModelMapper modelMapper;

    public IdeaServiceImpl(IdeaRepository ideaRepository, UserRepository userRepository, ModelMapper modelMapper) {
        this.ideaRepository = ideaRepository;
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public IdeaDTO addIdea(IdeaDTO idea) throws ValidationException {
        if (idea.getTitle() == null || idea.getTitle().isEmpty()) {
            throw new ValidationException("Please enter a valid title for the idea.");
        }
        if (idea.getStatus() == null || idea.getStatus().isEmpty()) {
            throw new ValidationException("Please enter a valid status for the idea.");
        }
        if (idea.getCategoryList() == null || idea.getCategoryList().size() <= 0) {
            throw new ValidationException("Please enter a valid category for the idea.");
        }
        Idea savedIdea = new Idea();
        savedIdea.setUser(idea.getUser());
        savedIdea.setImage(idea.getImage());
        savedIdea.setStatus(idea.getStatus());
        savedIdea.setText(idea.getText());
        savedIdea.setTitle(idea.getTitle());
        savedIdea.setCategoryList(new ArrayList<>());
        for (Category category : idea.getCategoryList()) {
            savedIdea.getCategoryList().add(category);
        }
        return modelMapper.map(ideaRepository.save(savedIdea), IdeaDTO.class);
    }


    @Override
    public IdeaDTO getIdeaById(Long id) throws ValidationException {
        if (ideaRepository.existsById(id)) {
            Idea idea = ideaRepository.findIdeaById(id);
            return modelMapper.map(ideaRepository.findIdeaById(id), IdeaDTO.class);
        } else {
            throw new ValidationException("Idea doesn't exist.");
        }
    }

    @Override
    public IdeaDTO updateIdeaById(Long id, IdeaUpdateDTO ideaUpdateDTO) throws ValidationException {
        if (ideaRepository.existsById(id)) {
            Idea idea = ideaRepository.findById(id).orElseThrow(
                    () -> new ValidationException("Idea doesn't exist."));
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
            ideaRepository.save(idea);
        } else {
            throw new ValidationException("Idea doesn't exist.");
        }
        return null;
    }


    @Override
    public void deleteIdeaById(Long id) throws ValidationException {
        if (ideaRepository.existsById(id)) {
            ideaRepository.deleteById(id);
        } else {
            throw new ValidationException("Idea doesn't exist.");
        }
    }

    @Override
    public Page<IdeaDTO> getAllIdeas(Pageable pageable) {
        return new PageImpl<IdeaDTO>(
                ideaRepository.findAll(pageable)
                        .stream()
                        .map(user -> modelMapper.map(user, IdeaDTO.class))
                        .toList()
        );
    }

    @Override
    public List<IdeaDTO> getAllIdeasByUserId(Long id) throws ValidationException {
        if (id < 0) {
            throw new ValidationException("Please enter a valid ID.");
        }
        if (!userRepository.existsById(id)) {
            throw new ValidationException("User doesn't exist.");
        }
        if (userRepository.findById(id).isEmpty()) {
            throw new ValidationException("User doesn't have any ideas.");
        }
        return (ArrayList<IdeaDTO>) ideaRepository.findAllByUserId(id).stream()
                .map(idea -> modelMapper.map(idea, IdeaDTO.class))
                .collect(Collectors.toList());
    }
}
