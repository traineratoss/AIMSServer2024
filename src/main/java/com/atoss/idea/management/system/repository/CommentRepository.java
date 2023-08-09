package com.atoss.idea.management.system.repository;

import com.atoss.idea.management.system.repository.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    /**
     * Overwriting default "findAllById" CRUD method in order to return a Page of type Comment
     *
     * Method returns a Page of comments that belong to a certain idea
     *
     * @param ideaId the unique identifier for the idea we are working with
     * @param pageable the Pageable object containing pagination information
     * @return Page of type Comment with the comments of the desired idea
     */
    Page<Comment> findAllByIdeaId(Long ideaId, Pageable pageable);

    /**
     * Overwriting default "findAllById" CRUD method in order to return a Page of type Comment
     *
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
    Long countAllReplies();

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
     * @param selectedDateTo data up to selection
     * @return a list containing idea-id's of the most commented ideas between given dates
     */
    //    @Query(value = " SELECT idea_id , COUNT(*) AS frequency "
    //            +
    //            " FROM comment c"
    //            +
    //            " WHERE (c.creation_date between cast(:selectedDateFrom AS timestamp) and cast(:selectedDateTo AS timestamp) )"
    //            +
    //            " and (idea_id IS NOT NULL) "
    //            +
    //            " GROUP BY idea_id "
    //            +
    //            " ORDER BY frequency DESC "
    //            +
    //            " LIMIT 5 ", nativeQuery = true)

    //    @Query(value =
    //            "SELECT idea_id FROM idea i "
    //            +
    //            "WHERE i.idea_id IN ("
    //            +
    //            "    SELECT c.idea_id"
    //            +
    //            "    FROM comment c "
    //            +
    //            " WHERE (c.creation_date between cast(:selectedDateFrom AS timestamp) and cast(:selectedDateTo AS timestamp) )"
    //           +
    //            " and (idea_id IS NOT NULL) "
    //            +
    //            "    GROUP BY c.idea_id "
    //            +
    //            "    ORDER BY COUNT(*) DESC "
    //            +
    //            "    LIMIT 5 "
    //            +
    //            ")", nativeQuery = true)

    @Query(value = " select c.idea_id, COUNT(*) AS total_comments "
            +
            " from comment c "
            +
            " WHERE (c.creation_date between cast(:selectedDateFrom AS timestamp) and cast(:selectedDateTo AS timestamp) ) "
            +
            " and (idea_id IS NOT NULL) "
            +
            " GROUP BY c.idea_id "
            +
            " order by total_comments DESC "
            +
            " limit 5 ", nativeQuery = true)
    List<Long> mostCommentedIdeasByDate(@Param("selectedDateFrom") String selectedDateFrom,
                                                 @Param("selectedDateTo") String selectedDateTo);

    /**
     * gets the most commented ideas id's
     *
     * @return a list containing idea-id's of the most commented ideas
     */
    @Query(value = "SELECT idea_id, COUNT(*) AS frequency "
            +
            " FROM comment "
            +
            " WHERE idea_id IS NOT NULL "
            +
            " GROUP BY idea_id "
            +
            " ORDER BY frequency DESC "
            +
            " LIMIT 5", nativeQuery = true)
    List<Long> mostCommentedIdeas();



    /**
     * gets the most replied comments up to a limit , not used atm
     *
     * @return List of objects that contains on [0] the comment_id and on [1]
     *         the frequency  as the number of replies per comment
     */
    @Query(value = "SELECT parent_id, COUNT(*) AS frequency FROM comment c WHERE parent_id IS NOT NULL GROUP BY"
            +
            " parent_id ORDER BY frequency DESC LIMIT 5 ", nativeQuery = true)
    List<Object[]> mostRepliedComments();


    /**
     * =======
     *
     * @param selectedDateFrom ======
     * @param selectedDateTo =====
     * @return ===
     */
    @Query(value = " SELECT "
            +
            "  COUNT(*) AS frequency "
            +
            "FROM "
            +
            "  comment c "
            +
            " WHERE "
            +
            "  (c.creation_date between cast(:selectedDateFrom AS timestamp) and cast(:selectedDateTo AS timestamp) ) "
            +
            "  AND idea_id IS NULL "
            +
            "UNION "
            +
            "SELECT "
            +
            "  COUNT(*) AS frequency "
            +
            "FROM "
            +
            "  comment  c "
            +
            "WHERE "
            +
            "  (c.creation_date between cast(:selectedDateFrom AS timestamp) and cast(:selectedDateTo AS timestamp) ) "
            +
            "  AND idea_id IS NOT NULL ", nativeQuery = true)
    List<Long> getRepliesAndCommentsCount(@Param("selectedDateFrom") String selectedDateFrom,
                                       @Param("selectedDateTo") String selectedDateTo);


}
