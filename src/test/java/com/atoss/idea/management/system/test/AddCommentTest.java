package com.atoss.idea.management.system.test;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.atoss.idea.management.system.exception.IdeaNotFoundException;
import com.atoss.idea.management.system.exception.UserNotFoundException;
import com.atoss.idea.management.system.repository.CommentRepository;
import com.atoss.idea.management.system.repository.IdeaRepository;
import com.atoss.idea.management.system.repository.UserRepository;
import com.atoss.idea.management.system.repository.dto.RequestCommentDTO;
import com.atoss.idea.management.system.repository.dto.ResponseCommentDTO;
import com.atoss.idea.management.system.repository.entity.Comment;
import com.atoss.idea.management.system.repository.entity.Idea;
import com.atoss.idea.management.system.repository.entity.User;
import com.atoss.idea.management.system.service.implementation.CommentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.modelmapper.ModelMapper;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Optional;

public class AddCommentTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private IdeaRepository ideaRepository;

    @Mock
    private CommentRepository commentRepository;

    @Spy
    private ModelMapper modelMapper;

    @InjectMocks
    private CommentServiceImpl commentService;


    private RequestCommentDTO requestCommentDTO;
    private User user;
    private Idea idea;
    private Comment comment;

    @BeforeEach
    public void setUp(){
        MockitoAnnotations.openMocks(this);


        requestCommentDTO = new RequestCommentDTO();
        requestCommentDTO.setUsername("Helena");
        requestCommentDTO.setIdeaId(1L);
        requestCommentDTO.setCommentText("Abcdef");

        user = new User();
        user.setUsername("Helena");

        idea = new Idea();
        idea.setId(1L);

        comment = new Comment();
        comment.setUser(user);
        comment.setIdea(idea);
        comment.setCommentText("Abcdef");
        comment.setCreationDate(new Date());
    }

    @Test
    void addComment_Success() throws UnsupportedEncodingException {

        when(userRepository.findByUsername("Helena")).thenReturn(Optional.of(user));
        when(ideaRepository.findById(1L)).thenReturn(Optional.of(idea));

        ResponseCommentDTO responseCommentDTO = commentService.addComment(requestCommentDTO);

        assertEquals("Helena", responseCommentDTO.getUsername());
        assertEquals(1L, responseCommentDTO.getIdeaId());
        assertEquals("Abcdef", responseCommentDTO.getCommentText());

        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @Test
    void addComment_UserNotFound() {
        requestCommentDTO.setUsername("Helena");

        when(userRepository.findByUsername("Helena")).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> {
            commentService.addComment(requestCommentDTO);
        });

        assertEquals("User not found!", exception.getMessage());
    }

    @Test
    void addComment_IdeaNotFound(){
        requestCommentDTO.setIdeaId(1L);

        when(userRepository.findByUsername("Helena")).thenReturn(Optional.of(user));
        when(ideaRepository.findById(1L)).thenReturn(Optional.empty());

        IdeaNotFoundException exception = assertThrows(IdeaNotFoundException.class, () -> {
            commentService.addComment(requestCommentDTO);
        });

        assertEquals("Idea not found!", exception.getMessage());
    }

}
