package com.atoss.idea.management.system.controller;

import java.util.List;
import com.atoss.idea.management.system.repository.dto.RequestCommentDTO;
import com.atoss.idea.management.system.repository.dto.RequestCommentReplyDTO;
import com.atoss.idea.management.system.repository.dto.ResponseCommentDTO;
import com.atoss.idea.management.system.repository.dto.ResponseCommentReplyDTO;
import com.atoss.idea.management.system.service.implementation.CommentServiceImpl;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@CrossOrigin(origins = "*", maxAge = 3600)
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

    @Transactional
    @GetMapping("/all")
    public  ResponseEntity<Page<ResponseCommentDTO>> getAllCommentsByIdeaIdWithPaging(
                                                                 @RequestParam(required = true) Long ideaId,
                                                                 @RequestParam(required = true) int pageSize,
                                                                 @RequestParam(required = true) int pageNumber,
                                                                 @RequestParam(required = true) String sortCategory) {
        return new ResponseEntity<>(commentService.getAllCommentsByIdeaIdWithPaging(ideaId,
                PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.ASC, sortCategory))), HttpStatus.OK);
    }

    @GetMapping("/comments")
    public List<ResponseCommentDTO> getAllCommentsByIdeaId(@RequestParam(name = "ideaId") Long ideaId) {
        return commentService.getAllCommentsByIdeaId(ideaId);
    }

    @GetMapping("/comments/replies")
    public List<ResponseCommentReplyDTO> getAllRepliesByCommentId(@RequestBody RequestCommentReplyDTO requestCommentReplyDTO) {
        return commentService.getAllRepliesByCommentId(requestCommentReplyDTO);
    }

    @GetMapping("/test")
    public String getTime(@RequestParam(name = "id") Long id) {
        return commentService.getTimeForComment(id);
    }
}
