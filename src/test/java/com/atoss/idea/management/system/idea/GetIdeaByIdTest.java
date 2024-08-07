package com.atoss.idea.management.system.idea;

import com.atoss.idea.management.system.exception.FieldValidationException;
import com.atoss.idea.management.system.exception.IdeaNotFoundException;
import com.atoss.idea.management.system.repository.IdeaRepository;
import com.atoss.idea.management.system.repository.dto.IdeaResponseDTO;
import com.atoss.idea.management.system.repository.entity.Idea;
import com.atoss.idea.management.system.repository.entity.User;
import com.atoss.idea.management.system.service.implementation.CommentServiceImpl;
import com.atoss.idea.management.system.service.implementation.IdeaServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.modelmapper.ModelMapper;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Optional;
import static org.mockito.Mockito.*;

public class GetIdeaByIdTest {

    @Mock
    private IdeaRepository ideaRepository;

    @InjectMocks
    public IdeaServiceImpl ideaService;

    //private IdeaController ideaController;

    private Idea idea;

    @Spy
    private ModelMapper modelMapper;

    private IdeaResponseDTO ideaResponseDTO;

    @Mock
    private CommentServiceImpl commentService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        //ideaController = new IdeaController(ideaService);
    }

    @Test
    public void getIdeaTest() throws FieldValidationException {
        User user = new User();
        user.setUsername("andrei");

        idea = new Idea();
        idea.setId(100L);
        idea.setUser(user);
        idea.setCommentList(new ArrayList<>());


        when(ideaRepository.findById(100L)).thenReturn(Optional.of(idea));
        IdeaResponseDTO result = ideaService.getIdeaById(100L);
        //assertEquals(idea.getId(), 100L );
        assertEquals(result.getUsername(), "andrei");
        assertEquals(result.getCommentsNumber(), 0);

    }

    @Test
    public void ideaDoesnotExistTest() throws FieldValidationException {

        when(ideaRepository.findById(100L)).thenReturn(Optional.empty());

        Exception exception = Assertions.assertThrows(IdeaNotFoundException.class, () -> {
            ideaService.getIdeaById(100L);
        });

        assertEquals("Idea doesn't exist.", exception.getMessage());
    }


}
