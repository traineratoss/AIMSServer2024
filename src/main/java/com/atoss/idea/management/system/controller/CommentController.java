package com.atoss.idea.management.system.controller;

import com.atoss.idea.management.system.repository.dto.RequestCommentDTO;
import com.atoss.idea.management.system.repository.dto.RequestCommentReplyDTO;
import com.atoss.idea.management.system.repository.dto.ResponseCommentDTO;
import com.atoss.idea.management.system.repository.dto.ResponseCommentReplyDTO;
import com.atoss.idea.management.system.service.CommentService;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/aims/api/v1/ideas")
public class CommentController {

    private final CommentService commentService;

    /**
     * CONSTRUCTOR: for constructor dependency injection
     *
     * @param commentService used for injecting the CommentService in order to access and use its methods
     */
    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    /**
     * Performs a Post Request in order to create a new comment
     *
     * Method receives data in json format from the client's request (as a @RequestBody)
     * Then a responseCommentDTO object is created and saved in the database using the CommentService "addComment" method
     * In the end, a ResponseEntity of type ResponseCommentDTO is returned
     *
     * @param newComment the data received from the client representing the comment that is going to be added
     * @return ResponseEntity with the resulted object and the HttpStatus
     */
    @Transactional
    @PostMapping("/comments")
    @ResponseBody
    public ResponseEntity<ResponseCommentDTO> addComment(@RequestBody RequestCommentDTO newComment) {

        ResponseCommentDTO responseCommentDTO = commentService.addComment(newComment);

        return new ResponseEntity<ResponseCommentDTO>(responseCommentDTO, HttpStatus.OK);
    }

    /**
     * Performs a Post Request in order to create a new reply
     *
     * Method receives data in json format from the client's request (as a @RequestBody)
     * Then a responseCommentReplyDTO object is created and saved in the database using the CommentService "addReply" method
     * In the end, a ResponseEntity of type ResponseCommentReplyDTO is returned
     *
     * @param requestCommentReplyDTO the data received from the client representing the reply that is going to be added
     * @return ResponseEntity with the resulted object and the HttpStatus
     */
    @Transactional
    @PostMapping("/comments/reply")
    public ResponseEntity<ResponseCommentReplyDTO> addReply(@RequestBody RequestCommentReplyDTO requestCommentReplyDTO) {

        ResponseCommentReplyDTO responseCommentReplyDTO = commentService.addReply(requestCommentReplyDTO);

        return new ResponseEntity<ResponseCommentReplyDTO>(responseCommentReplyDTO, HttpStatus.OK);
    }


    /**
     * Performs a Get Request in order to get all comments of a certain idea in a paginated form
     *
     * Method takes the paging parametres: pageSize, pageNumber and sortCategory to create a Pageable object
     * The CommentService method "getAllPagedCommentsByIdeaId" gets all the comments that belong to a certain idea in the pageable object format
     * In the end, a ResponseEntity is returned
     *
     * @param ideaId the unique identifier for the idea we are searching for
     * @param pageSize how many elements will be displayed on the page
     * @param pageNumber the page accesed
     * @param sortCategory DESC so that we get the comments from newest to oldest
     * @return ResponseEntity with the resulted paginated list and the HttpStatus
     */
    @Transactional
    @GetMapping("/comments")
    public  ResponseEntity<Page<ResponseCommentDTO>> getAllCommentsByIdeaIdWithPaging(
                                                                 @RequestParam(required = true) Long ideaId,
                                                                 @RequestParam(required = true) int pageSize,
                                                                 @RequestParam(required = true) int pageNumber,
                                                                 @RequestParam(required = true) String sortCategory) {

        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, sortCategory));

        return new ResponseEntity<>(commentService.getAllPagedCommentsByIdeaId(ideaId, pageable), HttpStatus.OK);
    }

    /**
     * Performs a Get Request in order to get all replies of a certain comment in a paginated form
     *
     * Method takes the paging parametres: pageSize, pageNumber and sortCategory to create a Pageable object
     * The CommentService method "getAllRepliesByCommentId" gets all the replies that belong to a certain comment in the pageable object format
     * In the end, a ResponseEntity is returned
     *
     * @param commentId the unique identifier for the comment we are searching for
     * @param pageSize how many elements will be displayed on the page
     * @param pageNumber the page accesed
     * @param sortCategory DESC so that we get the replies from newest to oldest
     * @return ResponseEntity with the resulted paginated list and the HttpStatus
     */
    @Transactional
    @GetMapping("/comments/replies")
    public ResponseEntity<Page<ResponseCommentReplyDTO>> getAllRepliesByCommentId(@RequestParam(name = "commentId") Long commentId,
                                                                  @RequestParam(required = true) int pageSize,
                                                                  @RequestParam(required = true) int pageNumber,
                                                                  @RequestParam(required = true) String sortCategory) {

        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, sortCategory));

        return new ResponseEntity<>(commentService.getAllRepliesByCommentId(commentId, pageable), HttpStatus.OK);
    }

    /**
     * Performs a Get Request in order to get the time that has passed since a comment was created
     *
     * Method gets the comment id as a RequestParam
     * Then, the id is used in the CommentService method "getTimeForComment" and the result is returned
     *
     * @param id the unique identifier for the comment we are searching for
     * @return time since the comment was created
     */
    @Transactional
    @GetMapping("/test")
    public String getTime(@RequestParam(name = "id") Long id) {
        return commentService.getTimeForComment(id);
    }

    /**
     * Performs a Delete Request in order to delete a certain comment
     *
     * Method gets the id of the comment that is going to be deleted as a RequestParam
     * The, the id is used in the CommentService method "deleteComment" and the deleting operation happens
     *
     * @param commentId the unique identifier for the comment we are going to delete
     */
    @Transactional
    @DeleteMapping("/comments")
    public void deleteComment(@RequestParam(name = "commentId") Long commentId) {
        commentService.deleteComment(commentId);
    }
}
