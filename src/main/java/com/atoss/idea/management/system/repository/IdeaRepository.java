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

    Page<Idea> findAllByUserId(Long id, Pageable pageable);

    @Query("SELECT i FROM Idea i WHERE i.title LIKE %?1%")
    Page<Idea> findAllByTitle(String title, Pageable pageable);

    @Query("SELECT i FROM Idea i WHERE i.text LIKE %?1%")
    Page<Idea> findAllByText(String text, Pageable pageable);

    @Query("SELECT i FROM Idea i WHERE i.status LIKE %?1%")
    Page<Idea> findAllByStatus(String status, Pageable pageable);

    @Query("SELECT DISTINCT i FROM Idea i JOIN i.categoryList c WHERE c.text LIKE %:category%")
    Page<Idea> findAllByCategoryName(@Param("category") String categoryName, Pageable pageable);
}
