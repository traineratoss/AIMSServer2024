package com.atoss.idea.management.system.service;

import com.atoss.idea.management.system.exception.FieldValidationException;
import com.atoss.idea.management.system.repository.dto.FilteredStatisticsDTO;
import com.atoss.idea.management.system.repository.dto.IdeaPageDTO;
import com.atoss.idea.management.system.repository.dto.IdeaRequestDTO;
import com.atoss.idea.management.system.repository.dto.IdeaResponseDTO;
import com.atoss.idea.management.system.repository.dto.IdeaUpdateDTO;
import com.atoss.idea.management.system.repository.dto.StatisticsDTO;
import com.atoss.idea.management.system.repository.entity.Idea;
import com.atoss.idea.management.system.repository.entity.Status;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface IdeaService {

    /**
     * Creates an idea for the user that has a specific username
     *
     * @param idea it is the Object we receive from the client
     * @param username the username of the User we want to attribute this idea to
     * @return the idea DTO that we want to add to the User that has
     *         the username passed as an argument
     */
    IdeaResponseDTO addIdea(IdeaRequestDTO idea, String username);

    /**
     * Gets an Idea by an id
     *
     * @param id the id of the idea we want to receive
     * @return the idea DTO with that specific id
     * @throws FieldValidationException if the idea with the given id isn't found
     *                                  in the database
     */
    IdeaResponseDTO getIdeaById(Long id);

    /**
     * Updates an idea by receiving its id and the fields we want to update
     *
     * @param id the id of the idea we want to update
     * @param ideaUpdateDTO the DTO that contains all the necessary fields to update
     * @return an updated idea DTO that overwrites the initial idea
     */
    IdeaResponseDTO updateIdeaById(Long id, IdeaUpdateDTO ideaUpdateDTO);

    /**
     * Deletes an idea by its id
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
    IdeaPageDTO getAllIdeas(Pageable pageable);

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
    IdeaPageDTO getAllIdeasByUserUsername(String username, Pageable pageable);

    /**
     * Filters ideas based on specified criteria
     *
     * @param title the ideas matching the specified title criteria
     * @param text the ideas matching the specified text criteria
     * @param status  the ideas matching the specified statuses
     * @param categories the ideas matching the specified categories
     * @param user the ideas matching the specified users
     * @param selectedDateFrom the ideas matching the specified selected date from
     * @param selectedDateTo the ideas matching the specified selected date to
     * @param sortDirection the sorting direction of the pages
     * @param username if not null, returns filtered ideas belonging to the specified username
     * @param pageable  it contains all the necessary information about the
     *                  requested page, such as page size, page number,
     *                  sort category and sort direction
     * @return an IdeaPageDTO containing filtered ideas based on the given criteria
     */
    IdeaPageDTO filterIdeasByAll(String title,
                                 String text,
                                 List<Status> status,
                                 List<String> categories,
                                 List<String> user,
                                 String selectedDateFrom,
                                 String selectedDateTo,
                                 String sortDirection,
                                 String username,
                                 Pageable pageable);

    /**
     * Filters statistics based on specified criteria
     *
     * @param statuses the ideas matching the specified statuses
     * @param categories the ideas matching the specified categories
     * @param users the ideas matching the specified users
     * @param selectedDateFrom the ideas matching the specified selected date from
     * @param selectedDateTo the ideas matching the specified selected date to
     * @return a statistics DTO
     */
    FilteredStatisticsDTO getStatisticsByDate(List<Status> statuses,
                                              List<String> categories,
                                              List<String> users,
                                              String selectedDateFrom,
                                              String selectedDateTo);

    /**
     * Gets the general statistics of the application
     *
     * @return a statistic DTO that contains all the field necessary for
     *         the statistics
     */
    StatisticsDTO getGeneralStatistics();

    /**
     * Gets filtered stats - WORKING ON THIS
     *
     * @param ideaPageDTO the idea page dto - working on this
     * @return a dto of Filtered Statistics
     */
    FilteredStatisticsDTO getFilteredStatistics(IdeaPageDTO ideaPageDTO);

    /**
     * Filters ideas based on date criteria
     *
     * @param selectedDateFrom the starting date for filtering
     * @param selectedDateTo the ending date for filtering
     * @param root the root (Idea) for constructing criteria queries
     * @param cb the CriteriaBuilder for building criteria queries (used for more complex
     *           queries and to prevent writing raw SQL code)
     * @return a list of predicates to filter Ideas based on the specified date range
     */
    List<Predicate> filterByDate(String selectedDateFrom, String selectedDateTo, Root<Idea> root, CriteriaBuilder cb);

}