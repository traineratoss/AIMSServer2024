package com.atoss.idea.management.system.service;


import com.atoss.idea.management.system.repository.dto.CommentDTO;
import com.atoss.idea.management.system.repository.entity.Comment;
import org.springframework.stereotype.Service;

@Service
public interface CommentService {
    CommentDTO addComment(Comment comment);

    CommentDTO getComment(Long id);

    CommentDTO updateComment(Comment comment);

    void deleteComment(Long id);

}
