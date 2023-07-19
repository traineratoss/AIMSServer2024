package com.atoss.idea.management.system.controller;

import com.atoss.idea.management.system.exception.ValidationException;
import com.atoss.idea.management.system.repository.dto.IdeaDTO;
import com.atoss.idea.management.system.service.IdeaService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;
import java.util.List;

@RestController
public class IdeaController {

    private final IdeaService ideaService;

    public IdeaController(IdeaService ideaService) {
        this.ideaService = ideaService;
    }

    @PostMapping("/idea/")
    public IdeaDTO addIdea(@RequestBody IdeaDTO idea) throws ValidationException {
        return ideaService.addIdea(idea);
    }

    @GetMapping("/idea/{id}")
    public IdeaDTO getIdeaById(@PathVariable("id") Long id) throws ValidationException {
        return ideaService.getIdeaById(id);
    }

    @PutMapping("/idea/")
    public IdeaDTO updateIdeaById(@RequestBody IdeaDTO ideaDTO) throws ValidationException {
        return ideaService.updateIdeaById(ideaDTO);
    }

    @DeleteMapping("/idea/{id}")
    public void deleteIdea(@PathVariable("id") Long id) throws ValidationException {
        ideaService.deleteIdeaById(id);
    }

    @GetMapping("/idea/all")
    public List<IdeaDTO> getAllIdeas() throws ValidationException {
        return ideaService.getAllIdeas();
    }
}