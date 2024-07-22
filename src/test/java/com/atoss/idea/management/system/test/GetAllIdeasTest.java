package com.atoss.idea.management.system.test;

import com.atoss.idea.management.system.controller.IdeaController;
import com.atoss.idea.management.system.exception.FieldValidationException;
import com.atoss.idea.management.system.repository.IdeaRepository;
import com.atoss.idea.management.system.repository.dto.IdeaResponseDTO;
import com.atoss.idea.management.system.repository.entity.Comment;
import com.atoss.idea.management.system.repository.entity.Idea;
import com.atoss.idea.management.system.repository.entity.User;
import com.atoss.idea.management.system.service.implementation.CommentServiceImpl;
import com.atoss.idea.management.system.service.implementation.IdeaServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class GetAllIdeasTest {

    @Mock
    private IdeaRepository ideaRepository;
    @InjectMocks
    private IdeaServiceImpl ideaServiceImpl;

    @Mock
    private CommentServiceImpl commentServiceImpl;
    private IdeaController ideaController;

    @Spy
    public ModelMapper modelMapper;


    @BeforeEach
    public void setup(){
        MockitoAnnotations.openMocks(this);
        ideaController = new IdeaController(ideaServiceImpl);
    }

    @Test
    public void testGetAllIdeas() throws ParseException {
        User user = new User();
        user.setUsername("user12");

        Date date = new Date();

        Idea idea = new Idea();
        idea.setUser(user);
        idea.setCreationDate(date);

        Comment comment1 = new Comment();
        Comment comment2 = new Comment();
        comment1.setCommentText("asfasfasf");
        comment2.setCommentText("asfasfasfasfasa");
        List<Comment> commentList = Arrays.asList(comment1, comment2);

        Page<Idea> ideaPage = new PageImpl<>(Arrays.asList(idea));
        idea.setCommentList(commentList);
        when(ideaRepository.findAll()).thenReturn(Arrays.asList(idea));
        when(ideaRepository.findAll(PageRequest.of(0, 10))).thenReturn(ideaPage);
        when(commentServiceImpl.getElapsedTime(any(Date.class))).thenReturn("1 day elapsed");

        Page<IdeaResponseDTO> result = ideaServiceImpl.getAllIdeas(PageRequest.of(0, 10));

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        IdeaResponseDTO dto = result.getContent().get(0);
        assertEquals("user12", dto.getUsername());
        assertEquals("1 day elapsed", dto.getElapsedTime());
        assertEquals(2, dto.getCommentsNumber());
    }

    @Test
    public void getAllIdeasException(){
        User user = new User();
        Idea idea = new Idea();
        idea.setUser(user);

        Page<Idea> ideaPage = new PageImpl<>(Arrays.asList(idea));

        when(ideaRepository.findAll()).thenReturn(new ArrayList<>());

        assertThatThrownBy(() -> ideaServiceImpl.getAllIdeas(PageRequest.of(0, 10)))
                .isInstanceOf(FieldValidationException.class)
                .hasMessage("No ideas found.");
    }
}
