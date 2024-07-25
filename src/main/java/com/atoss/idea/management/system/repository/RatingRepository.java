package com.atoss.idea.management.system.repository;

import com.atoss.idea.management.system.repository.entity.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {
    Optional<Rating> findByIdeaIdAndUserId(Long idea_id, Long user_id);

    List<Rating> findByIdeaId(Long id);

}

