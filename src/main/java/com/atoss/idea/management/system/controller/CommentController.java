package com.atoss.idea.management.system.controller;

import com.atoss.idea.management.system.repository.dto.*;
import com.atoss.idea.management.system.repository.entity.ReviewStatus;
import com.atoss.idea.management.system.service.CommentService;
import jakarta.transaction.Transactional;

import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.util.List;

@RestController
@Log4j2
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
     * <p>
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
        if (log.isInfoEnabled()) {
            log.info("Received request to add comment");
        }
        try {
            ResponseCommentDTO responseCommentDTO = commentService.addComment(newComment);
            if (log.isInfoEnabled()) {
                log.info("Successfully added comment");
            }
            return new ResponseEntity<>(responseCommentDTO, HttpStatus.OK);
        } catch (UnsupportedEncodingException e) {
            if (log.isErrorEnabled()) {
                log.error("Error adding comment");
            }
            throw e;
        }
    }


    /**
     * Performs a Post Request in order to create a new reply
     * <p>
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
        if (log.isInfoEnabled()) {
            log.info("Received request to add reply");
        }
        ResponseCommentReplyDTO responseCommentReplyDTO = commentService.addReply(requestCommentReplyDTO);
        if (log.isInfoEnabled()) {
            log.info("Successfully added reply");
        }
        return new ResponseEntity<ResponseCommentReplyDTO>(responseCommentReplyDTO, HttpStatus.OK);
    }


    /**
     * Performs a Get Request in order to get all comments of a certain idea in a paginated form
     * <p>
     * Method takes the paging parametres: pageSize, pageNumber and sortCategory to create a Pageable object
     * The CommentService method "getAllPagedCommentsByIdeaId" gets all the comments that belong to a certain idea in the pageable object format
     * In the end, a ResponseEntity is returned
     *
     * @param ideaId       the unique identifier for the idea we are searching for
     * @param pageSize     how many elements will be displayed on the page
     * @param pageNumber   the page accesed
     * @param sortCategory DESC so that we get the comments from newest to oldest
     * @return ResponseEntity with the resulted paginated list and the HttpStatus
     */
    @Transactional
    @GetMapping("/comments")
    public ResponseEntity<Page<ResponseCommentDTO>> getAllCommentsByIdeaIdWithPaging(
            @RequestParam(required = true) Long ideaId,
            @RequestParam(required = true) int pageSize,
            @RequestParam(required = true) int pageNumber,
            @RequestParam(required = true) String sortCategory) {
        if (log.isInfoEnabled()) {
            log.info("Received request to get comments for idea");
        }

        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, sortCategory));
        Page<ResponseCommentDTO> commentsPage = commentService.getAllPagedCommentsByIdeaId(ideaId, pageable);

        if (log.isInfoEnabled()) {
            log.info("Successfully retrieved comments for idea");
        }
        return new ResponseEntity<>(commentsPage, HttpStatus.OK);
    }


    /**
     * Performs a Get Request in order to get all replies of a certain comment in a paginated form
     * <p>
     * Method takes the paging parametres: pageSize, pageNumber and sortCategory to create a Pageable object
     * The CommentService method "getAllRepliesByCommentId" gets all the replies that belong to a certain comment in the pageable object format
     * In the end, a ResponseEntity is returned
     *
     * @param commentId    the unique identifier for the comment we are searching for
     * @param pageSize     how many elements will be displayed on the page
     * @param pageNumber   the page accesed
     * @param sortCategory DESC so that we get the replies from newest to oldest
     * @return ResponseEntity with the resulted paginated list and the HttpStatus
     */
    @Transactional
    @GetMapping("/comments/replies")
    public ResponseEntity<Page<ResponseCommentReplyDTO>> getAllRepliesByCommentId(@RequestParam(name = "commentId") Long commentId,
                                                                                  @RequestParam(required = true) int pageSize,
                                                                                  @RequestParam(required = true) int pageNumber,
                                                                                  @RequestParam(required = true) String sortCategory) {
        if (log.isInfoEnabled()) {
            log.info("Received request to get all the replies for a comment");
        }

        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, sortCategory));

        Page<ResponseCommentReplyDTO> repliesPage = commentService.getAllRepliesByCommentId(commentId, pageable);
        if (log.isInfoEnabled()) {
            log.info("Successfully retrieved replies for comment");
        }

        return new ResponseEntity<>(repliesPage, HttpStatus.OK);
    }


    /**
     * Performs a Get Request in order to get the time that has passed since a comment was created
     * <p>
     * Method gets the comment id as a RequestParam
     * Then, the id is used in the CommentService method "getTimeForComment" and the result is returned
     *
     * @param id the unique identifier for the comment we are searching for
     * @return time since the comment was created
     */
    @Transactional
    @GetMapping("/test")
    public String getTime(@RequestParam(name = "id") Long id) {
        if (log.isInfoEnabled()) {
            log.info("Received request to get time for comment");
        }

        String elapsedTime = commentService.getTimeForComment(id);

        if (log.isInfoEnabled()) {
            log.info("Successfully retrieved time for comment");
        }
        return elapsedTime;
    }


    /**
     * Performs a Delete Request in order to delete a certain comment
     * <p>
     * Method gets the id of the comment that is going to be deleted as a RequestParam
     * The, the id is used in the CommentService method "deleteComment" and the deleting operation happens
     *
     * @param commentId the unique identifier for the comment we are going to delete
     * @return ResponseEntity with a message indicating the result of the delete operation
     */
    @Transactional
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<String> deleteComment(@PathVariable Long commentId) {
        if (log.isInfoEnabled()) {
            log.info("Received request to delete comment");
        }

        commentService.deleteComment(commentId);
        if (log.isInfoEnabled()) {
            log.info("Successfully deleted comment");
        }
        return new ResponseEntity<>("Comment likes, reports and replies will also be deleted for deleted comment", HttpStatus.OK);
    }


    /**
     * Retrieves the list of users who liked a specific comment.
     *
     * @param commentId the ID of the comment
     * @return ResponseEntity containing the list of UserResponseDTOs who liked the comment
     */
    @GetMapping("/comments/{commentId}/likes")
    public ResponseEntity<List<UserResponseDTO>> getLikesForComment(@PathVariable Long commentId) {
        if (log.isInfoEnabled()) {
            log.info("Received request to get likes for comment");
        }
        List<UserResponseDTO> likes = commentService.getLikesForComment(commentId);
        if (log.isInfoEnabled()) {
            log.info("Successfully retrieved likes for comment");
        }
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
        if (log.isInfoEnabled()) {
            log.info("Received request to get number of likes for the comment");
        }
        int likesCount = commentService.getLikesCountForComment(commentId);
        if (log.isInfoEnabled()) {
            log.info("Successfully retrieved likes count for comment");
        }
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
        if (log.isInfoEnabled()) {
            log.info("Received request to add like to comment");
        }

        commentService.addLike(commentId, userId);
        if (log.isInfoEnabled()) {
            log.info("Successfully added like to comment");
        }

        return new ResponseEntity<>("Like added successfully", HttpStatus.OK);
    }


    /**
     * Deletes a like from a specific comment by a specific user.
     *
     * @param commentId the ID of the comment
     * @param userId    the ID of the user
     * @return ResponseEntity with a message indicating the result of the delete operation
     */
    @DeleteMapping("/comments/like/delete/{commentId}/{userId}")
    public ResponseEntity<String> deleteLikes(@PathVariable Long commentId, @PathVariable Long userId) {
        if (log.isInfoEnabled()) {
            log.info("Received request to delete like from comment");
        }
        commentService.deleteLikes(commentId, userId);
        if (log.isInfoEnabled()) {
            log.info("Successfully deleted like from comment");
        }
        return new ResponseEntity<>("Like successfully deleted", HttpStatus.OK);
    }


    /**
     * Deletes a report from a specific comment by a specific user.
     *
     * @param commentId the ID of the comment
     * @param userId    the ID of the user
     * @return ResponseEntity with a message indicating the result of the delete operation
     */
    @Transactional
    @DeleteMapping("/comments/report/delete/{commentId}/{userId}")
    public ResponseEntity<String> deleteReport(@PathVariable Long commentId, @PathVariable Long userId) {
        if (log.isInfoEnabled()) {
            log.info("Received request to delete report from comment");
        }
        commentService.deleteReport(commentId, userId);
        if (log.isInfoEnabled()) {
            log.info("Successfully deleted report from comment");
        }
        return new ResponseEntity<>("Report successfully deleted", HttpStatus.OK);
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
        if (log.isInfoEnabled()) {
            log.info("Received request to check the existence of like by comment and user");
        }
        boolean exists = commentService.existsLikeByCommentIdAndUserId(commentId, userId);
        if (exists) {
            if (log.isInfoEnabled()) {
                log.info("Like exists by comment id and user id");
            }
        } else {
            if (log.isInfoEnabled()) {
                log.info("Like doesn't exists by comment id and user id");
            }
        }
        if (log.isInfoEnabled()) {
            log.info("Successfully checked the existence of like by comment and user");
        }
        return exists;
    }


    /**
     * Checks if a report exists for a given comment ID and user ID.
     *
     * @param commentId the ID of the comment
     * @param userId    the ID of the user
     * @return {@code true} if a report exists for the given comment ID and user ID, otherwise {@code false}
     */
    @GetMapping("/comments/report/find/{commentId}/{userId}")
    public boolean existsReportByCommentIdAndUserId(@PathVariable Long commentId, @PathVariable Long userId) {
        if (log.isInfoEnabled()) {
            log.info("Received request to check the existence of report by comment and user");
        }
        boolean exists = commentService.existsReportByCommentIdAndUserId(commentId, userId);
        if (exists) {
            if (log.isInfoEnabled()) {
                log.info("Report exists by comment id and user id");
            }
        } else {
            if (log.isInfoEnabled()) {
                log.info("Report doesn't exists by comment id and user id");
            }
        }
        return exists;
    }


    /**
     * Retrieves the count of reports for a specific comment.
     *
     * @param commentId the ID of the comment
     * @return ResponseEntity with the count of reports for the specified comment
     */
    @GetMapping("/comments/reports/count/{commentId}")
    public ResponseEntity<Integer> getReportsCountForComment(@PathVariable Long commentId) {

        if (log.isInfoEnabled()) {
            log.info("Received request to get the number of reports for the comment");
        }
        int reportsCount = commentService.getReportsCountForComment(commentId);
        if (log.isInfoEnabled()) {
            log.info("Successfully retrieved reports count for comment");
        }
        return ResponseEntity.ok(reportsCount);
    }


    /**
     * Retrieves all comments sorted by the number of reports.
     *
     * @param pageSize   the number of comments per page
     * @param pageNumber the page number to retrieve
     * @return ResponseEntity with a CommentPageDTO containing the comments
     */
    @GetMapping("/comments/allByReportsNr")
    public ResponseEntity<CommentPageDTO> getAllCommentsByReportsNr(@RequestParam(required = true) int pageSize,
                                                                    @RequestParam(required = true) int pageNumber) {
        if (log.isInfoEnabled()) {
            log.info("Received request to get retrieve all the comments sorted by reports number");
        }
        CommentPageDTO commentPageDTO = commentService.getAllCommentsByReportsNr(
                PageRequest.of(
                        pageNumber,
                        pageSize,
                        Sort.by(Sort.Direction.DESC, "id")
                )
        );
        if (log.isInfoEnabled()) {
            log.info("Successfully retrieved comments sorted by reports number");
        }
        return new ResponseEntity<>(
                commentPageDTO,
                HttpStatus.OK
        );
    }


    /**
     * Adds a report to a specific comment from a specific user
     * <p>
     * This method handles the HTTP POST request to add a report for a comment by a user
     * It calls the CommentService to perform the actual report addition logic
     *
     * @param commentId the ID of the comment
     * @param userId    the ID of the user
     * @return ResponseEntity containing a success message if the report is added successfully
     */
    @Transactional
    @PostMapping("/comments/report/{commentId}/{userId}")
    public ResponseEntity<String> addReport(@PathVariable Long commentId, @PathVariable Long userId) {

        if (log.isInfoEnabled()) {
            log.info("Received request to add report for comment");
        }
        commentService.addReport(commentId, userId);
        if (log.isInfoEnabled()) {
            log.info("Successfully added report for comment");
        }
        return new ResponseEntity<>("Report added successfully", HttpStatus.OK);
    }


    /**
     * Updates the status of a reported comment. If the comment has more than 5 reports,
     * it is marked as under review by displaying a placeholder.
     *
     * @param commentId the ID of the comment to update
     * @return ResponseEntity with a message indicating the result of the update operation
     */
    @Transactional
    @PatchMapping("/comments/report/patch/{commentId}")
    public ResponseEntity<String> updateReportedComment(@PathVariable Long commentId) {

        if (log.isInfoEnabled()) {
            log.info("Received request to update reported comment");
        }
        if (commentService.getReportsCountForComment(commentId) <= 5) {
            if (log.isInfoEnabled()) {
                log.info("Comment does not have enough reports to be reviewed");
            }
            return new ResponseEntity<>("This comment does not have enough reports to be reviewed!", HttpStatus.OK);
        }

        commentService.deleteReportsByCommentId(commentId);
        commentService.displayPlaceholder(commentId);

        return new ResponseEntity<>("Comment with id " + commentId + " is under review by admin", HttpStatus.OK);
    }


    /**
     * Deletes all reports associated with a specific comment ID.
     *
     * @param commentId the ID of the comment
     * @return a ResponseEntity containing a confirmation message and HTTP status code
     */
    @Transactional
    @DeleteMapping("/comments/reports/delete/{commentId}")
    public ResponseEntity<String> deleteReportsByCommentId(@PathVariable Long commentId) {

        if (log.isInfoEnabled()) {
            log.info("Received request to delete all reports for comment");
        }
        commentService.deleteReportsByCommentId(commentId);
        if (log.isInfoEnabled()) {
            log.info("Successfully deleted all reports for comment");
        }
        return new ResponseEntity<>("Deleted reports for comment with id: " + commentId, HttpStatus.OK);
    }


    /**
     * Sets the review status for a specific comment identified by its ID via a PATCH request.
     * <p>
     * This method is transactional to ensure that the operation is completed successfully or rolled back in case of failure.
     * The endpoint for this method is "/comments/reports/review/set".
     *
     * @param reviewStatus the new review status to be set for the comment, passed as a request parameter
     * @param commentId    the ID of the comment whose review status is to be updated, passed as a request parameter
     * @throws IllegalArgumentException if the commentId or reviewStatus is null
     */
    @Transactional
    @PatchMapping("/comments/reports/review/set")
    public void setReviewStatusByCommentId(@RequestParam(required = true) ReviewStatus reviewStatus, @RequestParam(required = true) Long commentId) {

        if (log.isInfoEnabled()) {
            log.info("Received request to set review status for comment");
        }
        commentService.setReviewStatusByCommentId(reviewStatus, commentId);
        if (log.isInfoEnabled()) {
            log.info("Successfully set review status for comment");
        }
    }


    /**
     * Retrieves the review status for a specific comment identified by its ID via a GET request.
     * <p>
     * The endpoint for this method is "/comments/reports/review/get/{commentId}" where {commentId} is a path variable.
     *
     * @param commentId the ID of the comment whose review status is to be retrieved, passed as a path variable
     * @return a ResponseEntity containing the review status of the specified comment
     * @throws IllegalArgumentException if the commentId is null
     */
    @GetMapping("/comments/reports/review/get/{commentId}")
    public ResponseEntity<ReviewStatus> getReviewStatusByCommentId(@PathVariable Long commentId) {

        if (log.isInfoEnabled()) {
            log.info("Received request to get review status for comment");
        }
        ReviewStatus reviewStatus = commentService.getReviewStatusByCommentId(commentId);
        if (log.isInfoEnabled()) {
            log.info("Successfully retrieved review status for comment");
        }
        return ResponseEntity.ok(reviewStatus);
    }


    /**
     * Retrieves the number of likes from table.
     *
     * @return a ResponseEntity containing the number of likes
     */
    @GetMapping("/likes/count")
    public ResponseEntity<Long> getNumberOfLikes() {

        if (log.isInfoEnabled()) {
            log.info("Received request to get the total number of likes");
        }
        Long number = commentService.countNumberOfLikes();
        if (log.isInfoEnabled()) {
            log.info("Successfully retrieved the total number of likes");
        }
        return new ResponseEntity(number, HttpStatus.OK);
    }


    /**
     * Retrieves the number of reports from table.
     *
     * @return a ResponseEntity containing the number of reports
     */
    @GetMapping("/reports/count")
    public ResponseEntity<Long> getNumberOfReports() {

        if (log.isInfoEnabled()) {
            log.info("Received request to get the total number of reports");
        }
        Long number = commentService.countNumberOfReports();
        if (log.isInfoEnabled()) {
            log.info("Successfully retrieved the total number of reports");
        }
        return new ResponseEntity(number, HttpStatus.OK);
    }

}
