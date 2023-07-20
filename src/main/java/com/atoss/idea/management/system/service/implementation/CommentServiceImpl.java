package com.atoss.idea.management.system.service.implementation;

import com.atoss.idea.management.system.repository.CommentRepository;
import com.atoss.idea.management.system.repository.IdeaRepository;
import com.atoss.idea.management.system.repository.UserRepository;
import com.atoss.idea.management.system.repository.dto.CommentDTO;
import com.atoss.idea.management.system.repository.dto.RequestCommentDTO;
import com.atoss.idea.management.system.repository.dto.RequestCommentReplyDTO;
import com.atoss.idea.management.system.repository.entity.Comment;
import com.atoss.idea.management.system.service.CommentService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

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
    public String getElapsedTime(Date creationDate) {

        LocalDate oldDate =  creationDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate currentDate = LocalDate.now();

        Period elapsedTime = Period.between(currentDate, oldDate);
        elapsedTime.get(ChronoUnit.HOURS);

        Long test = ChronoUnit.HOURS.between(currentDate, oldDate);

        String response = elapsedTime + " h ago";
        return response;
    }

    @Override
    public CommentDTO addComment(RequestCommentDTO requestCommentDTO) {

        if (!userRepository.findByUsername(requestCommentDTO.getUsername()).isPresent()) {
            throw new RuntimeException();
        }

        if (!ideaRepository.existsById(requestCommentDTO.getIdeaId())) {
            throw new RuntimeException();
        }

        java.util.Date creationDate = new java.util.Date();

        Comment newComment =  new Comment();

        newComment.setUser(userRepository.findUserByUsername(requestCommentDTO.getUsername()));
        newComment.setIdea(ideaRepository.findIdeaById(requestCommentDTO.getIdeaId()));
        newComment.setCommentText(requestCommentDTO.getCommentText());
        newComment.setCreationDate(creationDate);
        newComment.setParent(null);

        commentRepository.save(newComment);

        // TODO add ResponseCommentDTO
        return modelMapper.map(newComment, CommentDTO.class);
    }

    @Override
    public CommentDTO addReply(RequestCommentReplyDTO requestCommentReplyDTO) {

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
        newReply.setCommentText(requestCommentReplyDTO.getCommentText());
        newReply.setCreationDate(creationDate);
        newReply.setParent(commentRepository.findById(requestCommentReplyDTO.getParentId()).get());

        commentRepository.save(newReply);

        // TODO add ResponseCommentDTO

        return modelMapper.map(newReply, CommentDTO.class);
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

    @Override
    public List<RequestCommentDTO> getAllCommentsByIdeaId(RequestCommentDTO requestCommentDTO) {
        if (!ideaRepository.existsById(requestCommentDTO.getIdeaId())) {
            throw new RuntimeException();
        }

        List<RequestCommentDTO> filteredList = commentRepository.findAllByIdeaId(requestCommentDTO.getIdeaId()).stream()
                .map(comment -> modelMapper.map(comment, RequestCommentDTO.class))
                .collect(Collectors.toList());

        return filteredList;
    }

    @Override
    public List<RequestCommentReplyDTO> getAllRepliesByCommentId(RequestCommentReplyDTO requestCommentReplyDTO) {
        if (!commentRepository.existsById(requestCommentReplyDTO.getParentId())) {
            throw new RuntimeException();
        }

        List<RequestCommentReplyDTO> filteredList = commentRepository.findById(requestCommentReplyDTO.getParentId()).get().getReplies().stream()
                .map(comment -> modelMapper.map(comment, RequestCommentReplyDTO.class))
                .collect(Collectors.toList());

        return filteredList;
    }

    @Override
    public CommentDTO updateComment(Comment comment) {
        return null;
    }

    @Override
    public void deleteComment(Long id) {

    }
}
