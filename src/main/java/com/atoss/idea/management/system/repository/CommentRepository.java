package com.atoss.idea.management.system.repository;

import com.atoss.idea.management.system.repository.entity.Comment;
import com.atoss.idea.management.system.repository.entity.ReviewStatus;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
//import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    /**
     * Deletes report associated with a specific comment and user.
     *
     * @param commentId the unique identifier of the comment for which reports should be deleted
     * @param userId    the unique identifier of the user associated with the reports to be deleted
     */
    @Transactional
    @Modifying
    @Query(value = "DELETE FROM reports WHERE user_id = :userId AND comment_id = :commentId", nativeQuery = true)
    void deleteReport(Long commentId, Long userId);


    /**
     * Overwriting default "findAllById" CRUD method in order to return a Page of type Comment
     * <p>
     * Method returns a Page of comments that belong to a certain idea
     *
     * @param ideaId   the unique identifier for the idea we are working with
     * @param pageable the Pageable object containing pagination information
     * @return Page of type Comment with the comments of the desired idea
     */
    Page<Comment> findAllByIdeaId(Long ideaId, Pageable pageable);

    /**
     * Overwriting default "findAllById" CRUD method in order to return a Page of type Comment
     * <p>
     * Method returns a Page of replies that belong to a certain comment
     *
     * @param parentId the unique identifier for the comment we are working with
     * @param pageable the Pageable object containing pagination information
     * @return Page of type Comment with the replies of the comment
     */
    Page<Comment> findAllByParentId(Long parentId, Pageable pageable);

    /**
     * Counts in the Database the number of comments
     *
     * @return the number of comments
     */


    @Query(value = "SELECT COUNT(parent_id) FROM comment c", nativeQuery = true)
    Long countReplies();

    /**
     * Counts in the Database the number of replies
     *
     * @return the number of replies
     */
    @Query(value = "SELECT COUNT(*) FROM comment WHERE parent_id IS NULL", nativeQuery = true)
    Long countComments();


    /**
     * gets the most commented ideas id's
     *
     * @param selectedDateFrom date from which we select
     * @param selectedDateTo   data up to selection
     * @return a list containing idea-id's of the most commented ideas between given dates
     */
    @Query(value = " select comment.idea_id, count(*) as noOfComments from comment "
            +
            " where comment.idea_id in "
            +
            " (select idea.idea_id from idea "
            +
            " where (idea.date between cast(:selectedDateFrom AS timestamp) "
            +
            " and cast(:selectedDateTo AS timestamp)) "
            +
            " and idea.status != 1) "
            +
            " group by comment.idea_id order by noOfComments DESC limit 5", nativeQuery = true)
    List<Long> mostCommentedIdeasIdsByDate(@Param("selectedDateFrom") String selectedDateFrom,
                                           @Param("selectedDateTo") String selectedDateTo);

    /**
     * gets the most commented ideas id's since the beginning of the app
     *
     * @return a list containing idea-id's of the most commented ideas
     */
    @Query(value = "SELECT idea_id, COUNT(*) AS frequency "
            +
            " FROM comment "
            +
            " WHERE idea_id IS NOT NULL "
            +
            " AND idea_id IN "
            +
            " (SELECT idea_id FROM idea WHERE status != 1) "
            +
            " GROUP BY idea_id "
            +
            " ORDER BY frequency DESC "
            +
            " LIMIT 5", nativeQuery = true)
    List<Long> mostCommentedIdeas();


    /**
     * gets the most replied comments up to a limit , not used atm
     * @return List of objects
     *
     */
    @Query(value = "SELECT parent_id, COUNT(*) AS frequency FROM comment c WHERE parent_id IS NOT NULL GROUP BY"
            +
            " parent_id ORDER BY frequency DESC LIMIT 5 ", nativeQuery = true)
    List<Object[]> mostRepliedComments();


    /**
     * Function to get number of replies and comments created in a specific time interval
     *
     * @param selectedDateFrom date from which we select
     * @param selectedDateTo   data up to selection
     * @return An array with 2 elements , on index 0 we have Replies and on index 1 Comments
     */
    @Query(value = " select count(*) from comment c "
            +
            " where (c.creation_date  between cast(:selectedDateFrom AS timestamp) and cast(:selectedDateTo AS timestamp)) "
            +
            " and c.idea_id is not null and c.idea_id in (select idea_id from idea "
            +
            " where status != 1 and (idea.date between cast(:selectedDateFrom AS timestamp) and cast(:selectedDateTo AS timestamp)) ) union all "
            +
            " select count(*) from comment c where (c.creation_date between cast(:selectedDateFrom AS timestamp) "
            +
            " and cast(:selectedDateTo AS timestamp)) "
            +
            " and c.idea_id is null and c.idea_id in (select idea_id from idea "
            +
            " where status != 1 and (idea.date  between cast(:selectedDateFrom AS timestamp) "
            +
            " and cast(:selectedDateTo AS timestamp)))", nativeQuery = true)
    List<Long> getRepliesAndCommentsCount(@Param("selectedDateFrom") String selectedDateFrom,
                                          @Param("selectedDateTo") String selectedDateTo);


    /**
     * Deletes a like from the database for the given user and comment.
     *
     * @param commentId the ID of the comment from which the like is to be deleted.
     * @param userId    the ID of the user who liked the comment.
     */

    @Modifying
    @Query(value = "DELETE FROM likes WHERE user_id = :userId AND comment_id = :commentId", nativeQuery = true)
    void deleteLikes(@Param("commentId") Long commentId, @Param("userId") Long userId);


    /**
     * Counts the number of likes for a given comment.
     *
     * @param commentId the ID of the comment for which likes are to be counted.
     * @return the number of likes for the comment.
     */
    @Query("SELECT COUNT(l) FROM User u JOIN u.likedComments l WHERE l.id = :commentId")
    int countLikesByCommentId(@Param("commentId") Long commentId);

    /**
     * Counts the number of reports.
     *
     * @return the number of reports.
     */
    @Query(value = "SELECT COUNT(*) FROM reports", nativeQuery = true)
    Long countAllReports();


    /**
     * Counts the number of likes.
     *
     * @return the number of likes.
     */
    @Query(value = "SELECT COUNT(*) FROM likes", nativeQuery = true)
    Long countAllLikes();


    /**
     * Checks if a like exists for a given user and comment.
     *
     * @param commentId the ID of the comment.
     * @param userId    the ID of the user.
     * @return true if the user has liked the comment, false otherwise.
     */
    @Query("SELECT CASE WHEN COUNT(l) > 0 THEN true ELSE false END FROM User u JOIN u.likedComments l WHERE u.id = :userId AND l.id = :commentId")
    boolean existsLikeByCommentIdAndUserId(@Param("commentId") Long commentId, @Param("userId") Long userId);

    /**
     * Checks if a report exists for a given comment ID and user ID.
     *
     * @param commentId the ID of the comment
     * @param userId    the ID of the user
     * @return {@code true} if a report exists for the given comment ID and user ID, otherwise {@code false}
     */
    @Query("SELECT CASE WHEN COUNT(l) > 0 THEN true ELSE false END FROM User u JOIN u.reportedComments l WHERE u.id = :userId AND l.id = :commentId")
    boolean existsReportByCommentIdAndUserId(@Param("commentId") Long commentId, @Param("userId") Long userId);

    /**
     * Counts the number of reports for a specific comment.
     *
     * @param commentId the ID of the comment
     * @return the count of reports for the specified comment
     */
    @Query("SELECT COUNT(l) FROM User u JOIN u.reportedComments l WHERE l.id = :commentId")
    int countReportsByCommentId(@Param("commentId") Long commentId);


    /**
     * Deletes all replies for a given comment ID.
     *
     * @param commentId the ID of the comment whose replies are to be deleted
     */
    @Modifying
    @Query(value = "DELETE FROM comment WHERE parent_id = :commentId", nativeQuery = true)
    void deleteRepliesForComment(Long commentId);

    /**
     * Retrieves the IDs of comments that have been reported by five or more users.
     *
     * @return a list of comment IDs that have five or more reports
     */
    @Query("SELECT c.id FROM Comment c JOIN c.listOfUsers u GROUP BY c.id HAVING COUNT(u) > 5")
    List<Long> moreThenFiveReports();

    /**
     * Deletes likes associated with a specific comment.
     *
     * @param commentId the ID of the comment
     */
    @Modifying
    @Query(value = "DELETE FROM likes where comment_id = :commentId", nativeQuery = true)
    void deleteLikesForComment(Long commentId);

    /**
     * Deletes reports associated with a specific comment.
     *
     * @param commentId the ID of the comment
     */
    @Modifying
    @Query(value = "DELETE FROM reports where comment_id = :commentId", nativeQuery = true)
    void deleteReportsByCommentId(Long commentId);

    /**
     * Finds the top 5 comments with the most likes.
     *
     * @return a list of the top 5 most liked comments.
     */
    @Query(value = "SELECT c.* FROM comment c "
            +
            "JOIN likes l ON c.comment_id = l.comment_id "
            +
            "GROUP BY c.comment_id "
            +
            "ORDER BY COUNT(l.user_id) DESC "
            +
            "LIMIT 5", nativeQuery = true)
    List<Comment> findTop5CommentsByLikes();

    /**
     * Finds all replies for a given comment ID.
     *
     * @param commentId the ID of the comment whose replies are to be retrieved
     * @return a list of replies for the specified comment
     */
    @Query(value = "SELECT * from comment WHERE parent_id = :commentId", nativeQuery = true)
    List<Comment> findAllRepliesForComment(Long commentId);

    /**
     * Retrieves the review status of a comment by its ID.
     *
     * @param commentId the ID of the comment
     * @return the review status of the comment
     */
    @Query(value = "SELECT review_status FROM comment  WHERE comment_id = :commentId", nativeQuery = true)
    ReviewStatus getReviewStatusByCommentId(Long commentId);

}
