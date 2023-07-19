package com.atoss.idea.management.system.controller;

import com.atoss.idea.management.system.exception.ValidationException;
import com.atoss.idea.management.system.repository.dto.IdeaDTO;
import com.atoss.idea.management.system.repository.entity.Idea;
import com.atoss.idea.management.system.service.IdeaService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import java.util.List;

@RestController
@RequestMapping("/idea")
public class IdeaController {

    private final IdeaService ideaService;

    public IdeaController(IdeaService ideaService) {
        this.ideaService = ideaService;
    }

    @PostMapping
    public IdeaDTO addIdea(@RequestBody Idea idea) throws ValidationException {
        return ideaService.addIdea(idea);
    }

    @GetMapping("/{id}")
    public IdeaDTO getIdeaById(@PathVariable("id") Long id) throws ValidationException {
        return ideaService.getIdeaById(id);
    }

    @PutMapping
    public IdeaDTO updateIdeaById(@RequestBody IdeaDTO ideaDTO) throws ValidationException {
        return ideaService.updateIdeaById(ideaDTO);
    }

    @DeleteMapping("/{id}")
    public void deleteIdeaById(@PathVariable("id") Long id) throws ValidationException {
        ideaService.deleteIdeaById(id);
    }

    @GetMapping("/all")
    public List<IdeaDTO> getAllIdeas() throws ValidationException {
        return ideaService.getAllIdeas();
    }
}