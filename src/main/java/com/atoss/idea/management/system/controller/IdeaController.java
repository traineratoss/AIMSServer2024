package com.atoss.idea.management.system.controller;

import com.atoss.idea.management.system.exception.ValidationException;
import com.atoss.idea.management.system.repository.dto.IdeaRequestDTO;
import com.atoss.idea.management.system.repository.dto.IdeaUpdateDTO;
import com.atoss.idea.management.system.service.implementation.IdeaServiceImpl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.DeleteMapping;

@RestController
@RequestMapping("/ideas")
public class IdeaController {

    private final IdeaServiceImpl ideaService;

    public IdeaController(IdeaServiceImpl ideaService) {
        this.ideaService = ideaService;
    }


    @PostMapping("/createIdea")
    public IdeaRequestDTO addIdea(@RequestBody IdeaRequestDTO idea) throws ValidationException {
        return ideaService.addIdea(idea);
    }

    @GetMapping("getIdea/id")
    public IdeaRequestDTO getIdeaById(@RequestParam(required = true) Long id) throws ValidationException {
        return ideaService.getIdeaById(id);
    }

    @PatchMapping("updateIdea/id")
    public IdeaRequestDTO updateIdeaById(@RequestParam(required = true) Long id,
                                         @RequestBody IdeaUpdateDTO ideaUpdateDTO) throws ValidationException {
        return ideaService.updateIdeaById(id, ideaUpdateDTO);
    }

    @DeleteMapping("deleteIdea/id")
    public void deleteIdeaById(@RequestParam(required = true) Long id) throws ValidationException {
        ideaService.deleteIdeaById(id);
    }

    // We can sort the ideas based on a category we introduce manually, sorting a page based on it and a page size.
    @GetMapping("/getAllIdeas/page")
    public Page<IdeaRequestDTO> getAllIdeas(@RequestParam(required = true) int pageSize,
                                            @RequestParam(required = true) int pageNumber,
                                            @RequestParam(required = true) String sortCategory) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.ASC, sortCategory));
        return ideaService.getAllIdeas(pageable);
    }

    @GetMapping("/getAllIdeasByUserId/userId")
    public Page<IdeaRequestDTO> getAllIdeasByUserId(@RequestParam(required = true) Long id,
                                                    @RequestParam(required = true) int pageSize,
                                                    @RequestParam(required = true) int pageNumber,
                                                    @RequestParam(required = true) String sortCategory) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.ASC, sortCategory));
        return ideaService.getAllIdeasByUserId(id, pageable);
    }
}