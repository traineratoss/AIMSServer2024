package com.atoss.idea.management.system.controller;

import java.util.List;
import com.atoss.idea.management.system.repository.dto.CommentDTO;
import com.atoss.idea.management.system.service.implementation.CommentServiceImpl;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.atoss.idea.management.system.repository.entity.Comment;

@RestController
public class CommentController {

    private final CommentServiceImpl commentService;

    public CommentController(CommentServiceImpl commentService) {
        this.commentService = commentService;
    }

    @PostMapping("/ideas/{idea_id}/comments")
    public CommentDTO addComment(@RequestBody Comment comment, @PathVariable("idea_id") Long ideaId) {
        return commentService.addComment(comment, ideaId);
    }

    @PostMapping("/ideas/{idea_id}/comments/{comment_id}")
    public CommentDTO addReply(@RequestBody Comment reply, @PathVariable("idea_id") Long ideaId, @PathVariable("comment_id") Long commentId) {
        return commentService.addReply(reply, ideaId, commentId);
    }

    @GetMapping("/ideas/{idea_id}/comments")
    public List<CommentDTO> getAllCommentsByIdeaId(@PathVariable("idea_id") Long ideaId) {
        return commentService.getAllCommentsByIdeaId(ideaId);
    }

    @GetMapping("/ideas/{idea_id}/comments/{comment_id}")
    public List<CommentDTO> getAllRepliesByCommentId(@PathVariable("idea_id") Long ideaId, @PathVariable("comment_id") Long commentId) {
        return commentService.getAllRepliesByCommentId(ideaId, commentId);
    }

}
