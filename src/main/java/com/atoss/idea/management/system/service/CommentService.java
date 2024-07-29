package com.atoss.idea.management.system.service;

import com.atoss.idea.management.system.exception.CommentNotFoundException;
import com.atoss.idea.management.system.exception.IdeaNotFoundException;
import com.atoss.idea.management.system.exception.UserNotFoundException;
import com.atoss.idea.management.system.repository.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;

public interface CommentService {

    /**
     * Calculates the difference of time between creating a comment/reply and the moment the method is called
     *
     * @param id the unique identifier of the comment or reply
     * @return time the time difference in a human-readable format
     * @throws CommentNotFoundException when the id is not found in the database
     */

    String getTimeForComment(Long id);

    /**
     * Adds a like to a specific comment from a given user.
     * @param commentId the ID of the comment to which the like is being added
     * @param userId the ID of the user who is liking the comment
     * @throws CommentNotFoundException if the comment with the specified ID does not exist
     * @throws UserNotFoundException if the user with the specified ID does not exist, or if the user is the owner of the comment
     *                               or if the user has already liked the comment
     */

    void addLike(Long commentId, Long userId);

    /**
     * Transforms time-units (milliseconds) into the biggest kind of time-unit (flooring applied)
     *
     * @param creationDate the date of comment/reply creation
     * @return timeUnits the formatted elapsed time in the largest time unit (e.g., days, hours)
     */
    String getElapsedTime(Date creationDate);

    /**
     * Takes a RequestCommentDTO and formats it to fit a Comment's entity structure
     * The new object is added to the database using CRUD methods
     * The result is yet again formatted to fit a ResponseCommentDTO structure and returned
     *
     * @param newComment the RequestCommentDTO object representing the new comment to be added
     * @return responseCommentDTO the formatted ResponseCommentDTO object representing the newly added comment
     * @throws UserNotFoundException when the username is not found in the database
     * @throws IdeaNotFoundException when the id for an idea is not found in the database
     */
    ResponseCommentDTO addComment(RequestCommentDTO newComment) throws UnsupportedEncodingException;

    /**
     * Takes a RequestCommentReplyDTO and formats it to fit a Comment's entity structure
     * The new object is added to the database using CRUD methods
     * The result is yet again formatted to fit a ResponseCommentReplyDTO structure and returned
     *
     * @param requestCommentReplyDTO the RequestCommentReplyDTO object representing the new reply to be added
     * @return responseCommentReplyDTO the formatted ResponseCommentReplyDTO object representing the newly added reply
     * @throws UserNotFoundException when the username is not found in the database
     * @throws CommentNotFoundException when the comment id is not found in the database
     */
    ResponseCommentReplyDTO addReply(RequestCommentReplyDTO requestCommentReplyDTO);

    /**
     * Reads the comments that belong to a certain idea (ideaId)
     * The data gets loaded from the database and stored inside a List
     * The List is traversed using a stream() line and formatted so that
     * every element (Comment Entity) fits the new format (ResponseCommentDTO)
     * The result is re-formatted to fit the Paging parameters (pageable) and then returned.
     *
     * @param ideaId the unique identifier of the idea
     * @param pageable the Pageable object containing pagination information
     * @return pagedComments a Page object containing the formatted comments belonging to the idea
     * @throws IdeaNotFoundException when the id for an idea is not found in the database
     */
    Page<ResponseCommentDTO> getAllPagedCommentsByIdeaId(Long ideaId, Pageable pageable);

    /**
     * Reads the replies that belong to a certain comment (commentId)
     * The data gets loaded from the database and stored inside a List
     * The List is traversed using a stream() line and formatted so that
     * every element (Comment Entity) fits the new format (ResponseCommentReplyDTO)
     * The result is re-formatted to fit the Paging parameters (pageable) and then returned.
     *
     * @param commentId the unique identifier of the comment
     * @param pageable the Pageable object containing pagination information
     * @return pagedReplies a Page object containing the formatted replies belonging to the comment
     * @throws CommentNotFoundException when the comment id is not found in the database
     */
    Page<ResponseCommentReplyDTO> getAllRepliesByCommentId(Long commentId, Pageable pageable);

