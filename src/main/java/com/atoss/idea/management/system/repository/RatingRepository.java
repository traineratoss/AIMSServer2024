package com.atoss.idea.management.system.repository;

import com.atoss.idea.management.system.repository.entity.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
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
     * Returns rating
     *
     * @param id the id of the idea
     * @return the rating of that specific idea by id
     */
    List<Rating> findByIdeaId(Long id);

}

