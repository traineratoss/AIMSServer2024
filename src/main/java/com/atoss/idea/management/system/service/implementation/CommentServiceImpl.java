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

    // create a link between comment and elapsed time function
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

    // converts milliseconds in the correct time unit
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

    @Override
    public void deleteComment(Long commentId) {
        if (!commentRepository.existsById(commentId)) {
            throw new CommentNotFoundException();
        }
        commentRepository.deleteById(commentId);
    }


    @Override
    public List<UserResponseDTO> getLikesForComment(Long commentId){
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new RuntimeException("Comment not found"));
        return comment.getUserList().stream()
                .map(user -> modelMapper.map(user, UserResponseDTO.class))
                .collect(Collectors.toList());
    }

    public int getLikesCountForComment(Long commentId) {
        return commentRepository.countLikesByCommentId(commentId);
    }

    @Override
    public void deleteLikes(Long commentId, Long userId){
        if (!commentRepository.existsById(commentId)) {
            throw new CommentNotFoundException();
        }
        commentRepository.deleteLikes(commentId,userId);
    }
}