    /**
     * Every comment has its unique id
     * The function deletes a comment using CRUD methods, by searching for its id
     *
     * @param commentId the unique identifier of the comment to be deleted
     * @throws CommentNotFoundException when the comment id is not found in the database
     */
    void deleteComment(Long commentId);

    /**
     * Displays a placeholder for a comment under review.
     *
     * @param commentId the ID of the comment to display the placeholder for
     */
    void displayPlaceholder(Long commentId);

    /**
     * Deletes reports associated with a specific comment and user.
     * @param commentId the unique identifier of the comment whose reports are to be deleted
     * @param userId the unique identifier of the user associated with the reports to be deleted
     * @throws CommentNotFoundException if no comment is found with the given {@code commentId}
     * @throws UserNotFoundException if no user is found with the given {@code userId}
     */
    void deleteReport(Long commentId, Long userId);

    /**
     * Deletes a like from a specific comment by a given user.
     *
     * @param commentId the ID of the comment from which the like is to be deleted
     * @param userId the ID of the user whose like is to be removed
     * @throws CommentNotFoundException if the comment with the specified ID does not exist
     * @throws UserNotFoundException if the user with the specified ID does not exist
     */
    void deleteLikes(Long commentId, Long userId);

    /**
     * Retrieves a list of users who have liked a specific comment.
     * @param commentId the ID of the comment for which to retrieve the list of users who liked it
     * @return a list of {@link UserResponseDTO} representing users who liked the comment
     * @throws CommentNotFoundException if the comment with the specified ID does not exist
     */
    List<UserResponseDTO> getLikesForComment(Long commentId);

    /**
     * Retrieves the count of likes for a specific comment.
     * @param commentId the ID of the comment for which to retrieve the like count
     * @return the count of likes for the comment
     * @throws CommentNotFoundException if the comment with the specified ID does not exist
     */
    int getLikesCountForComment(Long commentId);

    /**
     * Checks if a specific user has liked a specific comment.
     * @param commentId the ID of the comment to check
     * @param userId the ID of the user to check
     * @return {@code true} if the user has liked the comment, {@code false} otherwise
     */
    boolean existsLikeByCommentIdAndUserId(Long commentId, Long userId);

    /**
     * Retrieves the count of reports for a specific comment.
     *
     * @param commentId the ID of the comment
     * @return the count of reports for the specified comment
     */
    int getReportsCountForComment(Long commentId);

    /**
     * Retrieves all comments sorted by the number of reports.
     *
     * @param pageable the pagination and sorting information
     * @return a CommentPageDTO containing the comments sorted by the number of reports
     */
    CommentPageDTO getAllCommentsByReportsNr(Pageable pageable);

    /**
     * Adds a report from a user to a comment.
     *
     * This method performs the following actions:
     * - Verifies that the comment exists and throws an exception if not found.
     * - Verifies that the user exists and throws an exception if not found.
     * - Checks if the user is the owner of the comment and prevents the report if true.
     * - Checks if the user has already reported the comment and prevents multiple reports.
     * - If both the comment and user exist, and the user is not the owner of the comment, and the user hasn't already reported the comment,
     *   adds the report to both the user and the comment, and saves the changes to the database.
     *
     * @param commentId the ID of the comment to be reported
     * @param userId the ID of the user who is reporting the comment
     * @throws UserNotFoundException if the user with the specified ID does not exist
     * @throws CommentNotFoundException if the comment with the specified ID does not exist
     * @throws IllegalArgumentException if the user tries to report their own comment or if the user has already reported the comment
     */
    void addReport(Long commentId, Long userId);

    /**
     * Deletes likes associated with a deleted comment.
     *
     * @param commentId the ID of the comment whose likes are to be deleted
     */
    void deleteLikesForDeletedComment(Long commentId);

    /**
     * Deletes reports associated with a deleted comment.
     *
     * @param commentId the ID of the comment whose reports are to be deleted
     */
    void deleteReportsForDeletedComment(Long commentId);
}