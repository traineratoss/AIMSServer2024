package com.atoss.idea.management.system.service.implementation;

import com.atoss.idea.management.system.repository.CommentRepository;
import com.atoss.idea.management.system.repository.IdeaRepository;
import com.atoss.idea.management.system.repository.dto.CommentDTO;
import com.atoss.idea.management.system.repository.entity.Comment;
import com.atoss.idea.management.system.service.CommentService;

import java.util.List;

public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final IdeaRepository ideaRepository;

    public CommentServiceImpl(CommentRepository commentRepository, IdeaRepository ideaRepository) {
        this.commentRepository = commentRepository;
        this.ideaRepository = ideaRepository;
    }

    @Override
    public CommentDTO addComment(Comment comment, Long ideaId) {
        return null;
    }

    @Override
    public CommentDTO addReply(Comment comment, Long parentId) {
        return null;
    }

    @Override
    public CommentDTO getComment(Long id) {
        return null;
    }

    @Override
    public List<CommentDTO> getAllComments() {
        return null;
    }

    @Override
    public List<CommentDTO> getAllCommentsByIdeaId(Long ideaId) {
        return null;
    }

    @Override
    public List<CommentDTO> getAllRepliesByCommentId(Long ideaId) {
        return null;
    }

    @Override
    public CommentDTO updateComment(Comment comment) {
        return null;
    }

    @Override
    public void deleteComment(Long id) {

    }
}
