package com.atoss.idea.management.system.service.implementation;

import com.atoss.idea.management.system.exception.UserNotFoundException;
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

        return seconds + " s " + minutes + " m " + hours + " h " + days + " d ";
    }

    @Transactional
    @Override
    public ResponseCommentDTO addComment(RequestCommentDTO requestCommentDTO) {

        User user = userRepository.findByUsername(requestCommentDTO.getUsername()).orElseThrow(() -> new UserNotFoundException("User not found!"));
        user.setAvatar(null);

        Idea idea = ideaRepository.findIdeaById(requestCommentDTO.getIdeaId());

        if (!ideaRepository.existsById(requestCommentDTO.getIdeaId())) {
            throw new RuntimeException();
        }

        java.util.Date creationDate = new java.util.Date();

        Comment newComment =  new Comment();

        newComment.setUser(user);
        newComment.setIdea(ideaRepository.findIdeaById(requestCommentDTO.getIdeaId()));
        newComment.setParent(null);
        newComment.setCommentText(requestCommentDTO.getCommentText());
        newComment.setCreationDate(creationDate);

        commentRepository.save(newComment);

        // TODO add ResponseCommentDTO
        return modelMapper.map(newComment, ResponseCommentDTO.class);
    }

    @Override
    public ResponseCommentDTO addReply(RequestCommentReplyDTO requestCommentReplyDTO) {

        if (userRepository.findByUsername(requestCommentReplyDTO.getUsername()).isEmpty()) {
            throw new RuntimeException();
        }

        if (!commentRepository.existsById(requestCommentReplyDTO.getParentId())) {
            throw new RuntimeException();
        }

        java.util.Date creationDate = new java.util.Date();

        Comment newReply =  new Comment();

        newReply.setUser(userRepository.findUserByUsername(requestCommentReplyDTO.getUsername()));
        newReply.setIdea(null);
        newReply.setParent(commentRepository.findById(requestCommentReplyDTO.getParentId()).get());
        newReply.setCommentText(requestCommentReplyDTO.getCommentText());
        newReply.setCreationDate(creationDate);

        commentRepository.save(newReply);

        // TODO add ResponseCommentDTO

        return modelMapper.map(newReply, ResponseCommentDTO.class);
    }

    @Override
    public List<ResponseCommentDTO> getAllCommentsByIdeaId(RequestCommentDTO requestCommentDTO) {
        if (!ideaRepository.existsById(requestCommentDTO.getIdeaId())) {
            throw new RuntimeException();
        }

        List<ResponseCommentDTO> filteredList = commentRepository.findAllByIdeaId(requestCommentDTO.getIdeaId()).stream()
                .map(comment -> modelMapper.map(comment, ResponseCommentDTO.class))
                .collect(Collectors.toList());

        return filteredList;
    }

    @Override
    public List<ResponseCommentReplyDTO> getAllRepliesByCommentId(RequestCommentReplyDTO requestCommentReplyDTO) {
        if (!commentRepository.existsById(requestCommentReplyDTO.getParentId())) {
            throw new RuntimeException();
        }

        List<ResponseCommentReplyDTO> filteredList = commentRepository.findById(requestCommentReplyDTO.getParentId()).get().getReplies().stream()
                .map(comment -> modelMapper.map(comment, ResponseCommentReplyDTO.class))
                .collect(Collectors.toList());

        return filteredList;
    }

    @Override
    public Page<ResponseCommentDTO> getAllCommentsByIdeaIdWithPaging(Long ideaId, Pageable pageable) {
        if (!ideaRepository.existsById(ideaId)) {
            throw new RuntimeException();
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
                                        return responseCommentReplyDTO;
                                    })
                                    .toList();
                            String username = comment.getUser().getUsername();
                            ResponseCommentDTO responseCommentDTO = modelMapper.map(comment, ResponseCommentDTO.class);
                            responseCommentDTO.setUsername(username);
                            responseCommentDTO.setReplies(replies);
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
