package com.atoss.idea.management.system.comment;

import com.atoss.idea.management.system.exception.CommentNotFoundException;
import com.atoss.idea.management.system.exception.IdeaNotFoundException;
import com.atoss.idea.management.system.repository.*;
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
import org.springframework.data.domain.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class GetAllPagedCommentsByIdeaIdTest {

    @Mock
    private  CommentRepository commentRepository;
    @Spy
    private  IdeaRepository ideaRepository;
    @Spy
    private  UserRepository userRepository;
    @Spy
    private  ModelMapper modelMapper;
    @InjectMocks
    private CommentServiceImpl commentService;
    private Idea idea;



    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetAllPagedCommentsByIdeaId() {
        Date data=new Date("29/06/2024");

        User user=new User();
        user.setUsername("Irina");

        Comment comment3=new Comment();
        comment3.setId(3L);
        comment3.setUser(user);
        comment3.setIdea(idea);
        comment3.setCommentText("comment3-text");
        comment3.setCreationDate(data);


        List <Comment> replyList=new ArrayList<>();
        replyList.add(comment3);

        Comment comment1=new Comment();
        comment1.setId(1L);
        comment1.setUser(user);
        comment1.setIdea(idea);
        comment1.setCommentText("comment1-text");
        comment1.setCreationDate(data);
        comment1.setReplies(replyList);


        Comment comment2=new Comment();
        comment2.setId(2L);
        comment2.setUser(user);
        comment2.setIdea(idea);
        comment2.setCommentText("comment2-text");
        comment2.setCreationDate(data);
        comment2.setReplies(replyList);

        Long ideaId=1L;
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "creationDate"));


        List<Comment> commentList=new ArrayList<>();
        commentList.add(comment1);
        commentList.add(comment2);

        Page<Comment> commentPage = new PageImpl<>(commentList);


        when(ideaRepository.existsById(ideaId)).thenReturn(true);
        when(commentRepository.findAllByIdeaId(any(Long.class),any(Pageable.class))).thenReturn(commentPage);
        when(commentRepository.findById(any(Long.class))).thenReturn(Optional.of(comment1));


        Page<ResponseCommentDTO> resultPage = commentService.getAllPagedCommentsByIdeaId(ideaId, pageable);


        assertEquals(2, resultPage.getContent().size());

        assertEquals(comment1.getCommentText(), resultPage.getContent().get(0).getCommentText());
        assertEquals(comment2.getUser().getUsername(), resultPage.getContent().get(1).getUsername());

    }


    @Test
    public void testGetElapsedTime() {

        Date currentDate = new Date();
        Date newDate=new Date();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);

        newDate.setTime(currentDate.getTime()-1000);//1 sec
        String resultData=commentService.getElapsedTime(newDate);
        assertEquals("1 second",resultData);

        newDate.setTime(currentDate.getTime()-20000);//20 sec
        resultData=commentService.getElapsedTime(newDate);
        assertEquals("20 seconds",resultData);

        newDate.setTime(currentDate.getTime()-60000);//1 min
        resultData=commentService.getElapsedTime(newDate);
        assertEquals("1 minute",resultData);

        newDate.setTime(currentDate.getTime()-300000);//5 min
        resultData=commentService.getElapsedTime(newDate);
        assertEquals("5 minutes",resultData);

        calendar.add(Calendar.HOUR_OF_DAY, -1);
        newDate = calendar.getTime();
        resultData=commentService.getElapsedTime(newDate);
        assertEquals("1 hour",resultData);

        calendar.add(Calendar.HOUR_OF_DAY, -3); //because the TimeUnit.MILLISECONDS.toHours function approximates by rounding up
        newDate = calendar.getTime();
        resultData=commentService.getElapsedTime(newDate);
        assertEquals("4 hours",resultData);

        calendar.add(Calendar.MONTH, -1);
        newDate = calendar.getTime();
        resultData=commentService.getElapsedTime(newDate);
        assertEquals("1 month",resultData);

        calendar.add(Calendar.MONTH, -4);
        newDate = calendar.getTime();
        resultData=commentService.getElapsedTime(newDate);
        assertEquals("5 months",resultData);

        calendar.add(Calendar.YEAR, -1);
        newDate = calendar.getTime();
        resultData=commentService.getElapsedTime(newDate);
        assertEquals("1 year",resultData);

        calendar.add(Calendar.YEAR, -6);
        newDate = calendar.getTime();
        resultData=commentService.getElapsedTime(newDate);
        assertEquals("7 years",resultData);

    }

    @Test
    public void testGetTimeForComment() {
        Date currentDate = new Date();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);

        calendar.add(Calendar.HOUR_OF_DAY, -1);

        Date oneHourBefore = calendar.getTime();

        assertEquals("1 hour",commentService.getElapsedTime(oneHourBefore));

    }


    @Test
    public void testIdeaNotFoundExceptionGetComments() {

        Long ideaId=1L;
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "creationDate"));
        when(ideaRepository.existsById(ideaId)).thenReturn(false);

        assertThrows(IdeaNotFoundException.class, ()->{
            commentService.getAllPagedCommentsByIdeaId(ideaId, pageable);
        });

    }

    @Test
    public void testCommentNotFoundExceptionGetComments() {

      when(commentRepository.findById(anyLong())).thenReturn(Optional.empty());
      assertThrows(CommentNotFoundException.class, ()->{
            commentService.getTimeForComment(2L);
      });
    }




}
