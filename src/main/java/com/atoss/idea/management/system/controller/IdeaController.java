package com.atoss.idea.management.system.controller;

import com.atoss.idea.management.system.repository.dto.IdeaPageDTO;
import com.atoss.idea.management.system.repository.dto.IdeaRequestDTO;
import com.atoss.idea.management.system.repository.dto.IdeaResponseDTO;
import com.atoss.idea.management.system.repository.dto.IdeaUpdateDTO;
import com.atoss.idea.management.system.repository.entity.Status;
import com.atoss.idea.management.system.service.implementation.IdeaServiceImpl;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/ideas")
public class IdeaController {

    private final IdeaServiceImpl ideaService;

    public IdeaController(IdeaServiceImpl ideaService) {
        this.ideaService = ideaService;
    }


    @PostMapping("/createIdea")
    @Transactional
    public ResponseEntity<IdeaResponseDTO> addIdea(@RequestBody IdeaRequestDTO idea,
                                                   @RequestParam String username) {
        return new ResponseEntity<>(ideaService.addIdea(idea, username), HttpStatus.OK);
    }

    @GetMapping("/getIdea/id")
    @Transactional
    public ResponseEntity<IdeaResponseDTO> getIdeaById(@RequestParam(required = true) Long id) {
        return new ResponseEntity<>(ideaService.getIdeaById(id), HttpStatus.OK);
    }

    @PatchMapping("/updateIdea/id")
    @Transactional
    public ResponseEntity<IdeaResponseDTO> updateIdeaById(@RequestParam(required = true) Long id,
                                                          @RequestBody IdeaUpdateDTO ideaUpdateDTO) {
        return new ResponseEntity<>(ideaService.updateIdeaById(id, ideaUpdateDTO), HttpStatus.OK);
    }

    @DeleteMapping("/deleteIdea/id")
    @Transactional
    public ResponseEntity<String> deleteIdeaById(@RequestParam(required = true) Long id)  {
        ideaService.deleteIdeaById(id);
        return new ResponseEntity<>("Idea successfully deleted", HttpStatus.OK);
    }

    // We can sort the ideas based on a category we introduce manually, sorting a page based on it and a page size.
    @Transactional
    @GetMapping("/getAllIdeas/page")
    public ResponseEntity<Page<IdeaResponseDTO>> getAllIdeas(@RequestParam(required = true) int pageSize,
                                                             @RequestParam(required = true) int pageNumber,
                                                             @RequestParam(required = true) String sortCategory,
                                                             @RequestParam(required = true) Sort.Direction sortDirection) {
        switch (sortDirection) {
            case ASC -> {
                Pageable pageableAsc = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.ASC, sortCategory));
                return new ResponseEntity<>(ideaService.getAllIdeas(pageableAsc), HttpStatus.OK);
            }
            case DESC -> {
                Pageable pageableDesc = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, sortCategory));
                return new ResponseEntity<>(ideaService.getAllIdeas(pageableDesc), HttpStatus.OK);
            }
            default -> {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        }
    }

    @Transactional
    @GetMapping("/getAllIdeasByUserId/userId")
    public ResponseEntity<Page<IdeaResponseDTO>> getAllIdeasByUserId(@RequestParam(required = true) Long id,
                                                                     @RequestParam(required = true) int pageSize,
                                                                     @RequestParam(required = true) int pageNumber,
                                                                     @RequestParam(required = true) String sortCategory,
                                                                     @RequestParam(required = true) Sort.Direction sortDirection) {
        switch (sortDirection) {
            case ASC -> {
                Pageable pageableAsc = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.ASC, sortCategory));
                return new ResponseEntity<>(ideaService.getAllIdeasByUserId(id, pageableAsc), HttpStatus.OK);
            }
            case DESC -> {
                Pageable pageableDesc = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, sortCategory));
                return new ResponseEntity<>(ideaService.getAllIdeasByUserId(id, pageableDesc), HttpStatus.OK);
            }
            default -> {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        }
    }

    @Transactional
    @GetMapping("/filterIdeas")
    public ResponseEntity<IdeaPageDTO> filterAllIdeasByParameters(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String text,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String user,
            @RequestParam(required = false) String selectedDateFrom,
            @RequestParam(required = false) String selectedDateTo,
            @RequestParam(required = true) int pageNumber,
            @RequestParam(required = true) String sortDirection) {

        List<String> categories = new ArrayList<>();
        if (category != null && !category.isEmpty()) {
            categories = Arrays.asList(category.split(","));
        }

        List<String> users = new ArrayList<>();
        if (user != null && !user.isEmpty()) {
            users = Arrays.asList(user.split(","));
        }

        List<String> statusStrings = new ArrayList<>();
        if (status != null && !status.isEmpty()) {
            statusStrings = Arrays.asList(status.split(","));
        }
        List<Status> statusEnums = statusStrings.stream().map(s -> Status.valueOf(s)).toList();
        // pentru a converti corect, din String in Status nu stie in QueryCreator

        Pageable pageableAsc = PageRequest.of(pageNumber, 2);

        return new ResponseEntity<>(ideaService.filterIdeasByAll(title,
                text, statusEnums, categories, users, selectedDateFrom, selectedDateTo, sortDirection, pageableAsc), HttpStatus.OK);
    }
}