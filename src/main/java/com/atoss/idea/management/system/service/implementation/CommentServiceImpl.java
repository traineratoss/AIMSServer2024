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
import java.util.Date;
import java.util.List;
import java.util.Optional;
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
        Optional<Comment> comment = commentRepository.findById(id);
        Date creationDate =  comment.get().getCreationDate();

        return getElapsedTime(creationDate);
    }

    @Override
    public String getElapsedTime(Date creationDate) {

        Date currentDate = new Date();
        Long seconds = (currentDate.getTime() - creationDate.getTime()) / 1000;

        Long minutes = seconds / 60;

        Long hours = minutes / 60;

        Long days = hours / 24;

        Long months = days / 28;

        StringBuilder sb = new StringBuilder();

        if (seconds >= 60) {
            if (minutes >= 60) {
                if (hours >= 24) {
                    if (days >= 28) {
                        sb.append(months + "months");
                    } else {
                        sb.append(days + "days");
                    }
                } else {
                    sb.append(hours + "hours");
                }
            } else {
                sb.append(minutes + "mins");
            }
        } else {
            sb.append(seconds + "seconds");
        }
        return sb.toString();
    }

    @Transactional
    @Override
    public ResponseCommentDTO addComment(RequestCommentDTO requestCommentDTO) {

        User user = userRepository.findByUsername(requestCommentDTO.getUsername()).orElseThrow(() -> new UserNotFoundException("User not found!"));

        Idea idea = ideaRepository.findIdeaById(requestCommentDTO.getIdeaId());

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
        // TODO add ResponseCommentDTO
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
                .map(comment -> modelMapper.map(comment, ResponseCommentDTO.class))
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
                .collect(Collectors.toList());
        return filteredList;
    }



    @Override
    public Page<ResponseCommentDTO> getAllCommentsByIdeaIdWithPaging(Long ideaId, Pageable pageable) {
        if (!ideaRepository.existsById(ideaId)) {
            throw new IdeaNotFoundException();
        }
        // Dragos B.
        // The original Comment entity stores a User object
        // ResponseCommentDTO and ResponseCommentReplyDTO store the username
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
                            List<ResponseCommentReplyDTO> replies = comment.getReplies().stream()
                                    .map(reply -> {
                                        String username = reply.getUser().getUsername();
                                        ResponseCommentReplyDTO responseCommentReplyDTO = modelMapper.map(reply, ResponseCommentReplyDTO.class);
                                        responseCommentReplyDTO.setUsername(username);
                                        String time = getTimeForComment(reply.getId());
                                        responseCommentReplyDTO.setElapsedTime(time);
                                        return responseCommentReplyDTO;
                                    })
                                    .toList();
                            String username = comment.getUser().getUsername();
                            ResponseCommentDTO responseCommentDTO = modelMapper.map(comment, ResponseCommentDTO.class);
                            responseCommentDTO.setUsername(username);
                            responseCommentDTO.setReplies(replies);
                            String time = getTimeForComment(comment.getId());
                            responseCommentDTO.setElapsedTime(time);
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
    public void deleteComment(Long id) {

    }

    @Override
    public CommentDTO getComment(Long id) {
        return null;
    }

    @Override
    public CommentDTO getReply(Long parentID) {
        return null;
    }

    @Override
    public List<CommentDTO> getAllComments() {
        return null;
    }

}