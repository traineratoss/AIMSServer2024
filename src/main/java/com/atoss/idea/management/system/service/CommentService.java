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
    void addLike(Long commentId, Long userId);
    String getTimeForComment(Long id);

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
    void deleteLikes(Long commentId, Long userId);


    List<UserResponseDTO> getLikesForComment(Long commentId);
    int getLikesCountForComment(Long commentId);

    boolean existsByCommentIdAndUserId(Long commentId, Long userId);

}
