package com.atoss.idea.management.system.comment;


import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.atoss.idea.management.system.exception.CommentNotFoundException;
import com.atoss.idea.management.system.repository.CommentRepository;
import com.atoss.idea.management.system.repository.dto.ResponseCommentReplyDTO;
import com.atoss.idea.management.system.repository.entity.Comment;
import com.atoss.idea.management.system.repository.entity.User;
import com.atoss.idea.management.system.service.implementation.CommentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public class GetAllRepliesByCommentIdTest {

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private CommentServiceImpl commentServiceImpl;

//    @Spy
//    private ModelMapper modelMapper;

    private Comment replyComment;
    private Comment parentComment;


    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        User user = new User();
        user.setUsername("Helena");

        parentComment = new Comment();
        parentComment.setId(1L);
        parentComment.setUser(user);
        parentComment.setCommentText("Nice idea!");
        parentComment.setCreationDate(new Date());

        replyComment = new Comment();
        replyComment.setId(2L);
        replyComment.setUser(user);
        replyComment.setCommentText("I don't think so");
        replyComment.setParent(parentComment);
        replyComment.setCreationDate(new Date());



    }

    @Test
    public void getAllRepliesByCommentId_Success() {
        Long commentId = 1L;
        Pageable pageable = PageRequest.of(0, 10); //prima pag cu 10 el pe ea

        when(commentRepository.existsById(any(Long.class))).thenReturn(true);
        when(commentRepository.findById(parentComment.getId())).thenReturn(Optional.of(parentComment));
        when(commentRepository.findById(replyComment.getId())).thenReturn(Optional.of(replyComment));
        when(commentRepository.findAllByParentId(commentId, pageable))
                .thenReturn(new PageImpl<>(List.of(replyComment), pageable, 1));

        Page<ResponseCommentReplyDTO> response = commentServiceImpl.getAllRepliesByCommentId(commentId, pageable);

        assertEquals(1, response.getTotalElements());
        ResponseCommentReplyDTO replyDTO = response.getContent().get(0);
        assertEquals("Helena", replyDTO.getUsername());
        assertEquals("I don't think so", replyDTO.getCommentText());
        assertEquals(1L, replyDTO.getParentId());
    }

    @Test
    public void getAllRepliesByCommentId_CommentNotFound() {
        Long commentId = 96789L;
        Pageable pageable = PageRequest.of(0, 10);

        when(commentRepository.existsById(commentId)).thenReturn(false);

        assertThrows(CommentNotFoundException.class, () -> {
            commentServiceImpl.getAllRepliesByCommentId(commentId, pageable);
        });


    }
}
