package com.atoss.idea.management.system.repository;

import com.atoss.idea.management.system.repository.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    /**
     * Overwriting default "findAllById" CRUD method in order to return a Page of type Comment
     *
     * Method returns a Page of comments that belong to a certain idea
     *
     * @param ideaId the unique identifier for the idea we are working with
     * @param pageable the Pageable object containing pagination information
     * @return Page of type Comment with the comments of the desired idea
     */
    Page<Comment> findAllByIdeaId(Long ideaId, Pageable pageable);

    /**
     * Overwriting default "findAllById" CRUD method in order to return a Page of type Comment
     *
     * Method returns a Page of replies that belong to a certain comment
     *
     * @param parentId the unique identifier for the comment we are working with
     * @param pageable the Pageable object containing pagination information
     * @return Page of type Comment with the replies of the comment
     */
    Page<Comment> findAllByParentId(Long parentId, Pageable pageable);
}
