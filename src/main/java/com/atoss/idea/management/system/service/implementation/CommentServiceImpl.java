package com.atoss.idea.management.system.service.implementation;

import com.atoss.idea.management.system.repository.CommentRepository;
import com.atoss.idea.management.system.repository.IdeaRepository;
import com.atoss.idea.management.system.repository.UserRepository;
import com.atoss.idea.management.system.repository.dto.CommentDTO;
import com.atoss.idea.management.system.repository.entity.Comment;
import com.atoss.idea.management.system.repository.entity.Idea;
import com.atoss.idea.management.system.service.CommentService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
    public CommentDTO addComment(Comment comment, Long ideaId) {
        if (!userRepository.existsById(comment.getUser().getUserId())) {
            throw new RuntimeException();
        }

        if (!ideaRepository.existsById(ideaId)) {
            throw new RuntimeException();
        }

        Optional<Idea> ideaOptional = ideaRepository.findById(ideaId);

        Idea idea = ideaOptional.orElse(null);

        comment.setIdea(idea);

        commentRepository.save(comment);

        return modelMapper.map(comment, CommentDTO.class);
    }

    @Override
    public CommentDTO addReply(Comment reply, Long ideaId, Long commentId) {

        Optional<Idea> ideaOptional = ideaRepository.findById(ideaId);

        Idea idea = ideaOptional.orElse(null);

        reply.setIdea(idea);

        Optional<Comment> commentOptional = commentRepository.findById(commentId);

        Comment comment = commentOptional.orElse(null);

        reply.setParent(comment);

        commentRepository.save(reply);

        return modelMapper.map(reply, CommentDTO.class);
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
    public List<CommentDTO> getAllCommentsByIdeaId(Long ideaId) {
        if (!ideaRepository.existsById(ideaId)) {
            throw new RuntimeException();
        }

        List<CommentDTO> filteredList = commentRepository.findAllByIdeaId(ideaId).stream()
                .map(comment -> modelMapper.map(comment, CommentDTO.class))
                .collect(Collectors.toList());

        return (ArrayList<CommentDTO>) filteredList;
    }

    @Override
    public List<CommentDTO> getAllRepliesByCommentId(Long ideaId, Long commentId) {
        if (!commentRepository.existsById(commentId)) {
            throw new RuntimeException();
        }

        List<CommentDTO> filteredList = commentRepository.findAllByIdeaId(ideaId).stream()
                .filter(comment -> comment.getParent().getId() != null)
                .map(comment -> modelMapper.map(comment, CommentDTO.class))
                .collect(Collectors.toList());

        return (ArrayList<CommentDTO>) filteredList;
    }

    @Override
    public CommentDTO updateComment(Comment comment) {
        return null;
    }

    @Override
    public void deleteComment(Long id) {

    }
}
