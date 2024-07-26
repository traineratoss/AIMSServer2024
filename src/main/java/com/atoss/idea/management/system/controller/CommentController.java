package com.atoss.idea.management.system.controller;

import com.atoss.idea.management.system.repository.dto.*;
import com.atoss.idea.management.system.service.CommentService;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.util.List;

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
    public ResponseEntity<ResponseCommentDTO> addComment(@RequestBody RequestCommentDTO newComment) throws UnsupportedEncodingException {

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


    /**
     * Retrieves the list of users who liked a specific comment.
     *
     * @param commentId the ID of the comment
     * @return ResponseEntity containing the list of UserResponseDTOs who liked the comment
     */
    @GetMapping("/comments/{commentId}/likes")
    public ResponseEntity<List<UserResponseDTO>> getLikesForComment(@PathVariable Long commentId) {
        List<UserResponseDTO> likes = commentService.getLikesForComment(commentId);
        return ResponseEntity.ok(likes);
    }

    /**
     * Retrieves the count of likes for a specific comment.
     *
     * @param commentId the ID of the comment
     * @return ResponseEntity containing the count of likes
     */
    @GetMapping("/comments/{commentId}/likes/count")
    public ResponseEntity<Integer> getLikesCountForComment(@PathVariable Long commentId) {
        int likesCount = commentService.getLikesCountForComment(commentId);
        return ResponseEntity.ok(likesCount);
    }

    /**
     * Adds a like to a specific comment from a specific user.
     *
     * @param commentId the ID of the comment
     * @param userId    the ID of the user
     * @return ResponseEntity containing a success message
     */
    @Transactional
    @PostMapping("/comments/like/{commentId}/{userId}")
    public ResponseEntity<String> addLike(@PathVariable Long commentId, @PathVariable Long userId) {
        commentService.addLike(commentId, userId);
        return new ResponseEntity<>("Like added successfully", HttpStatus.OK);
    }

    /**
     * Deletes a like from a specific comment by a specific user.
     *
     * @param commentId the ID of the comment
     * @param userId    the ID of the user
     */
    @Transactional
    @DeleteMapping("/comments/like/delete/{commentId}/{userId}")
    public ResponseEntity<String> deleteLikes(@PathVariable Long commentId, @PathVariable Long userId) {
        commentService.deleteLikes(commentId, userId);
        return new ResponseEntity<>("Like succesfully deleted", HttpStatus.OK);
    }

    @Transactional
    @DeleteMapping("/comments/report/delete/{commentId}/{userId}")
    public  ResponseEntity<String> deleteReport(@PathVariable Long commentId, @PathVariable Long userId) {
        commentService.deleteReport(commentId, userId);
        return new ResponseEntity<>("Report succesfully deleted", HttpStatus.OK);
    }


    /**
     * Checks if a specific user has liked a specific comment.
     *
     * @param commentId the ID of the comment
     * @param userId    the ID of the user
     * @return true if the user has liked the comment, false otherwise
     */
    @GetMapping("/comments/like/find/{commentId}/{userId}")
    public boolean existsLikeByCommentIdAndUserId(@PathVariable Long commentId, @PathVariable Long userId) {
        return commentService.existsLikeByCommentIdAndUserId(commentId, userId);
    }

    @GetMapping("/comments/reports/count/{commentId}")
    public ResponseEntity<Integer> getReportsCountForComment(@PathVariable Long commentId) {
        int reportsCount = commentService.getReportsCountForComment(commentId);
        return ResponseEntity.ok(reportsCount);
    }

    @Transactional
    @GetMapping("/comments/allByReportsNr")
    public ResponseEntity<CommentPageDTO> getAllUserByUsername(@RequestParam(required = true) int pageSize,
                                                            @RequestParam(required = true) int pageNumber,
                                                               @RequestParam(required = true) String sortCategory) {
        return new ResponseEntity<>(
                commentService.getAllCommentsByReportsNr(
                        PageRequest.of(
                                pageNumber,
                                pageSize,
                                Sort.by(Sort.Direction.ASC, "id")
                        )
                ),
                HttpStatus.OK
        );
    }


    /**
     * @param commentId the ID of the comment
     * @param userId    the ID of the user
     * @return ResponseEntity containing a success message if the report is added successfully
     * Adds a report to a specific comment from a specific user.
     *
     * This method handles the HTTP POST request to add a report for a comment by a user.
     * It calls the CommentService to perform the actual report addition logic.
     *
     */
    @Transactional
    @PostMapping("/comments/report/{commentId}/{userId}")
    public ResponseEntity<String> addReport(@PathVariable Long commentId, @PathVariable Long userId) {
        commentService.addReport(commentId, userId);
        return new ResponseEntity<>("Report added successfully", HttpStatus.OK);
    }


}
