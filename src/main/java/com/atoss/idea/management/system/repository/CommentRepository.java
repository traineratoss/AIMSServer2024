package com.atoss.idea.management.system.repository;

import com.atoss.idea.management.system.repository.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByIdeaId(Long ideaId);

}
