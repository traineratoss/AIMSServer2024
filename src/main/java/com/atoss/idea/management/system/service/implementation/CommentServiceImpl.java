package com.atoss.idea.management.system.service.implementation;

import com.atoss.idea.management.system.exception.IdeaNotFoundException;
import com.atoss.idea.management.system.exception.UserNotFoundException;
import com.atoss.idea.management.system.exception.CommentNotFoundException;
import com.atoss.idea.management.system.repository.CommentRepository;
import com.atoss.idea.management.system.repository.IdeaRepository;
import com.atoss.idea.management.system.repository.SubscriptionRepository;
import com.atoss.idea.management.system.repository.UserRepository;
import com.atoss.idea.management.system.repository.dto.*;
import com.atoss.idea.management.system.repository.entity.Comment;
import com.atoss.idea.management.system.repository.entity.Idea;
import com.atoss.idea.management.system.repository.entity.ReviewStatus;
import com.atoss.idea.management.system.repository.entity.User;
import com.atoss.idea.management.system.service.CommentService;
import jakarta.transaction.Transactional;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Log4j2
@Service
public class CommentServiceImpl implements CommentService {
    ClassLoader classLoader = getClass().getClassLoader();
    private List<String> badWords = new ArrayList<>();
    private final CommentRepository commentRepository;
    private final IdeaRepository ideaRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final HtmlServiceImpl htmlService;

    private final SendEmailServiceImpl sendEmailService;

    private final SubscriptionRepository subscriptionRepository;

    /**
     * CONSTRUCTOR
     *
     * @param commentRepository      for accessing CRUD repository methods for Comment Entity
     * @param ideaRepository         for accessing CRUD repository methods for Idea Entity
     * @param userRepository         for accessing CRUD repository methods for User Entity
     * @param modelMapper            for mapping entity-dto relationships
     * @param htmlService            for handling HTML content and processing
     * @param sendEmailService       for handling email content and processing
     * @param subscriptionRepository for accessing CRUD repository methods for Subscription Entity
     */
    public CommentServiceImpl(CommentRepository commentRepository, IdeaRepository ideaRepository,
                              UserRepository userRepository, ModelMapper modelMapper, HtmlServiceImpl htmlService,
                              SendEmailServiceImpl sendEmailService, SubscriptionRepository subscriptionRepository) {
        this.commentRepository = commentRepository;
        this.ideaRepository = ideaRepository;
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.htmlService = htmlService;
        this.sendEmailService = sendEmailService;
        this.subscriptionRepository = subscriptionRepository;
    }

    /**
     * Retrieves the elapsed time since the creation of a comment in a human-readable format.
     *
     * @param id the ID of the comment
     * @return a string representing the elapsed time since the creation of the comment, formatted in a human-readable way
     * @throws CommentNotFoundException if the comment with the specified ID is not found
     */

    @Override
    public String getTimeForComment(Long id) {
        if (log.isDebugEnabled()) {
            log.debug("Retrieving time for comment with id: {}", id);
        }
        Optional<Comment> commentOptional = commentRepository.findById(id);

        if (commentOptional.isPresent()) {
            Comment comment = commentOptional.get();
            Date creationDate = comment.getCreationDate();
            if (log.isInfoEnabled()) {
                log.info("Successfully retrieved time for comment: {}", creationDate);
            }
            return getElapsedTime(creationDate);
        } else {
            if (log.isErrorEnabled()) {
                log.error("Comment not found with id: {}", id);
            }
            throw new CommentNotFoundException();
        }
    }

