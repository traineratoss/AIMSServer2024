package com.atoss.idea.management.system.repository;

import com.atoss.idea.management.system.repository.entity.Idea;
import com.atoss.idea.management.system.repository.entity.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IdeaRepository extends JpaRepository<Idea, Long> {

    /**
     * Searches in the Database the ideas who belong to a user with a
     * given username
     *
     * @param username the username of the User whose ideas belong to
     * @param pageable it contains all the necessary information about the
     *                 requested page, such as page size, page number,
     *                 sort category and sort direction
     * @return returns from the DB all the ideas who belong to a user with a
     *         given username, paged
     */
    @Query("SELECT i FROM Idea i JOIN i.user u WHERE u.username = :username")
    Page<Idea> findAllByUserUsername(@Param("username") String username, Pageable pageable);

    /**
     * Counts in the Database the number of idea who have a specific status
     *
     * @param status the given Status
     * @return the number of ideas who have this status
     */
    Long countByStatus(Status status);

    /**
     * counts grouped by status , very important , [0] means OPEN , [1] means DRAFT and [2] means IMPLEMENTED
     *
     * @param selectedDateFrom date from which we select
     * @param selectedDateTo data up to selection
     * @return a list of statuses
     */
    @Query(value = " SELECT COUNT(*) AS frequency FROM idea i "
            +
            " WHERE (i.date between cast(:selectedDateFrom AS timestamp) and cast(:selectedDateTo AS timestamp))"
            +
            " GROUP BY i.status ", nativeQuery = true)
    List<Long> countStatusByDate(@Param("selectedDateFrom") String selectedDateFrom,
                           @Param("selectedDateTo") String selectedDateTo);

    /**
     * returns all ideas in order to be further processed
     *  Use-cases : if we want te get more detailed  info about statistics generated from a certain given time interval
     *
     * @param selectedDateFrom date from which we select
     * @param selectedDateTo data up to selection
     * @return all ideas within a certain given time interval
     */
    @Query(value = " SELECT * "
            +
            " FROM idea i"
            +
            " WHERE (i.date between cast(:selectedDateFrom AS timestamp) and cast(:selectedDateTo AS timestamp)) ", nativeQuery = true)
    List<Idea> filterIdeasByDate(@Param("selectedDateFrom") String selectedDateFrom,
                                 @Param("selectedDateTo") String selectedDateTo);

}
