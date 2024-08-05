package com.atoss.idea.management.system.repository;

import com.atoss.idea.management.system.repository.entity.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;


@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {

    /**
     * Returns the idea rating of the user id and idea id
     *
     * @param ideaId the id of the idea
     * @param userId the id of the user
     * @return the rating of that specific idea of that specific user
     */
    Optional<Rating> findByIdeaIdAndUserId(Long ideaId, Long userId);

    /**
     * Finds all ratings by the specified user ID.
     *
     * @param userId the ID of the user
     * @return a list of ratings for the specified user
     */
    List<Rating> findByUserId(Long userId);

    /**
     * Returns rating
     *
     * @param id the id of the idea
     * @return the rating of that specific idea by id
     */
    List<Rating> findByIdeaId(Long id);

    /**
     * Counts the number of ratings for the specified idea ID.
     *
     * @param ideaId the ID of the idea
     * @return the number of ratings for the specified idea
     */
    Long countByIdeaId(Long ideaId);

    /**
     * Returns a list of objects where each object contains the idea ID and the count of ratings for that idea.
     *
     * @return a list of objects with idea ID and the count of ratings
     */
    @Query(value = "SELECT r.idea_id AS ideaId, COUNT(r) AS ratingCount FROM Rating r GROUP BY r.idea_id", nativeQuery = true)
    List<Map<Long, Object>> countRatingsForEachIdea();

}