    /**
     * Calculates the elapsed time from the given creation date to the current date, and returns it in a human-readable format.
     * The time units used are seconds, minutes, hours, days, months, and years.
     *
     * @param creationDate the date when the comment was created
     * @return a string representing the elapsed time from the creation date to the current date
     */
    @Override
    public String getElapsedTime(Date creationDate) {
        Date currentDate = new Date();

        long milliSeconds = currentDate.getTime() - creationDate.getTime();

        long seconds = TimeUnit.MILLISECONDS.toSeconds(milliSeconds);
        if (seconds < 60 && seconds != 1) {
            return seconds + " seconds";
        } else if (seconds == 1) {
            return seconds + " second";
        }

        long minutes = TimeUnit.MILLISECONDS.toMinutes(milliSeconds);
        if (minutes < 60 && minutes != 1) {
            return minutes + " minutes";
        } else if (minutes == 1) {
            return minutes + " minute";
        }

        long hours = TimeUnit.MILLISECONDS.toHours(milliSeconds);
        if (hours < 24 && hours != 1) {
            return hours + " hours";
        } else if (hours == 1) {
            return hours + " hour";
        }

        long days = TimeUnit.MILLISECONDS.toDays(milliSeconds);
        if (days < 30 && days != 1) {
            return days + " days";
        } else if (days == 1) {
            return days + " day";
        }

        Calendar calForCreationDate = Calendar.getInstance();
        calForCreationDate.setTime(creationDate);

        Calendar calForCurrentDate = Calendar.getInstance();
        calForCurrentDate.setTime(currentDate);

        int years = calForCurrentDate.get(Calendar.YEAR) - calForCreationDate.get(Calendar.YEAR);
        int months = calForCurrentDate.get(java.util.Calendar.MONTH) - calForCreationDate.get(Calendar.MONTH);

        int elapsedMonths = years * 12 + months;

        if (elapsedMonths < 12 && elapsedMonths != 1) {
            return elapsedMonths + " months";
        } else if (elapsedMonths == 1) {
            return elapsedMonths + " month";
        }

        if (years > 1) {
            return years + " years";
        } else {
            return years + " year";
        }
    }

    /**
     * Replaces bad word from a String. A bad word is any word found in a certain file stored in the project.
     *
     * @param text the string we want to format
     * @return filteredText the updated text
     */
    private String filterBadWords(String text) {
        if (log.isInfoEnabled()) {
            log.info("Filtering bad words from text");
        }
        for (String word : badWords) {
            String pattern = "\\b" + word + "\\b";
            text = text.replaceAll("(?i)" + pattern, "*".repeat(word.length()));
        }
        if (log.isInfoEnabled()) {
            log.info("Filtered text");
        }
        return text;
    }

    /**
     * Opens and reads String values containing bad words from a certain file path
     *
     * @param path the path to the file
     * @throws IOException when the path for the input file is not found
     */
    private void readBadWordsFromFile(String path) {
        if (log.isInfoEnabled()) {
            log.info("Reading bad words from file");
        }
        try {
            FileReader fileReader = new FileReader(path);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line;

            while ((line = bufferedReader.readLine()) != null) {
                String word = line.trim();

                badWords.add(word);
            }
            if (log.isInfoEnabled()) {
                log.info("Successfully read bad words from file");
            }
            bufferedReader.close();
        } catch (IOException e) {
            if (log.isErrorEnabled()) {
                log.error("Error reading bad words from file ERROR: {}", e.getMessage());
            }
        }
    }

    /**
     * Adds a new comment to an idea.
     *
     * @param requestCommentDTO the DTO containing information about the comment to be added
     * @return a {@link ResponseCommentDTO} representing the added comment
     * @throws UnsupportedEncodingException if an error occurs while decoding the bad words file path
     * @throws UserNotFoundException        if the user specified in the DTO does not exist
     * @throws IdeaNotFoundException        if the idea specified in the DTO does not exist
     */
    @Transactional
    @Override
    public ResponseCommentDTO addComment(RequestCommentDTO requestCommentDTO) throws UnsupportedEncodingException {
        if (log.isInfoEnabled()) {
            log.info("Adding a new comment from user: {}", requestCommentDTO.getUsername());
        }
        User user = userRepository.findByUsername(requestCommentDTO.getUsername()).orElseThrow(() -> {
            if (log.isErrorEnabled()) {
                log.error("User not found: {}", requestCommentDTO.getUsername());
            }
            return new UserNotFoundException("User not found!");
        });
        Idea idea = ideaRepository.findById(requestCommentDTO.getIdeaId()).orElseThrow(() -> {
            if (log.isErrorEnabled()) {
                log.error("Idea not found with id: {}", requestCommentDTO.getIdeaId());
            }
            return new IdeaNotFoundException("Idea not found!");
        });

        Comment newComment = new Comment();
        String wordsFilePath = "textTerms/badWords.txt";
        URL resourceUrl = classLoader.getResource(wordsFilePath);

        if (resourceUrl != null) {
            String filePath = URLDecoder.decode(resourceUrl.getFile(), "UTF-8");
            readBadWordsFromFile(filePath);
        } else {
            if (log.isWarnEnabled()) {
                log.warn("Bad words file not found");
            }
        }

        java.util.Date creationDate = new java.util.Date();

        newComment.setUser(user);
        newComment.setIdea(idea);
        newComment.setParent(null);
        String htmlContent = htmlService.markdownToHtml(requestCommentDTO.getCommentText());
        newComment.setCommentText(htmlContent);
        newComment.setCreationDate(creationDate);

        String filteredCommentText = filterBadWords(newComment.getCommentText());
        newComment.setCommentText(filteredCommentText);

        if (log.isDebugEnabled()) {
            log.debug("Saving comment to repository with content: {}", newComment.getCommentText());
        }
        commentRepository.save(newComment);

        ResponseCommentDTO responseCommentDTO = modelMapper.map(newComment, ResponseCommentDTO.class);
        responseCommentDTO.setUsername(user.getUsername());


        List<Long> subscribedUsersIds = subscriptionRepository.findUserIdByIdeaId(requestCommentDTO.getIdeaId());

        List<User> subscribedUsers = new ArrayList<>();


        for (Long userId : subscribedUsersIds) {
            subscribedUsers.add(userRepository.findById(userId).get());
        }

        if (!subscribedUsers.isEmpty()) {
            if (log.isInfoEnabled()) {
                log.info("Sending email to the subscribed users");
            }
            sendEmailService.sendEmailAddedComment(subscribedUsers, newComment.getId(), requestCommentDTO.getCommentText());
        }
        return responseCommentDTO;
    }

