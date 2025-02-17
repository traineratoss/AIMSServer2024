package com.atoss.idea.management.system.service;

import com.atoss.idea.management.system.repository.dto.*;
import com.atoss.idea.management.system.repository.entity.Idea;
import com.atoss.idea.management.system.repository.entity.Rating;
import com.atoss.idea.management.system.repository.entity.Status;
import com.atoss.idea.management.system.repository.entity.Subscription;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

public interface IdeaService {

    /**
     * Creates an idea for the user that has a specific username
     *
     * @param idea     it is the Object we receive from the client
     * @param username the username of the User we want to attribute this idea to
     * @return the idea DTO that we want to add to the User that has
     *         the username passed as an argument
     */
    IdeaResponseDTO addIdea(IdeaRequestDTO idea, String username) throws IOException;

    /**
     * Gets an Idea by an id
     *
     * @param id the id of the idea we want to receive
     * @return the idea DTO with that specific id
     */
    IdeaResponseDTO getIdeaById(Long id);

    /**
     * Retrieves an idea by its ID for the purpose of updating it.
     *
     * @param id the ID of the idea to retrieve; must not be null
     * @return the IdeaResponseDTO containing the idea details
     * @throws IllegalArgumentException if the provided id is null
     */
    IdeaResponseDTO getIdeaByIdForUpdateIdea(Long id);

    /**
     * Updates an idea by receiving its id and the fields we want to update
     *
     * @param id            the id of the idea we want to update
     * @param ideaUpdateDTO the DTO that contains all the necessary fields to update
     * @return an updated idea DTO that overwrites the initial idea
     */
    IdeaResponseDTO updateIdeaById(Long id, IdeaUpdateDTO ideaUpdateDTO) throws UnsupportedEncodingException;

    /**
     * Deletes an idea by its id
     *
     * @param id the id of the idea we wish to delete
     */
    void deleteIdeaById(Long id);

    /**
     * Gets all the ideas paged
     *
     * @param pageable it contains all the necessary information about the
     *                 requested page, such as page size, page number,
     *                 sort category and sort direction
     * @return an IdeaPageDTO that contains a page of listed ideas and
     *         the total number of elements in all the pages
     */
    Page<IdeaResponseDTO> getAllIdeas(Pageable pageable);

    /**
     * Gets all the ideas that belong to a user paged
     *
     * @param username the username of the user we want to receive the ideas from
     * @param pageable it contains all the necessary information about the
     *                 requested page, such as page size, page number,
     *                 sort category and sort direction
     * @return an IdeaPageDTO that contains a page of listed ideas that belong
     *         to a user and the total number of elements in all the pages
     */
    Page<IdeaResponseDTO> getAllIdeasByUserUsername(String username, Pageable pageable);

    /**
     * Filters ideas based on specified criteria
     *
     * @param title            the ideas matching the specified title criteria
     * @param text             the ideas matching the specified text criteria
     * @param status           the ideas matching the specified statuses
     * @param categories       the ideas matching the specified categories
     * @param user             the ideas matching the specified users
     * @param selectedDateFrom the ideas matching the specified selected date from
     * @param selectedDateTo   the ideas matching the specified selected date to
     * @param sortDirection    the sorting direction of the pages
     * @param username         if not null, returns filtered ideas belonging to the specified username
     * @param rating           the rating to select
     * @param subscribed        subscribed status
     * @param userId            current userId
     * @param pageable         it contains all the necessary information about the
     *                         requested page, such as page size, page number,
     *                         sort category and sort direction
     * @return an IdeaPageDTO containing filtered ideas based on the given criteria
     */
    Page<IdeaResponseDTO> filterIdeasByAll(String title,
                                           String text,
                                           List<Status> status,
                                           List<String> categories,
                                           List<String> user,
                                           String selectedDateFrom,
                                           String selectedDateTo,
                                           String sortDirection,
                                           String username,
                                           String rating,
                                           Pageable pageable,
                                           Boolean subscribed,
                                           Long userId);

