package com.atoss.idea.management.system.service.implementation;

import com.atoss.idea.management.system.exception.IdeaNotFoundException;
import com.atoss.idea.management.system.exception.UserNotFoundException;
import com.atoss.idea.management.system.exception.CommentNotFoundException;
import com.atoss.idea.management.system.repository.CommentRepository;
import com.atoss.idea.management.system.repository.IdeaRepository;
import com.atoss.idea.management.system.repository.UserRepository;
import com.atoss.idea.management.system.repository.dto.CommentDTO;
import com.atoss.idea.management.system.repository.dto.RequestCommentDTO;
import com.atoss.idea.management.system.repository.dto.RequestCommentReplyDTO;
import com.atoss.idea.management.system.repository.dto.ResponseCommentDTO;
import com.atoss.idea.management.system.repository.entity.Comment;
import com.atoss.idea.management.system.repository.dto.ResponseCommentReplyDTO;
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

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Log4j2
@Service
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final IdeaRepository ideaRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    public CommentServiceImpl(CommentRepository commentRepository, IdeaRepository ideaRepository,
                              UserRepository userRepository, ModelMapper modelMapper) {
        this.commentRepository = commentRepository;
        this.ideaRepository = ideaRepository;
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }

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

    @Transactional
    @Override
    public ResponseCommentDTO addComment(RequestCommentDTO requestCommentDTO) {

        User user = userRepository.findByUsername(requestCommentDTO.getUsername()).orElseThrow(() -> new UserNotFoundException("User not found!"));

        Idea idea = ideaRepository.findById(requestCommentDTO.getIdeaId()).get();

        java.util.Date creationDate = new java.util.Date();

        Comment newComment =  new Comment();

        newComment.setUser(user);
        newComment.setIdea(idea);
        newComment.setParent(null);
        newComment.setCommentText(requestCommentDTO.getCommentText());
        newComment.setCreationDate(creationDate);

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

        commentRepository.save(newReply);

        ResponseCommentReplyDTO responseCommentReplyDTO = modelMapper.map(newReply, ResponseCommentReplyDTO.class);
        responseCommentReplyDTO.setUsername(user.getUsername());

        return responseCommentReplyDTO;
    }

    @Override
    public List<ResponseCommentDTO> getAllCommentsByIdeaId(Long ideaId) {
        if (!ideaRepository.existsById(ideaId)) {
            throw new IdeaNotFoundException();
        }

        List<ResponseCommentDTO> filteredList = commentRepository.findAllByIdeaId(ideaId).stream()
                .map(comment -> {
                    boolean hasReplies = comment.getReplies().size() > 0;
                    ResponseCommentDTO responseCommentDTO = new ResponseCommentDTO();
                    responseCommentDTO = modelMapper.map(comment, ResponseCommentDTO.class);
                    responseCommentDTO.setHasReplies(hasReplies);
                    return responseCommentDTO;
                })
                .collect(Collectors.toList());

        return filteredList;
    }

    @Transactional
    @Override
    public List<ResponseCommentReplyDTO> getAllRepliesByCommentId(Long commentId) {
        if (!commentRepository.existsById(commentId)) {
            throw new CommentNotFoundException();
        }

        List<ResponseCommentReplyDTO> filteredList = commentRepository.findById(commentId).get().getReplies().stream()
                .map(reply -> {
                    String username = reply.getUser().getUsername();
                    ResponseCommentReplyDTO responseCommentReplyDTO = modelMapper.map(reply, ResponseCommentReplyDTO.class);
                    responseCommentReplyDTO.setUsername(username);
                    String time = getTimeForComment(reply.getId());
                    responseCommentReplyDTO.setElapsedTime(time);
                    return responseCommentReplyDTO;
                })
                .collect(Collectors.collectingAndThen(Collectors.toList(),
                        list -> {
                            Collections.reverse(list);
                            return list; // Return the reversed list
                        }));

        return filteredList;
    }

    @Override
    public Page<ResponseCommentDTO> getAllCommentsByIdeaIdWithPaging(Long ideaId, Pageable pageable) {
        if (!ideaRepository.existsById(ideaId)) {
            throw new IdeaNotFoundException();
        }
        // Dragos B.
        // The original Comment entity stores a User object
        // ResponseCommentDTO stores the username
        // of their respective user (String)
        // A direct conversion between the two is impossible
        // My Workaround:
        // - find and store the username in a local variable
        // - map the comment to its corespondent response class
        // - update the username for the new object

        return new PageImpl<ResponseCommentDTO>(
                commentRepository.findAll(pageable)
                        .stream()
                        .filter(comment -> comment.getIdea() != null)
                        .filter(comment -> comment.getIdea().getId() == ideaId)
                        .map(comment -> {
                            String username = comment.getUser().getUsername();
                            boolean hasReplies = comment.getReplies().size() > 0;
                            String time = getTimeForComment(comment.getId());
                            ResponseCommentDTO responseCommentDTO = modelMapper.map(comment, ResponseCommentDTO.class);
                            responseCommentDTO.setUsername(username);
                            responseCommentDTO.setReplies(null);
                            responseCommentDTO.setElapsedTime(time);
                            responseCommentDTO.setHasReplies(hasReplies);
                            return responseCommentDTO;
                        })
                        .toList()
        );
    }

    @Override
    public CommentDTO updateComment(Comment comment) {
        return null;
    }

    @Override
    public void deleteComment(Long commentId) {
        if (!commentRepository.existsById(commentId)) {
            throw new CommentNotFoundException();
        }
        commentRepository.deleteById(commentId);
    }
}