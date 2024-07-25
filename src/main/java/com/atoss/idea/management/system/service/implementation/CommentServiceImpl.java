package com.atoss.idea.management.system.service.implementation;

import com.atoss.idea.management.system.exception.IdeaNotFoundException;
import com.atoss.idea.management.system.exception.UserNotFoundException;
import com.atoss.idea.management.system.exception.CommentNotFoundException;
import com.atoss.idea.management.system.repository.CommentRepository;
import com.atoss.idea.management.system.repository.IdeaRepository;
import com.atoss.idea.management.system.repository.UserRepository;
import com.atoss.idea.management.system.repository.dto.*;
import com.atoss.idea.management.system.repository.entity.Comment;
import com.atoss.idea.management.system.repository.entity.Idea;
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

    /**
     * CONSTRUCTOR
     *
     * @param commentRepository for accessing CRUD repository methods for Comment Entity
     * @param ideaRepository for accessing CRUD repository methods for Idea Entity
     * @param userRepository for accessing CRUD repository methods for User Entity
     * @param modelMapper for mapping entity-dto relationships
     */
    public CommentServiceImpl(CommentRepository commentRepository, IdeaRepository ideaRepository,
                              UserRepository userRepository, ModelMapper modelMapper) {
        this.commentRepository = commentRepository;
        this.ideaRepository = ideaRepository;
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
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
        Optional<Comment> commentOptional = commentRepository.findById(id);

        if (commentOptional.isPresent()) {
            Comment comment = commentOptional.get();
            Date creationDate = comment.getCreationDate();
            return getElapsedTime(creationDate);
        } else {
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
        for (String word : badWords) {
            String pattern = "\\b" + word + "\\b";
            text = text.replaceAll("(?i)" + pattern, "*".repeat(word.length()));
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
        try {
            FileReader fileReader = new FileReader(path);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line;

            while ((line = bufferedReader.readLine()) != null) {
                String word = line.trim();

                badWords.add(word);
            }

            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Adds a new comment to an idea.
     * <p>
     * This method performs the following steps:
     * <ul>
     *     <li>Retrieves the user and idea based on the provided DTO.</li>
     *     <li>Checks for and reads the list of bad words from a file.</li>
     *     <li>Filters the comment text for any bad words.</li>
     *     <li>Creates and saves a new comment with the filtered text.</li>
     *     <li>Maps the newly created comment to a response DTO and sets the username.</li>
     * </ul>
     * </p>
     *
     * @param requestCommentDTO the DTO containing information about the comment to be added
     * @return a {@link ResponseCommentDTO} representing the added comment
     * @throws UnsupportedEncodingException if an error occurs while decoding the bad words file path
     * @throws UserNotFoundException if the user specified in the DTO does not exist
     * @throws IdeaNotFoundException if the idea specified in the DTO does not exist
     */

    @Transactional
    @Override
    public ResponseCommentDTO addComment(RequestCommentDTO requestCommentDTO) throws UnsupportedEncodingException {

        User user = userRepository.findByUsername(requestCommentDTO.getUsername()).orElseThrow(() -> new UserNotFoundException("User not found!"));

        Idea idea = ideaRepository.findById(requestCommentDTO.getIdeaId()).orElseThrow(() -> new IdeaNotFoundException("Idea not found!"));

        Comment newComment =  new Comment();
        String wordsFilePath = "textTerms/badWords.txt";
        URL resourceUrl = classLoader.getResource(wordsFilePath);
        if (resourceUrl != null) {
            String filePath = URLDecoder.decode(resourceUrl.getFile(), "UTF-8");
            readBadWordsFromFile(filePath);
        }
        java.util.Date creationDate = new java.util.Date();

        newComment.setUser(user);
        newComment.setIdea(idea);
        newComment.setParent(null);
        newComment.setCommentText(requestCommentDTO.getCommentText());
        newComment.setCreationDate(creationDate);
        String filteredCommentText = filterBadWords(newComment.getCommentText());
        newComment.setCommentText(filteredCommentText);

        commentRepository.save(newComment);

        ResponseCommentDTO responseCommentDTO = modelMapper.map(newComment, ResponseCommentDTO.class);
        responseCommentDTO.setUsername(user.getUsername());

        return responseCommentDTO;
    }

    /**
     * Adds a reply to an existing comment.
     * <p>
     * This method performs the following steps:
     * <ul>
     *     <li>Retrieves the user based on the provided DTO.</li>
     *     <li>Checks if the parent comment exists.</li>
     *     <li>Filters the reply text for any bad words.</li>
     *     <li>Creates and saves a new reply to the parent comment.</li>
     *     <li>Maps the newly created reply to a response DTO and sets the username.</li>
     * </ul>
     * </p>
     *
     * @param requestCommentReplyDTO the DTO containing information about the reply to be added
     * @return a {@link ResponseCommentReplyDTO} representing the added reply
     * @throws UserNotFoundException if the user specified in the DTO does not exist
     * @throws CommentNotFoundException if the parent comment specified in the DTO does not exist
     */
    @Transactional
    @Override
    public ResponseCommentReplyDTO addReply(RequestCommentReplyDTO requestCommentReplyDTO) {

        User user = userRepository.findByUsername(requestCommentReplyDTO.getUsername())
                .orElseThrow(() -> new UserNotFoundException("User not found!"));

        if (!commentRepository.existsById(requestCommentReplyDTO.getParentId())) {
            throw new CommentNotFoundException();
        }

        java.util.Date creationDate = new java.util.Date();

        Comment newReply =  new Comment();

        newReply.setUser(user);
        newReply.setIdea(null);
        newReply.setParent(commentRepository.findById(requestCommentReplyDTO.getParentId()).get());
        newReply.setCommentText(requestCommentReplyDTO.getCommentText());
        newReply.setCreationDate(creationDate);
        String filteredCommentText = filterBadWords(newReply.getCommentText());
        newReply.setCommentText(filteredCommentText);

        commentRepository.save(newReply);

        ResponseCommentReplyDTO responseCommentReplyDTO = modelMapper.map(newReply, ResponseCommentReplyDTO.class);
        responseCommentReplyDTO.setUsername(user.getUsername());

        return responseCommentReplyDTO;
    }

    /**
     * Retrieves all replies for a given comment, paginated.
     * <p>
     * This method performs the following steps:
     * <ul>
     *     <li>Checks if the comment exists.</li>
     *     <li>Fetches the replies to the comment, applying pagination.</li>
     *     <li>Maps each reply to a response DTO, including additional information like username and elapsed time.</li>
     *     <li>Returns the list of replies as a {@link Page} of {@link ResponseCommentReplyDTO}.</li>
     * </ul>
     * </p>
     *
     * @param commentId the ID of the comment for which replies are to be fetched
     * @param pageable the pagination information
     * @return a {@link Page} of {@link ResponseCommentReplyDTO} containing the replies to the specified comment
     * @throws CommentNotFoundException if the comment with the specified ID does not exist
     */
    @Transactional
    @Override
    public Page<ResponseCommentReplyDTO> getAllRepliesByCommentId(Long commentId, Pageable pageable) {
        if (!commentRepository.existsById(commentId)) {
            throw new CommentNotFoundException();
        }

        List<ResponseCommentReplyDTO> replyList = commentRepository.findAllByParentId(commentId, pageable)
                .getContent()
                .stream()
                .map(pagedReply -> {
                    String username = pagedReply.getUser().getUsername();
                    String time = getTimeForComment(pagedReply.getId());

                    ResponseCommentReplyDTO responseCommentReplyDTO = new ResponseCommentReplyDTO();
                    responseCommentReplyDTO.setId(pagedReply.getId());
                    responseCommentReplyDTO.setUsername(username);
                    responseCommentReplyDTO.setCommentText(pagedReply.getCommentText());
                    responseCommentReplyDTO.setElapsedTime(time);
                    responseCommentReplyDTO.setParentId(commentId);
                    return responseCommentReplyDTO;
                }).toList();

        return new PageImpl<>(replyList, pageable, replyList.size());

    }
    /**
     * Retrieves a paginated list of comments for a given idea.
     * <p>
     * This method performs the following steps:
     * <ul>
     *     <li>Checks if the idea with the specified ID exists.</li>
     *     <li>Fetches all comments associated with the given idea, applying pagination.</li>
     *     <li>Maps each comment to a response DTO, including additional information like the username of the commenter,
     *         elapsed time since the comment was created, and whether the comment has replies.</li>
     *     <li>Returns the paginated list of comments as a {@link Page} of {@link ResponseCommentDTO}.</li>
     * </ul>
     * </p>
     *
     * @param ideaId the ID of the idea for which comments are to be fetched
     * @param pageable the pagination information to control the size and number of pages
     * @return a {@link Page} of {@link ResponseCommentDTO} containing comments associated with the specified idea
     * @throws IdeaNotFoundException if the idea with the specified ID does not exist
     */
    @Override
    public Page<ResponseCommentDTO> getAllPagedCommentsByIdeaId(Long ideaId, Pageable pageable) {

        if (!ideaRepository.existsById(ideaId)) {
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
                    return responseCommentDTO;
                }).toList();

        return new PageImpl<>(commentList, pageable, commentList.size());
    }

    /**
     * Verifies if a comment is owned by a specific user.
     * <p>
     * This method performs the following steps:
     * <ul>
     *     <li>Retrieves the comment based on the provided comment ID.</li>
     *     <li>Checks if the ID of the user who owns the comment matches the provided user ID.</li>
     * </ul>
     * </p>
     *
     * @param commentId the ID of the comment to be checked
     * @param userId the ID of the user whose ownership is to be verified
     * @return {@code true} if the comment is owned by the specified user, {@code false} otherwise
     * @throws CommentNotFoundException if the comment with the specified ID does not exist
     */
    private boolean verifyCommentOwner(Long commentId, Long userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException());


        return comment.getUser().getId().equals(userId);

    }

    /**
     * Adds a like from a user to a comment.
     * <p>
     * This method performs the following checks and actions:
     * <ul>
     *     <li>Verifies that the comment is not owned by the user trying to like it, as a user cannot like their own comment.</li>
     *     <li>Checks if the user exists and throws an exception if not found.</li>
     *     <li>Checks if the comment exists and throws an exception if not found.</li>
     *     <li>Checks if the user has already liked the comment; if so, throws an exception.</li>
     *     <li>If all checks pass, adds the like to both the user and the comment, and saves the changes to the database.</li>
     * </ul>
     * </p>
     *
     * @param commentId the ID of the comment to which the like is being added
     * @param userId the ID of the user who is liking the comment
     * @throws UserNotFoundException if the user with the specified ID does not exist, or if the user has already liked the comment
     * @throws CommentNotFoundException if the comment with the specified ID does not exist, or if the user is trying to like their own comment
     */

    @Transactional
    @Override
    public void addLike(Long commentId, Long userId) {
        if (!verifyCommentOwner(commentId, userId)) {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

            Comment comment = commentRepository.findById(commentId)
                    .orElseThrow(() -> new CommentNotFoundException());

            if (user.getLikedComments().contains(comment)) {
                throw new UserNotFoundException("User has already liked this comment!");
            }

            user.getLikedComments().add(comment);
            comment.getUserList().add(user);

            userRepository.save(user);
            commentRepository.save(comment);
        } else {
            throw new UserNotFoundException("A user can't like his own comment !");
        }
    }

    /**
     * Deletes a comment by its ID.
     * <p>
     * This method performs the following actions:
     * <ul>
     *     <li>Checks if the comment with the specified ID exists.</li>
     *     <li>If the comment exists, deletes it from the repository.</li>
     * </ul>
     * </p>
     *
     * @param commentId the ID of the comment to be deleted
     * @throws CommentNotFoundException if the comment with the specified ID does not exist
     */
    @Override
    public void deleteComment(Long commentId) {
        if (!commentRepository.existsById(commentId)) {
            throw new CommentNotFoundException();
        }
        commentRepository.deleteById(commentId);
    }

    /**
     * Retrieves the list of users who liked a specific comment.
     * <p>
     * This method finds the comment with the given ID and maps the list of users who liked it into a list of
     * {@link UserResponseDTO} objects.
     * </p>
     *
     * @param commentId the ID of the comment for which likes are being retrieved
     * @return a list of {@link UserResponseDTO} representing the users who liked the comment
     * @throws RuntimeException if the comment with the specified ID does not exist
     */

    @Override
    public List<UserResponseDTO> getLikesForComment(Long commentId) {

        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new RuntimeException("Comment not found"));
        return comment.getUserList().stream()
                .map(user -> modelMapper.map(user, UserResponseDTO.class))
                .collect(Collectors.toList());
    }

    /**
     * Retrieves the count of likes for a specific comment.
     * <p>
     * This method returns the total number of likes associated with the comment identified by the given ID.
     * </p>
     *
     * @param commentId the ID of the comment for which the like count is being retrieved
     * @return the number of likes for the specified comment
     */
    @Override
    public int getLikesCountForComment(Long commentId) {
        return commentRepository.countLikesByCommentId(commentId);
    }

    /**
     * Deletes a like from a comment by a specific user.
     * <p>
     * This method performs the following actions:
     * <ul>
     *     <li>Checks if the comment with the specified ID exists; throws {@link CommentNotFoundException} if not.</li>
     *     <li>Checks if the user with the specified ID exists; throws {@link UserNotFoundException} if not.</li>
     *     <li>If both the comment and user exist, deletes the like from the comment by the user.</li>
     * </ul>
     * </p>
     *
     * @param commentId the ID of the comment from which the like is being deleted
     * @param userId the ID of the user whose like is being removed
     * @throws CommentNotFoundException if the comment with the specified ID does not exist
     * @throws UserNotFoundException if the user with the specified ID does not exist
     */
    @Override
    public void deleteLikes(Long commentId, Long userId) {
        if (!commentRepository.existsById(commentId)) {
            throw new CommentNotFoundException();
        }
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException();
        }
        commentRepository.deleteLikes(commentId, userId);
    }


        /**
         * Checks if a specific user has liked a particular comment.
         * <p>
         * This method verifies whether a like exists from the user for the specified comment.
         * </p>
         *
         * @param commentId the ID of the comment to check for likes
         * @param userId the ID of the user to check for the like
         * @return {@code true} if the user has liked the comment, {@code false} otherwise
         */

        @Override
        public boolean existsByCommentIdAndUserId(Long commentId, Long userId){
            return commentRepository.existsByCommentIdAndUserId(commentId, userId);
        }

}