    /**
     * Adds a reply to an existing comment.
     *
     * @param requestCommentReplyDTO the DTO containing information about the reply to be added
     * @return a {@link ResponseCommentReplyDTO} representing the added reply
     * @throws UserNotFoundException    if the user specified in the DTO does not exist
     * @throws CommentNotFoundException if the parent comment specified in the DTO does not exist
     */
    @Transactional
    @Override
    public ResponseCommentReplyDTO addReply(RequestCommentReplyDTO requestCommentReplyDTO) {
        if (log.isInfoEnabled()) {
            log.info("Adding a new reply from user: {}", requestCommentReplyDTO.getUsername());
        }

        User user = userRepository.findByUsername(requestCommentReplyDTO.getUsername())
                .orElseThrow(() -> {
                    if (log.isErrorEnabled()) {
                        log.error("User not found: {}", requestCommentReplyDTO.getUsername());
                    }
                    return new UserNotFoundException("User not found!");
                });

        if (!commentRepository.existsById(requestCommentReplyDTO.getParentId())) {
            if (log.isErrorEnabled()) {
                log.error("Parent comment not found with id: {}", requestCommentReplyDTO.getParentId());
            }
            throw new CommentNotFoundException();
        }

        java.util.Date creationDate = new java.util.Date();

        Comment newReply = new Comment();

        newReply.setUser(user);
        newReply.setIdea(null);
        newReply.setParent(commentRepository.findById(requestCommentReplyDTO.getParentId()).get());
        String htmlContent = htmlService.markdownToHtml(requestCommentReplyDTO.getCommentText());
        newReply.setCommentText(htmlContent);
        newReply.setCreationDate(creationDate);
        String filteredCommentText = filterBadWords(newReply.getCommentText());
        newReply.setCommentText(filteredCommentText);

        if (log.isDebugEnabled()) {
            log.debug("Saving reply to repository with content: {}", newReply.getCommentText());
        }
        commentRepository.save(newReply);

        ResponseCommentReplyDTO responseCommentReplyDTO = modelMapper.map(newReply, ResponseCommentReplyDTO.class);
        responseCommentReplyDTO.setUsername(user.getUsername());

        return responseCommentReplyDTO;
    }

