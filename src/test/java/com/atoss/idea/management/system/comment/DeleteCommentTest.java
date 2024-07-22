package com.atoss.idea.management.system.comment;

import com.atoss.idea.management.system.exception.CommentNotFoundException;
import com.atoss.idea.management.system.repository.CommentRepository;
import com.atoss.idea.management.system.repository.entity.Comment;
import com.atoss.idea.management.system.service.implementation.CommentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

public class DeleteCommentTest {

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private CommentServiceImpl commentService;

    private Comment comment;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void DeleteCommentTestSucces()
    {
        comment=new Comment();
        comment.setId(1L);
        doReturn(true).when(commentRepository).existsById(comment.getId());
        commentService.deleteComment(comment.getId());
        verify(commentRepository, times(1)).deleteById(any(Long.class));

    }

    @Test
    public void DeleteCommentTestCommentNotFound()
    {
        comment=new Comment();
        comment.setId(1L);
        doReturn(false).when(commentRepository).existsById(comment.getId());
        assertThrows(CommentNotFoundException.class, () -> {
            commentService.deleteComment(comment.getId());
        });

        verify(commentRepository, never()).deleteById(comment.getId());
    }


}