    /**
     * Function used to return a list of ideas by their id
     *
     * @param ideaIds list of ids
     * @return list of Idea
     */
    List<Idea> findIdeasByIds(List<Long> ideaIds);

    /**
     * Filters ideas based on date criteria
     *
     * @param selectedDateFrom the starting date for filtering
     * @param selectedDateTo   the ending date for filtering
     * @param root             the root (Idea) for constructing criteria queries
     * @param cb               the CriteriaBuilder for building criteria queries (used for more complex
     *                         queries and to prevent writing raw SQL code)
     * @param columnName       specifies the name of the desired database column to be accesed
     * @return a list of predicates to filter Ideas based on the specified date range
     */
    List<Predicate> filterByDate(String selectedDateFrom,
                                 String selectedDateTo,
                                 Root<?> root,
                                 CriteriaBuilder cb,
                                 String columnName);


    /**
     * Adds or updates a rating for a specified idea by a specific user.
     *
     * @param ideaId      the ID of the idea to rate.
     * @param userId      the ID of the user providing the rating.
     * @param ratingValue the rating value to be given to the idea.
     * @return the updated or newly created Rating object.
     */
    Rating addOrUpdateRating(Long ideaId, Long userId, Double ratingValue);

    /**
     * Calculates the average rating for a specified idea.
     *
     * @param ideaId the ID of the idea for which the average rating is to be calculated.
     * @return the average rating of the idea, or null if the idea has no ratings.
     */
    Double getAverage(Long ideaId);

    /**
     * Retrieves the rating given by a specific user for a specified idea.
     *
     * @param userId the ID of the user whose rating is to be retrieved.
     * @param ideaId the ID of the idea for which the user's rating is to be retrieved.
     * @return the rating given by the user for the idea, or null if no rating exists.
     */
    Double getRatingByUserAndByIdea(Long userId, Long ideaId);


    /**
     * checks if there are any users subscribed to the idea whose rating average has changed and
     * if there are, sends an email to notify them of the change
     *
     * @param ideaId the ID of the idea whose rating average has changed
     */
    void sendEmailForRating(Long ideaId);

    /**
     * Adds a subscription for a user to an idea.
     *
     * @param ideaId the ID of the idea to subscribe to
     * @param userId the ID of the user subscribing to the idea
     * @return the created Subscription object
     */
    Subscription addSubscription(Long ideaId, Long userId);

    /**
     * Removes a subscription for a user from an idea.
     *
     * @param ideaId the ID of the idea to unsubscribe from
     * @param userId the ID of the user unsubscribing from the idea
     */
    void removeSubscription(Long ideaId, Long userId);

    /**
     * Retrieves all subscriptions for a user.
     *
     * @param userId the ID of the user whose subscriptions are being retrieved
     * @return a list of SubscriptionDTO objects representing the user's subscriptions
     */
    List<SubscriptionDTO> getAllSubscriptions(Long userId);

    /**
     * Retrieves an IdeaResponseDTO based on the provided comment ID.
     * <p>
     * This method fetches the idea associated with the given comment ID and returns it as an IdeaResponseDTO.
     *
     * @param commentId The ID of the comment used to find the associated idea.
     * @return The IdeaResponseDTO containing the details of the found idea.
     */
    IdeaResponseDTO getIdeaByCommentId(Long commentId);

    /**
     * Gets all ratings for a specific user.
     *
     * @param userId the ID of the user
     * @return a list of rating objects representing the ratings for the specified user
     */
    List<RatingDTO> getAllRatings(Long userId);

    /**
     * Gets the number of ratings for a specific idea.
     *
     * @param ideaId the ID of the idea
     * @return the number of ratings for the idea
     */
    Long getNumberOfRatingsForIdea(Long ideaId);

    /**
     * Gets numbr of rating for each idea
     *
     * @return a list of objects
     */
    List<Map<Long, Object>> getRatingsCountForEachIdea();


}