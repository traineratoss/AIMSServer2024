package com.atoss.idea.management.system.controller;

import com.atoss.idea.management.system.repository.dto.IdeaRequestDTO;
import com.atoss.idea.management.system.repository.dto.IdeaResponseDTO;
import com.atoss.idea.management.system.repository.dto.IdeaUpdateDTO;
import com.atoss.idea.management.system.service.implementation.IdeaServiceImpl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<IdeaResponseDTO> addIdea(@RequestBody IdeaRequestDTO idea,
                                                   @RequestParam String username) {
        return new ResponseEntity<>(ideaService.addIdea(idea, username), HttpStatus.OK);
    }

    @GetMapping("getIdea/id")
    public ResponseEntity<IdeaResponseDTO> getIdeaById(@RequestParam(required = true) Long id) {
        return new ResponseEntity<>(ideaService.getIdeaById(id), HttpStatus.OK);
    }

    @PatchMapping("updateIdea/id")
    public ResponseEntity<IdeaResponseDTO> updateIdeaById(@RequestParam(required = true) Long id,
                                                          @RequestBody IdeaUpdateDTO ideaUpdateDTO) {
        return new ResponseEntity<>(ideaService.updateIdeaById(id, ideaUpdateDTO), HttpStatus.OK);
    }

    @DeleteMapping("deleteIdea/id")
    public ResponseEntity<String> deleteIdeaById(@RequestParam(required = true) Long id)  {
        ideaService.deleteIdeaById(id);
        return new ResponseEntity<>("Idea successfully deleted", HttpStatus.OK);
    }

    // We can sort the ideas based on a category we introduce manually, sorting a page based on it and a page size.
    @GetMapping("/getAllIdeas/page")
    public ResponseEntity<Page<IdeaResponseDTO>> getAllIdeas(@RequestParam(required = true) int pageSize,
                                                             @RequestParam(required = true) int pageNumber,
                                                             @RequestParam(required = true) String sortCategory) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.ASC, sortCategory));
        return new ResponseEntity<>(ideaService.getAllIdeas(pageable), HttpStatus.OK);
    }

    @GetMapping("/getAllIdeasByUserId/userId")
    public ResponseEntity<Page<IdeaResponseDTO>> getAllIdeasByUserId(@RequestParam(required = true) Long id,
                                                                     @RequestParam(required = true) int pageSize,
                                                                     @RequestParam(required = true) int pageNumber,
                                                                     @RequestParam(required = true) String sortCategory) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.ASC, sortCategory));
        return new ResponseEntity<>(ideaService.getAllIdeasByUserId(id, pageable), HttpStatus.OK);
    }
}