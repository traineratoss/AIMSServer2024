package com.atoss.idea.management.system.service;


import com.atoss.idea.management.system.repository.dto.CommentDTO;
import com.atoss.idea.management.system.repository.entity.Comment;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public interface CommentService {
    CommentDTO addComment(Comment comment, Long ideaId);

    CommentDTO addReply(Comment comment, Long parentId);

    CommentDTO getComment(Long commentId);

    List<CommentDTO> getAllComments();

    List<CommentDTO> getAllCommentsByIdeaId(Long ideaId);

    List<CommentDTO> getAllRepliesByCommentId(Long parentId);

    CommentDTO updateComment(Comment comment);

    void deleteComment(Long id);

}
