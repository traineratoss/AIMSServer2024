package com.atoss.idea.management.system.repository;

import com.atoss.idea.management.system.repository.entity.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    List<Subscription> findByUserId(Long id);

    List<Subscription> findByIdeaId(Long id);
}
