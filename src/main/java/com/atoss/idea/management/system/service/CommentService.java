package com.atoss.idea.management.system.service;

import com.atoss.idea.management.system.repository.dto.CommentDTO;
import com.atoss.idea.management.system.repository.dto.RequestCommentDTO;
import com.atoss.idea.management.system.repository.dto.RequestCommentReplyDTO;
import com.atoss.idea.management.system.repository.dto.ResponseCommentDTO;
import com.atoss.idea.management.system.repository.entity.Comment;
import com.atoss.idea.management.system.repository.dto.ResponseCommentReplyDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Date;
import java.util.List;

public interface CommentService {

    public String getTimeForComment(Long id);

    String getElapsedTime(Date creationDate);

    ResponseCommentDTO addComment(RequestCommentDTO newComment);

    ResponseCommentReplyDTO addReply(RequestCommentReplyDTO requestCommentReplyDTO);

    CommentDTO getComment(Long commentId);

    CommentDTO getReply(Long commentID);

    List<CommentDTO> getAllComments();

    List<ResponseCommentDTO> getAllCommentsByIdeaId(Long ideaId);

    Page<ResponseCommentDTO> getAllCommentsByIdeaIdWithPaging(Long ideaId, Pageable pageable);

    List<ResponseCommentReplyDTO> getAllRepliesByCommentId(RequestCommentReplyDTO requestCommentReplyDTO);

    CommentDTO updateComment(Comment comment);

    void deleteComment(Long id);

}
