package com.atoss.idea.management.system.repository;

import com.atoss.idea.management.system.repository.entity.Idea;
import com.atoss.idea.management.system.repository.entity.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

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
}