    /**
     * Retrieves all replies for a given comment, paginated.
     *
     * @param commentId the ID of the comment for which replies are to be fetched
     * @param pageable  the pagination information
     * @return a {@link Page} of {@link ResponseCommentReplyDTO} containing the replies to the specified comment
     * @throws CommentNotFoundException if the comment with the specified ID does not exist
     */
    @Transactional
    @Override
    public Page<ResponseCommentReplyDTO> getAllRepliesByCommentId(Long commentId, Pageable pageable) {
        if (log.isInfoEnabled()) {
            log.info("Retrieving all replies for comment");
        }
        if (log.isDebugEnabled()) {
            log.debug("Retrieving all replies for comment with id: {}", commentId);
        }

        if (!commentRepository.existsById(commentId)) {
            if (log.isErrorEnabled()) {
                log.error("Comment not found with id: {}", commentId);
            }
            throw new CommentNotFoundException();
        }

        List<ResponseCommentReplyDTO> replyList = commentRepository.findAllByParentId(commentId, pageable)
                .getContent()
                .stream()
                .map(pagedReply -> {
                    String username = pagedReply.getUser().getUsername();

                    ResponseCommentReplyDTO responseCommentReplyDTO = new ResponseCommentReplyDTO();
                    responseCommentReplyDTO.setId(pagedReply.getId());
                    responseCommentReplyDTO.setUsername(username);


                    Long id = pagedReply.getId();
                    if (getReportsCountForComment(id) > 5) {
                        responseCommentReplyDTO.setCommentText("This reply is under review");
                        if (log.isInfoEnabled()) {
                            log.info("Reply is under review", id);
                        }
                    } else {
                        String htmlContent = htmlService.markdownToHtml(pagedReply.getCommentText());
                        responseCommentReplyDTO.setCommentText(htmlContent);
                    }

                    String time = getTimeForComment(pagedReply.getId());

                    responseCommentReplyDTO.setElapsedTime(time);
                    responseCommentReplyDTO.setParentId(commentId);
                    return responseCommentReplyDTO;
                }).toList();

        if (log.isInfoEnabled()) {
            log.info("Successfully retrieved all the replies for the comment");
        }
        return new PageImpl<>(replyList, pageable, replyList.size());
    }

    /**
     * Retrieves a paginated list of comments for a given idea.
     *
     * @param ideaId   the ID of the idea for which comments are to be fetched
     * @param pageable the pagination information to control the size and number of pages
     * @return a {@link Page} of {@link ResponseCommentDTO} containing comments associated with the specified idea
     * @throws IdeaNotFoundException if the idea with the specified ID does not exist
     */
    @Override
    public Page<ResponseCommentDTO> getAllPagedCommentsByIdeaId(Long ideaId, Pageable pageable) {
        if (log.isInfoEnabled()) {
            log.info("Retrieving all paged comments for idea");
        }
        if (log.isDebugEnabled()) {
            log.debug("Retrieving all paged comments for idea with id: {}", ideaId);
        }

        if (!ideaRepository.existsById(ideaId)) {
            if (log.isErrorEnabled()) {
                log.error("Idea not found with id: {}", ideaId);
            }
            throw new IdeaNotFoundException();
        }

        List<ResponseCommentDTO> commentList = commentRepository.findAllByIdeaId(ideaId, pageable)
                .getContent()
                .stream()
                .map(pagedComment -> {
                    String username = pagedComment.getUser().getUsername();
                    boolean hasReplies = pagedComment.getReplies().size() > 0;
                    String time = getTimeForComment(pagedComment.getId());

                    ResponseCommentDTO responseCommentDTO = modelMapper.map(pagedComment, ResponseCommentDTO.class);

                    responseCommentDTO.setUsername(username);
                    responseCommentDTO.setElapsedTime(time);
                    responseCommentDTO.setHasReplies(hasReplies);
                    String htmlContent = htmlService.markdownToHtml(pagedComment.getCommentText());
                    responseCommentDTO.setCommentText(htmlContent);

                    Long id = pagedComment.getId();

                    if (getReportsCountForComment(id) > 5) {
                        responseCommentDTO.setCommentText("This comment is under review");
                        if (log.isInfoEnabled()) {
                            log.info("Comment is under review");
                        }
                        if (log.isDebugEnabled()) {
                            log.debug("Comment with id: {} is under review", id);
                        }
                    }

                    return responseCommentDTO;
                }).toList();

        return new PageImpl<>(commentList, pageable, commentList.size());
    }

    /**
     * Verifies if a comment is owned by a specific user.
     *
     * @param commentId the ID of the comment to be checked
     * @param userId    the ID of the user whose ownership is to be verified
     * @return {@code true} if the comment is owned by the specified user, {@code false} otherwise
     * @throws CommentNotFoundException if the comment with the specified ID does not exist
     */
    private boolean verifyCommentOwner(Long commentId, Long userId) {
        if (log.isDebugEnabled()) {
            log.debug("Verifying comment owner of comment with id: {}", commentId);
        }
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> {
                    if (log.isErrorEnabled()) {
                        log.error("Comment not found with id: {}", commentId);
                    }
                    return new CommentNotFoundException();
                });

