package com.atoss.idea.management.system.controller;

import com.atoss.idea.management.system.repository.dto.IdeaRequestDTO;
import com.atoss.idea.management.system.repository.dto.IdeaResponseDTO;
import com.atoss.idea.management.system.repository.dto.IdeaUpdateDTO;
import com.atoss.idea.management.system.repository.entity.Status;
import com.atoss.idea.management.system.service.IdeaService;
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

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/aims/api/v1/ideas")
public class IdeaController {

    private final IdeaService ideaService;

    /**
     * Constructor for the Idea Controller
     *
     * @param ideaService the Idea Service interface
     */
    public IdeaController(IdeaService ideaService) {
        this.ideaService = ideaService;
    }

    /**
     * Adds a new idea to a user that has a given username
     *
     * @param idea the idea to be added
     * @param username the username associated with the idea
     * @return a Response Entity containing the response DTO of the added idea
     */
    @PostMapping("/create")
    @Transactional
    public ResponseEntity<IdeaResponseDTO> addIdea(@RequestBody IdeaRequestDTO idea,
                                                   @RequestParam String username) throws UnsupportedEncodingException {
        return new ResponseEntity<>(ideaService.addIdea(idea, username), HttpStatus.OK);
    }

    /**
     * Gets an idea by a given id
     *
     * @param id the id of the idea
     * @return a Response Entity containing the response DTO of the idea received
     */
    @GetMapping("/get")
    @Transactional
    public ResponseEntity<IdeaResponseDTO> getIdeaById(@RequestParam(required = true) Long id) {
        return new ResponseEntity<>(ideaService.getIdeaById(id), HttpStatus.OK);
    }

    /**
     * Updates an idea by a given id
     *
     * @param id the id of the idea we want to update
     * @param ideaUpdateDTO the DTO containing the updated idea information
     * @return a Response Entity containing the response DTO of the updated idea
     */
    @PatchMapping("/update")
    @Transactional
    public ResponseEntity<IdeaResponseDTO> updateIdeaById(@RequestParam(required = true) Long id,
                                                          @RequestBody IdeaUpdateDTO ideaUpdateDTO) throws UnsupportedEncodingException {
        return new ResponseEntity<>(ideaService.updateIdeaById(id, ideaUpdateDTO), HttpStatus.OK);
    }

    /**
     * Deletes an idea by a given id
     *
     * @param id the id of the idea we want to delete
     * @return a Response Entity containing a text that suggests the fact that we successfully
     *         deleted the idea
     */
    @DeleteMapping("/delete")
    @Transactional
    public ResponseEntity<String> deleteIdeaById(@RequestParam(required = true) Long id)  {
        ideaService.deleteIdeaById(id);
        return new ResponseEntity<>("Idea successfully deleted", HttpStatus.OK);
    }

    /**
     * Returns all the ideas paged
     *
     * @param pageSize the size of the page
     * @param pageNumber the number of the page
     * @param sortCategory the category we sort the ideas by
     * @param sortDirection the direction we want the ideas to be sorted by
     * @return a Response Entity containing an IdeaPage DTO ( the total number of ideas in all the pages +
     *         the page containing the list of ideas )
     */
    @Transactional
    @GetMapping("/all")
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

    /**
     * Returns all the ideas that belong to a User, paged
     *
     * @param username the username of the User whose ideas belong to
     * @param pageSize the size of the page
     * @param pageNumber the number of the page
     * @param sortCategory the category we sort the ideas by
     * @param sortDirection the direction we want the ideas to be sorted by
     * @return a Response Entity containing an IdeaPage DTO ( the total number of ideas in all the pages +
     *         the page containing the list of ideas )
     */
    @Transactional
    @GetMapping("/allByUser")
    public ResponseEntity<Page<IdeaResponseDTO>> getAllIdeasByUserUsername(@RequestParam(required = true) String username,
                                                                 @RequestParam(required = true) int pageSize,
                                                                 @RequestParam(required = true) int pageNumber,
                                                                 @RequestParam(required = true) String sortCategory,
                                                                 @RequestParam(required = true) Sort.Direction sortDirection) {
        switch (sortDirection) {
            case ASC -> {
                Pageable pageableAsc = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.ASC, sortCategory));
                return new ResponseEntity<>(ideaService.getAllIdeasByUserUsername(username, pageableAsc), HttpStatus.OK);
            }
            case DESC -> {
                Pageable pageableDesc = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, sortCategory));
                return new ResponseEntity<>(ideaService.getAllIdeasByUserUsername(username, pageableDesc), HttpStatus.OK);
            }
            default -> {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
        }
    }

    /**
     * Filters ideas based on specified criteria
     *
     * @param title the ideas matching the specified title criteria
     * @param text the ideas matching the specified text criteria
     * @param status a string that will be converted into an array
     *               of Statuses and filter the ideas matching these
     * @param category a string that will be converted into an array
     *                 of Categories and filter the ideas matching these
     * @param user a string that will be converted into an array
     *             of Users and filter the ideas matching these
     * @param selectedDateFrom the ideas matching the specified selected date from
     * @param selectedDateTo the ideas matching the specified selected date to
     * @param pageSize the size of the page
     * @param pageNumber the number of the page
     * @param username if not null, returns filtered ideas belonging to the specified username
     * @param sortDirection the direction we want the ideas to be sorted by
     * @return a Response Entity containing an IdeaPage DTO ( the total number of ideas in all the pages +
     *         the page containing the list of ideas )
     */
    @Transactional
    @GetMapping("/filter")
    public ResponseEntity<Page<IdeaResponseDTO>> filterAllIdeasByParameters(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String text,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String user,
            @RequestParam(required = false) String selectedDateFrom,
            @RequestParam(required = false) String selectedDateTo,
            @RequestParam(required = true) int pageNumber,
            @RequestParam(required = true) int pageSize,
            @RequestParam(required = false) String username,
            @RequestParam(required = true) String sortDirection) {

        List<String> categories = new ArrayList<>();
        if (category != null && !category.isEmpty()) {
            categories = Arrays.asList(category.split(","));
        }

        List<String> users = new ArrayList<>();
        if (user != null && !user.isEmpty() && username == null) {
            users = Arrays.asList(user.split(","));
        }

        List<String> statusStrings = new ArrayList<>();
        if (status != null && !status.isEmpty()) {
            statusStrings = Arrays.asList(status.split(","));
        }
        List<Status> statusEnums = statusStrings.stream().map(s -> Status.valueOf(s)).toList();
        // pentru a converti corect, din String in Status nu stie in QueryCreator

        Pageable pageableAsc = PageRequest.of(pageNumber, pageSize);

        return new ResponseEntity<>(ideaService.filterIdeasByAll(title,
                text, statusEnums, categories, users, selectedDateFrom, selectedDateTo, sortDirection, username, pageableAsc), HttpStatus.OK);
    }

}