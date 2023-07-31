package com.atoss.idea.management.system.repository;

import com.atoss.idea.management.system.repository.entity.Idea;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface IdeaRepository extends JpaRepository<Idea, Long> {
    Idea findIdeaById(Long id);

    @Query("SELECT i FROM Idea i JOIN i.user u WHERE u.username = :username")
    Page<Idea> findAllByUserUsername(@Param("username") String username, Pageable pageable);

}
