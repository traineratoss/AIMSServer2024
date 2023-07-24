package com.atoss.idea.management.system.controller;

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


@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/ideas")
public class IdeaController {

    private final IdeaServiceImpl ideaService;


    public IdeaController(IdeaServiceImpl ideaService) {
        this.ideaService = ideaService;
    }


    @Transactional
    @PostMapping("/createIdea")
    public ResponseEntity<IdeaResponseDTO> addIdea(@RequestBody IdeaRequestDTO idea,
                                                   @RequestParam String username) {
        return new ResponseEntity<>(ideaService.addIdea(idea, username), HttpStatus.OK);
    }

    @Transactional
    @GetMapping("/getIdea/id")
    public ResponseEntity<IdeaResponseDTO> getIdeaById(@RequestParam(required = true) Long id) {
        return new ResponseEntity<>(ideaService.getIdeaById(id), HttpStatus.OK);
    }

    @Transactional
    @PatchMapping("/updateIdea/id")
    public ResponseEntity<IdeaResponseDTO> updateIdeaById(@RequestParam(required = true) Long id,
                                                          @RequestBody IdeaUpdateDTO ideaUpdateDTO) {
        return new ResponseEntity<>(ideaService.updateIdeaById(id, ideaUpdateDTO), HttpStatus.OK);
    }

    @Transactional
    @DeleteMapping("/deleteIdea/id")
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
    @GetMapping("/filterIdeasByTitle")
    public ResponseEntity<Page<IdeaResponseDTO>> getIdeasByTitle(@RequestParam(required = true) String title,
                                                                 @RequestParam(required = true) int pageSize,
                                                                 @RequestParam(required = true) int pageNumber,
                                                                 @RequestParam(required = true) String sortCategory,
                                                                 @RequestParam(required = true) Sort.Direction sortDirection) {
        switch (sortDirection) {
            case ASC -> {
                Pageable pageableAsc = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.ASC, sortCategory));
                return new ResponseEntity<>(ideaService.filterIdeasByTitle(title, pageableAsc), HttpStatus.OK);
            }
            case DESC -> {
                Pageable pageableDesc = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, sortCategory));
                return new ResponseEntity<>(ideaService.filterIdeasByTitle(title, pageableDesc), HttpStatus.OK);
            }
            default -> {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        }
    }

    @Transactional
    @GetMapping("/filterIdeasByText")
    public ResponseEntity<Page<IdeaResponseDTO>> getIdeasByText(@RequestParam(required = true) String text,
                                                                @RequestParam(required = true) int pageSize,
                                                                @RequestParam(required = true) int pageNumber,
                                                                @RequestParam(required = true) String sortCategory,
                                                                @RequestParam(required = true) Sort.Direction sortDirection) {
        switch (sortDirection) {
            case ASC -> {
                Pageable pageableAsc = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.ASC, sortCategory));
                return new ResponseEntity<>(ideaService.filterIdeasByText(text, pageableAsc), HttpStatus.OK);
            }
            case DESC -> {
                Pageable pageableDesc = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, sortCategory));
                return new ResponseEntity<>(ideaService.filterIdeasByText(text, pageableDesc), HttpStatus.OK);
            }
            default -> {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        }
    }

    @Transactional
    @GetMapping("/filterIdeasByStatus")
    public ResponseEntity<Page<IdeaResponseDTO>> getIdeasByStatus(@RequestParam(required = true) String status,
                                                                  @RequestParam(required = true) int pageSize,
                                                                  @RequestParam(required = true) int pageNumber,
                                                                  @RequestParam(required = true) String sortCategory,
                                                                  @RequestParam(required = true) Sort.Direction sortDirection) {
        Status statusEnum;
        try {
            statusEnum = Status.valueOf(status);
        } catch (IllegalArgumentException ex) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        switch (sortDirection) {
            case ASC -> {
                Pageable pageableAsc = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.ASC, sortCategory));
                return new ResponseEntity<>(ideaService.filterIdeasByStatus(statusEnum, pageableAsc), HttpStatus.OK);
            }
            case DESC -> {
                Pageable pageableDesc = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, sortCategory));
                return new ResponseEntity<>(ideaService.filterIdeasByStatus(statusEnum, pageableDesc), HttpStatus.OK);
            }
            default -> {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        }
    }

    @Transactional
    @GetMapping("/filterIdeasByCategory")
    public ResponseEntity<Page<IdeaResponseDTO>> getIdeasByCategory(@RequestParam(required = true) String category,
                                                                    @RequestParam(required = true) int pageSize,
                                                                    @RequestParam(required = true) int pageNumber,
                                                                    @RequestParam(required = true) String sortCategory,
                                                                    @RequestParam(required = true) Sort.Direction sortDirection) {
        switch (sortDirection) {
            case ASC -> {
                Pageable pageableAsc = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.ASC, sortCategory));
                return new ResponseEntity<>(ideaService.filterIdeasByCategory(category, pageableAsc), HttpStatus.OK);
            }
            case DESC -> {
                Pageable pageableDesc = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, sortCategory));
                return new ResponseEntity<>(ideaService.filterIdeasByCategory(category, pageableDesc), HttpStatus.OK);
            }
            default -> {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        }
    }

    @Transactional
    @GetMapping("/filterIdeas")
    public ResponseEntity<Page<IdeaResponseDTO>> filterAllIdeasByParameters(@RequestParam(required = false) String title,
                                                                            @RequestParam(required = false) String text,
                                                                            @RequestParam(required = false) String status,
                                                                            @RequestParam(required = false) String category,
                                                                            @RequestParam(required = true) int pageSize,
                                                                            @RequestParam(required = true) int pageNumber,
                                                                            @RequestParam(required = true) String sortCategory,
                                                                            @RequestParam(required = true) Sort.Direction sortDirection) {
        Status statusEnum = (status != null) ? Status.valueOf(status) : null;
        switch (sortDirection) {
            case ASC -> {
                Pageable pageableAsc = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.ASC, sortCategory));
                return new ResponseEntity<>(ideaService.filterIdeasByAll(title, text, statusEnum, category, pageableAsc), HttpStatus.OK);
            }
            case DESC -> {
                Pageable pageableDesc = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, sortCategory));
                return new ResponseEntity<>(ideaService.filterIdeasByAll(title, text, statusEnum, category, pageableDesc), HttpStatus.OK);
            }
            default -> {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        }
    }
}