package com.atoss.idea.management.system.repository;

import com.atoss.idea.management.system.repository.entity.Subscription;
import com.atoss.idea.management.system.repository.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    List<Subscription> findByUserId(Long id);
    @Query(value = "SELECT user_id FROM Subscription WHERE idea_id = :ideaId", nativeQuery = true)
    List<Long> findUserIdByIdeaId(Long ideaId);

<<<<<<< HEAD
}
=======
    @Query(value = "SELECT user_id FROM Subscription WHERE idea_id = :ideaId", nativeQuery = true)
    List<Long> findUserIdByIdeaId(Long ideaId);
}
>>>>>>> 4851fc84e6d2b281a5499ed4b396179124ebca8d
