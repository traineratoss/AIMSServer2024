package com.atoss.idea.management.system.repository;

import com.atoss.idea.management.system.repository.entity.Rating;
import com.atoss.idea.management.system.repository.entity.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    List<Subscription> findByUserId(Long id);

    @Query(value = "SELECT user_id FROM Subscription WHERE idea_id = :ideaId", nativeQuery = true)
    List<Long> findUserIdByIdeaId(Long ideaId);

    Optional<Subscription> findByIdeaIdAndUserId(Long ideaId, Long userId);
}

