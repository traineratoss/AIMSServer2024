package com.atoss.idea.management.system.service;

import com.atoss.idea.management.system.repository.dto.CommentDTO;
import com.atoss.idea.management.system.repository.dto.RequestCommentDTO;
import com.atoss.idea.management.system.repository.entity.Comment;
import java.util.List;

public interface CommentService {
    CommentDTO addComment(RequestCommentDTO newComment);

    CommentDTO addReply(Comment reply, Long ideaId, Long commentId);

    CommentDTO getComment(Long commentId);

    CommentDTO getReply(Long commentID);

    List<CommentDTO> getAllComments();

    List<CommentDTO> getAllCommentsByIdeaId(Long ideaId);

    List<CommentDTO> getAllRepliesByCommentId(Long ideaId, Long commentId);

    CommentDTO updateComment(Comment comment);

    void deleteComment(Long id);

}
