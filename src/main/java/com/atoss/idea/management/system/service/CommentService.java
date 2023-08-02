package com.atoss.idea.management.system.service;

import com.atoss.idea.management.system.repository.dto.RequestCommentDTO;
import com.atoss.idea.management.system.repository.dto.RequestCommentReplyDTO;
import com.atoss.idea.management.system.repository.dto.ResponseCommentDTO;
import com.atoss.idea.management.system.repository.dto.ResponseCommentReplyDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Date;
import java.util.List;

public interface CommentService {

    String getTimeForComment(Long id);

    String getElapsedTime(Date creationDate);

    ResponseCommentDTO addComment(RequestCommentDTO newComment);

    ResponseCommentReplyDTO addReply(RequestCommentReplyDTO requestCommentReplyDTO);

    Page<ResponseCommentDTO> getAllPagedCommentsByIdeaId(Long ideaId, Pageable pageable);

    Page<ResponseCommentReplyDTO> getAllRepliesByCommentId(Long commentId, Pageable pageable);

    void deleteComment(Long commentId);

}
