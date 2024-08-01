package com.atoss.idea.management.system.comment;

import com.atoss.idea.management.system.exception.CommentNotFoundException;
import com.atoss.idea.management.system.exception.UserNotFoundException;
import com.atoss.idea.management.system.repository.CommentRepository;
import com.atoss.idea.management.system.repository.UserRepository;
import com.atoss.idea.management.system.repository.dto.RequestCommentReplyDTO;
import com.atoss.idea.management.system.repository.entity.Comment;
import com.atoss.idea.management.system.repository.entity.User;
import com.atoss.idea.management.system.service.implementation.CommentServiceImpl;
import com.atoss.idea.management.system.service.implementation.HtmlServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.modelmapper.ModelMapper;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class AddReplyTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CommentServiceImpl commentService;

    @Spy
    private ModelMapper modelMapper;
    private RequestCommentReplyDTO requestCommentReplyDTO;

    private User user;

    private Comment comment;

    @Mock
    private HtmlServiceImpl htmlService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void AddReplyTestSuccess() {
        user = new User();
        user.setUsername("Gasdasdas");
        comment = new Comment();
        comment.setUser(user);
        comment.setCommentText("Commentasdas");

        requestCommentReplyDTO = new RequestCommentReplyDTO();
        requestCommentReplyDTO.setUsername(user.getUsername());
        requestCommentReplyDTO.setCommentText(comment.getCommentText());
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
        when(commentRepository.findById(requestCommentReplyDTO.getParentId())).thenReturn(Optional.of(comment));

        doReturn(true).when(commentRepository).existsById(requestCommentReplyDTO.getParentId());
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);


        commentService.addReply(requestCommentReplyDTO);

        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @Test
    public void AddReplyTestUserNotFound() {
        requestCommentReplyDTO = new RequestCommentReplyDTO();
        requestCommentReplyDTO.setUsername("Vlad");
        requestCommentReplyDTO.setCommentText("Comment");
        when(userRepository.findByUsername(requestCommentReplyDTO.getUsername())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> {
            commentService.addReply(requestCommentReplyDTO);
        });

        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    public void AddReplyTestCommentNotFound()
    {
        User user = new User();
        user.setUsername("Gasdasdas");
        requestCommentReplyDTO = new RequestCommentReplyDTO();
        requestCommentReplyDTO.setUsername(user.getUsername());
        requestCommentReplyDTO.setCommentText("Comment");
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
        doReturn(false).when(commentRepository).existsById(requestCommentReplyDTO.getParentId());
        assertThrows(CommentNotFoundException.class, () -> {
            commentService.addReply(requestCommentReplyDTO);
        });

        verify(commentRepository, never()).save(any(Comment.class));
    }

}