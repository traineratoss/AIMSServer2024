package com.atoss.idea.management.system.controller;

import java.util.List;
import com.atoss.idea.management.system.repository.dto.CommentDTO;
import com.atoss.idea.management.system.repository.dto.RequestCommentDTO;
import com.atoss.idea.management.system.repository.dto.RequestCommentReplyDTO;
import com.atoss.idea.management.system.service.implementation.CommentServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/ideas")
public class CommentController {

    private final CommentServiceImpl commentService;

    public CommentController(CommentServiceImpl commentService) {
        this.commentService = commentService;
    }

    // TODO check if the mapping is good
    @PostMapping("/")
    public ResponseEntity<Object> addComment(@RequestBody RequestCommentDTO newComment) {

        commentService.addComment(newComment);

        return new ResponseEntity<>("Comment was dope", HttpStatus.OK);
    }

    @PostMapping("/comments")
    public ResponseEntity<Object> addReply(@RequestBody RequestCommentReplyDTO requestCommentReplyDTO) {

        commentService.addReply(requestCommentReplyDTO);

        return new ResponseEntity<>("Reply fired", HttpStatus.OK);
    }

    @GetMapping("/")
    public List<CommentDTO> getAllCommentsByIdeaId(@PathVariable("idea_id") Long ideaId) {
        return commentService.getAllCommentsByIdeaId(ideaId);
    }

    @GetMapping("/comments")
    public List<CommentDTO> getAllRepliesByCommentId(@PathVariable("idea_id") Long ideaId, @PathVariable("comment_id") Long commentId) {
        return commentService.getAllRepliesByCommentId(ideaId, commentId);
    }

}
