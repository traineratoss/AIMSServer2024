package com.atoss.idea.management.system.controller;

import com.atoss.idea.management.system.repository.dto.*;
import com.atoss.idea.management.system.repository.entity.Rating;
import com.atoss.idea.management.system.repository.entity.Status;
import com.atoss.idea.management.system.repository.entity.Subscription;
import com.atoss.idea.management.system.service.IdeaService;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;


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
     * @param idea     the idea to be added
     * @param username the username associated with the idea
     * @return a Response Entity containing the response DTO of the added idea
     */
    @PostMapping("/create")
    @Transactional
    public ResponseEntity<IdeaResponseDTO> addIdea(@RequestBody IdeaRequestDTO idea,
                                                   @RequestParam String username) throws IOException {
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
     * Handles HTTP GET requests to retrieve an idea by its ID for the purpose of updating it.
     *
     * <p>This method is transactional, ensuring that any database interactions are performed
     * within a transaction context.</p>
     *
     * @param id the ID of the idea to retrieve; must not be null
     * @return a ResponseEntity containing the IdeaResponseDTO and an HTTP status of OK
     * @throws IllegalArgumentException if the provided id is null
     */
    @GetMapping("/get/updateIdea")
    @Transactional
    public ResponseEntity<IdeaResponseDTO> getIdeaByIdForUpdateIdea(@RequestParam(required = true) Long id) {
        return new ResponseEntity<>(ideaService.getIdeaByIdForUpdateIdea(id), HttpStatus.OK);
    }

    /**
     * Updates an idea by a given id
     *
     * @param id            the id of the idea we want to update
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
     * @param       id the id of the idea we want to delete
     * @return      a Response Entity containing a text that suggests the fact that we successfully
     *              deleted the idea
     */
    @DeleteMapping("/delete")
    @Transactional
    public ResponseEntity<String> deleteIdeaById(@RequestParam(required = true) Long id) {
        ideaService.deleteIdeaById(id);
        return new ResponseEntity<>("Idea successfully deleted", HttpStatus.OK);
    }

    /**
     * Returns all the ideas paged
     *
     * @param pageSize      the size of the page
     * @param pageNumber    the number of the page
     * @param sortCategory  the category we sort the ideas by
     * @param sortDirection the direction we want the ideas to be sorted by
     * @return              a Response Entity containing an IdeaPage DTO (the total number of ideas in all the pages +
     *                      the page containing the list of ideas)
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
     * @param username      the username of the User whose ideas belong to
     * @param pageSize      the size of the page
     * @param pageNumber    the number of the page
     * @param sortCategory  the category we sort the ideas by
     * @param sortDirection the direction we want the ideas to be sorted by
     * @return              a Response Entity containing an IdeaPage DTO ( the total number of ideas in all the pages +
     *                      the page containing the list of ideas)
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
     * @param title            the ideas matching the specified title criteria
     * @param text             the ideas matching the specified text criteria
     * @param status           a string that will be converted into an array
     *                         of Statuses and filter the ideas matching these
     * @param category         a string that will be converted into an array
     *                         of Categories and filter the ideas matching these
     * @param user             a string that will be converted into an array
     *                         of Users and filter the ideas matching these
     * @param selectedDateFrom the ideas matching the specified selected date from
     * @param selectedDateTo   the ideas matching the specified selected date to
     * @param pageSize         the size of the page
     * @param pageNumber       the number of the page
     * @param username         if not null, returns filtered ideas belonging to the specified username
     * @param sortDirection    the direction we want the ideas to be sorted by
     * @param rating            rating to filter the ideas
     * @return                  a Response Entity containing an IdeaPage DTO ( the total number of ideas in all the pages +
     *                          the page containing the list of ideas )
     */
    @Transactional
    @GetMapping("/filter")
    public ResponseEntity<Page<IdeaResponseDTO>> filterAllIdeasByParameters(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String text,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String selectedDateFrom,
            @RequestParam(required = false) String selectedDateTo,
            @RequestParam(required = true) int pageNumber,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String user,
            @RequestParam(required = true) int pageSize,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String rating,
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
                text, statusEnums, categories, users, selectedDateFrom, selectedDateTo, sortDirection, username, rating, pageableAsc), HttpStatus.OK);
    }

    /**
     * add a rating to a idea
     *
     * @param ideaId            the id of the idea to modify thhe rating
     * @param userId            the id of the user to be modify the rating
     * @param ratingValue        a value which is the rating of that idea for that specific user
     * @return a Response Entity containing a response which includes the rating added for that user and idea
     */
    @Transactional
    @PostMapping("/ratings")
    public ResponseEntity<Rating> addOrUpdateRating(
            @RequestParam(required = true) Long ideaId,
            @RequestParam(required = true) Long userId,
            @RequestParam(required = true) double ratingValue) {
        Rating rating = ideaService.addOrUpdateRating(ideaId, userId, ratingValue);
        return new ResponseEntity<>(rating, HttpStatus.OK);
    }

    /**
     * find the rating based on id an user ids
     *
     * @param userId the id of the user which we search for
     * @return a Response Entity containing a response which includes the rating of that user for that idea
     */

    @Transactional
    @GetMapping("/getAllRatings")
    public ResponseEntity<List<RatingDTO>> getAllRatings(@RequestParam(required = true) Long userId) {
        return new ResponseEntity<>(ideaService.getAllRatings(userId), HttpStatus.OK);
    }

    /**
     * find the rating based on id a user id and idea id
     *
     * @param userId the id of the user which we search for
     * @param ideaId the id of the user which we search for
     * @return a Response Entity containing a response which includes the rating of that user for that idea
     */

    @Transactional
    @GetMapping("/getRating")
    public ResponseEntity<Double> getRating(@RequestParam Long ideaId, @RequestParam Long userId) {
        return new ResponseEntity<>(ideaService.getRatingByUserAndByIdea(ideaId, userId), HttpStatus.OK);
    }

    /**
     * Endpoint to get the number of ratings for a specific idea.
     *
     * @return the number of ratings for the idea
     */
    @GetMapping("/countRatings")
    public ResponseEntity<List<Map<Long, Object>>> getNumberOfRatings() {
        return new ResponseEntity<>(ideaService.getRatingsCountForEachIdea(), HttpStatus.OK);
    }

    /**
     * Adds a subscription for a user to an idea.
     *
     * @param ideaId the ID of the idea to subscribe to
     * @param userId the ID of the user subscribing to the idea
     * @return ResponseEntity containing the created Subscription object and HTTP status OK
     * @throws UnsupportedEncodingException if there is an encoding issue during the process
     */
    @Transactional
    @PostMapping("/addSubscription")
    public ResponseEntity<Subscription> addSubscription(@RequestParam(required = true) Long ideaId,
                                                        @RequestParam(required = true) Long userId)
            throws UnsupportedEncodingException {
        return new ResponseEntity<>(ideaService.addSubscription(ideaId, userId), HttpStatus.OK);
    }

    /**
     * Deletes a subscription for a user from an idea.
     *
     * @param ideaId the ID of the idea to unsubscribe from
     * @param userId the ID of the user unsubscribing from the idea
     * @return ResponseEntity containing a success message and HTTP status OK
     */
    @Transactional
    @DeleteMapping("/deleteSubscription")
    public ResponseEntity<String> deleteSubscriptionById(@RequestParam(required = true) Long ideaId,
                                                         @RequestParam(required = true) Long userId) {
        ideaService.removeSubscription(ideaId, userId);
        return new ResponseEntity<>("Subscription successfully deleted", HttpStatus.OK);
    }

    /**
     * Retrieves all subscriptions for a user.
     *
     * @param userId the ID of the user whose subscriptions are being retrieved
     * @return ResponseEntity containing a list of SubscriptionDTO objects and HTTP status OK
     */
    @Transactional
    @GetMapping("/getAllSubscriptions")
    public ResponseEntity<List<SubscriptionDTO>> getAllSubscriptions(@RequestParam Long userId) {
        return new ResponseEntity<>(ideaService.getAllSubscriptions(userId), HttpStatus.OK);
    }

    /**
     * Retrieves an idea based on the provided comment ID.
     *
     * This method handles HTTP GET requests to the "/getByComment" endpoint.
     * It expects a query parameter "commentId" which is mandatory.
     * The method is transactional, meaning that it will be executed within a database transaction context.
     *
     * @param commentId The ID of the comment used to find the associated idea.
     * @return A ResponseEntity containing the IdeaResponseDTO and an HTTP status of OK.
     */
    @GetMapping("/getByComment")
    @Transactional
    public ResponseEntity<IdeaResponseDTO> getIdeaByCommentId(@RequestParam(required = true) Long commentId) {
        return new ResponseEntity<>(ideaService.getIdeaByCommentId(commentId), HttpStatus.OK);
    }
}