        boolean isOwner = comment.getUser().getId().equals(userId);
        if (isOwner) {
            if (log.isInfoEnabled()) {
                log.info("User is the owner of comment");
            }
            if (log.isDebugEnabled()) {
                log.info("User with id: {} is the owner of comment with id: {}", userId, commentId);
            }
        } else {
            if (log.isInfoEnabled()) {
                log.info("User is not the owner of comment");
            }
            if (log.isDebugEnabled()) {
                log.info("User with id: {} is not the owner of comment with id: {}", userId, commentId);
            }
        }

        return isOwner;
    }

    /**
     * Adds a like from a user to a comment.
     *
     * @param commentId the ID of the comment to which the like is being added
     * @param userId    the ID of the user who is liking the comment
     * @throws UserNotFoundException    if the user with the specified ID does not exist, or if the user has already liked the comment
     * @throws CommentNotFoundException if the comment with the specified ID does not exist, or if the user is trying to like their own comment
     */

    @Transactional
    @Override
    public void addLike(Long commentId, Long userId) {
        if (log.isInfoEnabled()) {
            log.info("Adding like to comment");
        }
        if (log.isDebugEnabled()) {
            log.debug("Adding like to comment with id: {}", commentId);
        }

        if (!verifyCommentOwner(commentId, userId)) {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> {
                        if (log.isErrorEnabled()) {
                            log.error("User not found with id: {}", userId);
                        }
                        return new UserNotFoundException("User not found with id: " + userId);
                    });

            Comment comment = commentRepository.findById(commentId)
                    .orElseThrow(() -> {
                        if (log.isErrorEnabled()) {
                            log.error("Comment not found with id: {}", commentId);
                        }
                        return new CommentNotFoundException();
                    });

            if (user.getLikedComments().contains(comment)) {
                if (log.isDebugEnabled()) {
                    log.debug("User with id: {} has already liked comment with id: {}", userId, commentId);
                }
                throw new UserNotFoundException("User has already liked this comment!");
            }

            user.getLikedComments().add(comment);
            comment.getUserList().add(user);

            userRepository.save(user);
            commentRepository.save(comment);
        } else {
            if (log.isDebugEnabled()) {
                log.debug("User with id: {} tried to like their own comment with id: {}", userId, commentId);
            }
            throw new UserNotFoundException("A user can't like his own comment !");
        }
    }

    /**
     * Deletes a comment by its ID.
     *
     * @param commentId the ID of the comment to be deleted
     * @throws CommentNotFoundException if the comment with the specified ID does not exist
     */
    @Override
    public void deleteComment(Long commentId) {
        if (log.isInfoEnabled()) {
            log.info("Deleting comment");
        }
        if (log.isDebugEnabled()) {
            log.debug("Deleting comment with ID: {}", commentId);
        }

        deleteReportsByCommentId(commentId);
        deleteLikesForDeletedComment(commentId);
        deleteRepliesForDeletedComment(commentId);

        if (!commentRepository.existsById(commentId)) {
            if (log.isErrorEnabled()) {
                log.error("Comment not found with id: {}", commentId);
            }
            throw new CommentNotFoundException();
        }
        String commentText = commentRepository.findById(commentId).get().getCommentText();
        Comment comment = commentRepository.findById(commentId).get();

        if (comment.getIdea() != null) {
            Long ideaId = comment.getIdea().getId();
            List<Long> subscribedUsersIds = subscriptionRepository.findUserIdByIdeaId(ideaId);

            List<User> subscribedUsers = new ArrayList<>();


            for (Long userId : subscribedUsersIds) {
                subscribedUsers.add(userRepository.findById(userId).get());
            }

            if (!subscribedUsers.isEmpty()) {
                sendEmailService.sendEmailDeletedComment(subscribedUsers, commentId, commentText);
                if (log.isInfoEnabled()) {
                    log.info("Sending email for deleted comment to subscribed users");
                }
            }
        }
        commentRepository.deleteById(commentId);
    }

    /**
     * Retrieves the list of users who liked a specific comment.
     *
     * @param commentId the ID of the comment for which likes are being retrieved
     * @return a list of {@link UserResponseDTO} representing the users who liked the comment
     * @throws RuntimeException if the comment with the specified ID does not exist
     */

    @Override
    public List<UserResponseDTO> getLikesForComment(Long commentId) {
        if (log.isInfoEnabled()) {
            log.info("Retrieving likes for comment");
        }
        if (log.isDebugEnabled()) {
            log.debug("Retrieving likes for comment with id: {}", commentId);
        }

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> {
                    if (log.isErrorEnabled()) {
                        log.error("Comment not found with id: {}", commentId);
                    }
                    return new RuntimeException("Comment not found");
                });


        return comment.getUserList().stream()
                .map(user -> modelMapper.map(user, UserResponseDTO.class))
                .collect(Collectors.toList());
    }

    /**
     * Retrieves the count of likes for a specific comment.
     *
     * @param commentId the ID of the comment for which the like count is being retrieved
     * @return the number of likes for the specified comment
     */
    @Override
    public int getLikesCountForComment(Long commentId) {
        if (log.isInfoEnabled()) {
            log.info("Retrieving likes count for comment");
        }
        if (log.isDebugEnabled()) {
            log.debug("Retrieving likes count for comment with id: {}", commentId);
        }

        return commentRepository.countLikesByCommentId(commentId);
    }

    /**
     * Deletes a like from a comment by a specific user.
     *
     * @param commentId the ID of the comment from which the like is being deleted
     * @param userId    the ID of the user whose like is being removed
     * @throws CommentNotFoundException if the comment with the specified ID does not exist
     * @throws UserNotFoundException    if the user with the specified ID does not exist
     */
    @Transactional
    @Override
    public void deleteLikes(Long commentId, Long userId) {
        if (log.isInfoEnabled()) {
            log.info("Deleting like for comment");
        }
        if (log.isDebugEnabled()) {
            log.debug("Deleting like for comment with id: {}", commentId);
        }

        if (!commentRepository.existsById(commentId)) {
            if (log.isErrorEnabled()) {
                log.error("Comment not found with id: {}", commentId);
            }
            throw new CommentNotFoundException();
        }

        if (!userRepository.existsById(userId)) {
            if (log.isErrorEnabled()) {
                log.error("User not found with id: {}", userId);
            }
            throw new UserNotFoundException("User not found!");
        }

        commentRepository.deleteLikes(commentId, userId);
    }

    /**
     * Deletes reports associated with a specific comment and user.
     *
     * @param commentId the unique identifier of the comment whose reports are to be deleted
     * @param userId    the unique identifier of the user associated with the reports to be deleted
     * @throws CommentNotFoundException if no comment is found with the given {@code commentId}
     * @throws UserNotFoundException    if no user is found with the given {@code userId}
     */

    @Transactional
    @Override
    public void deleteReport(Long commentId, Long userId) {
        if (log.isInfoEnabled()) {
            log.info("Deleting report for comment");
        }
        if (log.isDebugEnabled()) {
            log.debug("Deleting report for comment with id: {}", commentId);
        }

        if (!commentRepository.existsById(commentId)) {
            if (log.isErrorEnabled()) {
                log.error("Comment not found with id: {}", commentId);
            }
            throw new CommentNotFoundException();
        }

        if (!userRepository.existsById(userId)) {
            if (log.isErrorEnabled()) {
                log.error("User not found with id: {}", userId);
            }
            throw new UserNotFoundException("User with id " + userId + " does not exist");
        }

        commentRepository.deleteReport(commentId, userId);
    }

    @Override
    public void displayPlaceholder(Long commentId) {
        if (log.isInfoEnabled()) {
            log.info("Displaying placeholder for comment");
        }
        if (log.isInfoEnabled()) {
            log.info("Displaying placeholder for comment with id: {}", commentId);
        }

        Optional<Comment> comment = commentRepository.findById(commentId);
        if (comment.isPresent()) {
            comment.get().setCommentText("This comment was deleted by admin for it's offensive content");
            commentRepository.save(comment.get());

            if (log.isInfoEnabled()) {
                log.info("Successfully updated comment content with placeholder");
            }
        } else {
            if (log.isErrorEnabled()) {
                log.error("Comment not found with id: {}", commentId);
            }
            throw new CommentNotFoundException();
        }
    }

    /**
     * Deletes likes associated with a deleted comment.
     * If the comment does not exist, a CommentNotFoundException is thrown.
     *
     * @param commentId the ID of the comment whose likes are to be deleted
     * @throws CommentNotFoundException if the comment does not exist
     */
    public void deleteLikesForDeletedComment(Long commentId) {
        if (log.isInfoEnabled()) {
            log.info("Removing all likes when deleting the comment");
        }
        if (log.isDebugEnabled()) {
            log.debug("Removing all likes when deleting the comment with id: {}", commentId);
        }

        if (!commentRepository.existsById(commentId)) {
            if (log.isErrorEnabled()) {
                log.error("Comment not found with id: {}", commentId);
            }
            throw new CommentNotFoundException();
        }

        commentRepository.deleteLikesForComment(commentId);
    }

    /**
     * Deletes all replies for a given deleted comment ID.
     * <p>
     * This method first checks if the comment exists by the given ID.
     * If the comment does not exist, it throws a {@link CommentNotFoundException}.
     * If the comment exists, it retrieves all replies associated with the comment.
     * For each reply, it deletes any likes associated with that reply.
     * Finally, it deletes all replies associated with the given comment ID.
     *
     * @param commentId the ID of the deleted comment whose replies are to be deleted
     * @throws CommentNotFoundException if the comment with the specified ID does not exist
     */
    public void deleteRepliesForDeletedComment(Long commentId) {
        if (log.isInfoEnabled()) {
            log.info("Removing all replies when deleting the comment");
        }
        if (log.isDebugEnabled()) {
            log.debug("Removing all replies when deleting the comment with id: {}", commentId);
        }

        if (!commentRepository.existsById(commentId)) {
            if (log.isErrorEnabled()) {
                log.error("Comment not found with id: {}", commentId);
            }
            throw new CommentNotFoundException();
        }

        List<Comment> replies = commentRepository.findAllRepliesForComment(commentId);
        for (Comment reply : replies) {
            commentRepository.deleteReportsByCommentId(reply.getId());
            commentRepository.deleteLikesForComment(reply.getId());
        }

        commentRepository.deleteRepliesForComment(commentId);
    }


    /**
     * Deletes reports associated with a deleted comment.
     * If the comment does not exist, a CommentNotFoundException is thrown.
     *
     * @param commentId the ID of the comment whose reports are to be deleted
     * @throws CommentNotFoundException if the comment does not exist
     */
    @Transactional
    public void deleteReportsByCommentId(Long commentId) {
        if (log.isInfoEnabled()) {
            log.info("Deleting reports for comment");
        }
        if (log.isDebugEnabled()) {
            log.debug("Deleting reports for comment with id: {}", commentId);
        }

        if (!commentRepository.existsById(commentId)) {
            if (log.isErrorEnabled()) {
                log.error("Comment not found with id: {}", commentId);
            }
            throw new CommentNotFoundException();
        }

        commentRepository.deleteReportsByCommentId(commentId);
        if (log.isInfoEnabled()) {
            log.info("Successfully deleted reports for the comment");
        }
    }

    @Override
    public boolean existsLikeByCommentIdAndUserId(Long commentId, Long userId) {
        boolean exists = commentRepository.existsLikeByCommentIdAndUserId(commentId, userId);
        if (log.isInfoEnabled()) {
            log.info("Checking existence of like by comment and user");
        }
        return exists;
    }

    @Override
    public boolean existsReportByCommentIdAndUserId(Long commentId, Long userId) {
        boolean exists = commentRepository.existsReportByCommentIdAndUserId(commentId, userId);
        if (log.isInfoEnabled()) {
            log.info("Checking existence of report by comment and user");
        }
        return exists;
    }

    @Override
    public int getReportsCountForComment(Long commentId) {
        if (log.isInfoEnabled()) {
            log.info("Retrieving the number of reports for the comment");
        }
        if (log.isDebugEnabled()) {
            log.debug("Retrieving the number of reports for the comment with id: {}", commentId);
        }

        if (!commentRepository.existsById(commentId)) {
            if (log.isErrorEnabled()) {
                log.error("Comment not found with id: {}", commentId);
            }
            throw new CommentNotFoundException();
        }
        return commentRepository.countReportsByCommentId(commentId);
    }

    @Transactional
    @Override
    public CommentPageDTO getAllCommentsByReportsNr(Pageable pageable) {
        if (log.isInfoEnabled()) {
            log.info("Getting all the comments sorted by reports number");
        }
        List<Long> commentsIds = commentRepository.moreThenFiveReports();
        int total = commentsIds.size();

        List<CommentDashboardResponseDTO> contents = new ArrayList<>();

        for (Long id : commentsIds) {
            Comment comment = commentRepository.findById(id)
                    .orElseThrow(() -> {
                        if (log.isErrorEnabled()) {
                            log.error("Comment not found");
                        }
                        return new CommentNotFoundException();
                    });
            CommentDashboardResponseDTO commentDashboardResponseDTO = new CommentDashboardResponseDTO();
            commentDashboardResponseDTO.setId(comment.getId());
            String htmlContent = htmlService.markdownToHtml(comment.getCommentText());
            commentDashboardResponseDTO.setContent(htmlContent);
            commentDashboardResponseDTO.setNrReports(commentRepository.countReportsByCommentId(id));
            contents.add(commentDashboardResponseDTO);
        }
        contents.sort((c1, c2) -> Integer.compare(c2.getNrReports(), c1.getNrReports()));

        return new CommentPageDTO(total, new PageImpl<>(contents, pageable, contents.size()));
    }


    /**
     * Adds a report from a user to a comment.
     * <p>
     * This method performs the following actions:
     * - Verifies that the comment exists and throws an exception if not found.
     * - Verifies that the user exists and throws an exception if not found.
     * - Checks if the user is the owner of the comment and prevents the report if true.
     * - Checks if the user has already reported the comment and prevents multiple reports.
     * - If both the comment and user exist, and the user is not the owner of the comment, and the user hasn't already reported the comment,
     * adds the report to both the user and the comment, and saves the changes to the database.
     *
     * @param commentId the ID of the comment to be reported
     * @param userId    the ID of the user who is reporting the comment
     * @throws UserNotFoundException    if the user with the specified ID does not exist
     * @throws CommentNotFoundException if the comment with the specified ID does not exist
     * @throws IllegalArgumentException if the user tries to report their own comment or if the user has already reported the comment
     */
    @Transactional
    @Override
    public void addReport(Long commentId, Long userId) {
        if (log.isInfoEnabled()) {
            log.info("Adding report for comment");
        }
        if (log.isDebugEnabled()) {
            log.debug("Adding report for comment with id: {}", commentId);
        }

        if (!verifyCommentOwner(commentId, userId)) {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> {
                        if (log.isErrorEnabled()) {
                            log.error("User not found with id: {}", userId);
                        }
                        return new UserNotFoundException("User not found with id: " + userId);
                    });

            Comment comment = commentRepository.findById(commentId)
                    .orElseThrow(() -> {
                        if (log.isErrorEnabled()) {
                            log.error("Comment not found with id: {}", commentId);
                        }
                        return new CommentNotFoundException();
                    });


            if (user.getReportedComments().contains(comment)) {
                if (log.isWarnEnabled()) {
                    log.warn("User has already reported this comment");
                }
                throw new UserNotFoundException("User has already reported this comment!");
            }

            user.getReportedComments().add(comment);
            comment.getListOfUsers().add(user);

            userRepository.save(user);
            commentRepository.save(comment);
        } else {
            if (log.isWarnEnabled()) {
                log.warn("User can't report his own comment");
            }
            throw new UserNotFoundException("A user can't report his own comment !");
        }
    }


    @Transactional
    @Override
    public void setReviewStatusByCommentId(ReviewStatus reviewStatus, Long commentId) {
        if (log.isInfoEnabled()) {
            log.info("Changing the review status for comment");
        }
        if (log.isDebugEnabled()) {
            log.debug("Changing the review status for comment with id: {}", commentId);
        }
        Optional<Comment> comment = commentRepository.findById(commentId);
        if (comment.isPresent()) {
            comment.get().setReviewStatus(reviewStatus);
            commentRepository.save(comment.get());
        } else {
            if (log.isErrorEnabled()) {
                log.error("Comment not found with id: {}", commentId);
            }
            throw new CommentNotFoundException();
        }
    }

    @Override
    public ReviewStatus getReviewStatusByCommentId(Long commentId) {
        if (log.isInfoEnabled()) {
            log.info("Retrieving review status for comment");
        }
        if (log.isDebugEnabled()) {
            log.debug("Retrieving review status for comment with id: {}", commentId);
        }
        if (!commentRepository.existsById(commentId)) {
            if (log.isErrorEnabled()) {
                log.error("Comment not found with id: {}", commentId);
            }
            throw new CommentNotFoundException();
        }
        return commentRepository.getReviewStatusByCommentId(commentId);
    }

    @Override
    public Long countNumberOfLikes() {
        if (log.isInfoEnabled()) {
            log.info("Retrieving total number of likes");
        }
        return commentRepository.countAllLikes();
    }

    @Override
    public Long countNumberOfReports() {
        if (log.isInfoEnabled()) {
            log.info("Retrieving total number of reports");
        }
        return commentRepository.countAllReports();
    }


}