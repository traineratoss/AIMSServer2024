package com.atoss.idea.management.system.repository;

import com.atoss.idea.management.system.repository.entity.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    /**
     * searched in the database for subscriptions which a certain user has
     *
     * @param id the given user id
     * @return all subscriptions of the user with the given id
     */
    List<Subscription> findByUserId(Long id);

    /**
     * searches in the database for the users which have subscribed to the given idea
     *
     * @param ideaId the given idea id
     * @return the user ids belonging to the users who are subscribed to the idea with the given id
     */
    @Query(value = "SELECT user_id FROM Subscription WHERE idea_id = :ideaId", nativeQuery = true)
    List<Long> findUserIdByIdeaId(Long ideaId);

    Optional<Subscription> findByIdeaIdAndUserId(Long ideaId, Long userId);

}

