package com.atoss.idea.management.system.repository;

import com.atoss.idea.management.system.repository.dto.ResponseCommentDTO;
import com.atoss.idea.management.system.repository.dto.ResponseCommentReplyDTO;
import com.atoss.idea.management.system.repository.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    Page<Comment> findAllByIdeaId(Long ideaId, Pageable pageable);

    Page<Comment> findAllByParentId(Long parentId, Pageable pageable);
}
