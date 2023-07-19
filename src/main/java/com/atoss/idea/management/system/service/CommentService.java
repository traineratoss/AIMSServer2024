package com.atoss.idea.management.system.service;

import com.atoss.idea.management.system.repository.dto.CommentDTO;
import com.atoss.idea.management.system.repository.entity.Comment;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public interface CommentService {
    Comment addComment(String text, Long ideaId, Long userId);

    Comment addReply(String text, Long parentId, Long userId);

    CommentDTO getComment(Long commentId);

    List<CommentDTO> getAllComments();

    List<CommentDTO> getAllCommentsByIdeaId(Long ideaId);

    List<CommentDTO> getAllRepliesByCommentId(Long parentId);

    Comment updateComment(Comment comment);

    void deleteComment(Long id);

}
