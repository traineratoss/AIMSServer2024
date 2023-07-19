package com.atoss.idea.management.system.service.implementation;

import com.atoss.idea.management.system.exception.ValidationException;
import com.atoss.idea.management.system.repository.IdeaRepository;
import com.atoss.idea.management.system.repository.dto.IdeaDTO;
import com.atoss.idea.management.system.repository.dto.IdeaUpdateDTO;
import com.atoss.idea.management.system.repository.entity.Category;
import com.atoss.idea.management.system.repository.entity.Idea;
import com.atoss.idea.management.system.service.IdeaService;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@Log4j2
public class IdeaServiceImpl implements IdeaService {

    private final IdeaRepository ideaRepository;

    private final ModelMapper modelMapper;

    public IdeaServiceImpl(IdeaRepository ideaRepository, ModelMapper modelMapper) {
        this.ideaRepository = ideaRepository;
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
    public List<IdeaDTO> getAllIdeas() throws ValidationException {
        if (ideaRepository.findAll().size() > 0) {
            return Arrays.asList(modelMapper.map(ideaRepository.findAll(), IdeaDTO[].class));
        } else {
            throw new ValidationException("No ideas found.");
        }
    }
}
