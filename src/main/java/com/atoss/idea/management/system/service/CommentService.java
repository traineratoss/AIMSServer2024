package com.atoss.idea.management.system.service;

import com.atoss.idea.management.system.repository.dto.CommentDTO;
import com.atoss.idea.management.system.repository.dto.RequestCommentDTO;
import com.atoss.idea.management.system.repository.dto.RequestCommentReplyDTO;
import com.atoss.idea.management.system.repository.entity.Comment;

import java.util.Date;
import java.util.List;

public interface CommentService {

    String getElapsedTime(Date creationDate);

    CommentDTO addComment(RequestCommentDTO newComment);

    CommentDTO addReply(RequestCommentReplyDTO requestCommentReplyDTO);

    CommentDTO getComment(Long commentId);

    CommentDTO getReply(Long commentID);

    List<CommentDTO> getAllComments();

    List<RequestCommentDTO> getAllCommentsByIdeaId(RequestCommentDTO requestCommentDTO);

    List<RequestCommentReplyDTO> getAllRepliesByCommentId(RequestCommentReplyDTO requestCommentReplyDTO);

    CommentDTO updateComment(Comment comment);

    void deleteComment(Long id);

}
