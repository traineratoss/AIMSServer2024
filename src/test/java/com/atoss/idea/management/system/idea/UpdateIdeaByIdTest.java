package com.atoss.idea.management.system.idea;

import com.atoss.idea.management.system.controller.IdeaController;
import com.atoss.idea.management.system.repository.*;
import com.atoss.idea.management.system.repository.dto.CategoryDTO;
import com.atoss.idea.management.system.repository.dto.IdeaResponseDTO;
import com.atoss.idea.management.system.repository.dto.IdeaUpdateDTO;
import com.atoss.idea.management.system.repository.entity.*;
import com.atoss.idea.management.system.service.implementation.HtmlServiceImpl;
import com.atoss.idea.management.system.service.implementation.IdeaServiceImpl;
import com.atoss.idea.management.system.service.implementation.CommentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.UnsupportedEncodingException;
import java.util.*;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class UpdateIdeaByIdTest {
    @Mock
    public IdeaRepository ideaRepository;
    @InjectMocks
    public IdeaServiceImpl ideaServiceImpl;
    public IdeaController ideaController;

    @Mock
    public CommentServiceImpl commentServiceImpl;

    @Mock
    public CategoryRepository categoryRepository;

    @Mock
    public ImageRepository imageRepository;

    @Spy
    public ModelMapper modelMapper;

    @Mock
    public SubscriptionRepository subscriptionRepository;

    @Mock
    private HtmlServiceImpl htmlService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        ideaController = new IdeaController(ideaServiceImpl);
    }

    @Test
    public void updateIdeaById_success() throws UnsupportedEncodingException {
        Long ideaId = 1L;

        Idea idea = new Idea();
        idea.setId(ideaId);
        User user = new User();
        user.setUsername("user12");
        idea.setUser(user);
        idea.setCommentList(List.of());
        idea.setCreationDate(new Date());

        IdeaUpdateDTO ideaUpdateDTO = new IdeaUpdateDTO();
        ideaUpdateDTO.setText("Updated text");
        ideaUpdateDTO.setTitle("Updated title");
        ideaUpdateDTO.setStatus(Status.OPEN);
        ideaUpdateDTO.setCategoryList(Collections.singletonList(new CategoryDTO()));

        when(ideaRepository.findById(ideaId)).thenReturn(Optional.of(idea));
        when(ideaRepository.save(any(Idea.class))).thenReturn(idea);
        when(categoryRepository.findByText(any(String.class))).thenReturn(null);
        when(commentServiceImpl.getElapsedTime(any(Date.class))).thenReturn("5 minutes");

        ResponseEntity<IdeaResponseDTO> response = ideaController.updateIdeaById(ideaId, ideaUpdateDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        IdeaResponseDTO responseDTO = response.getBody();
        assertNotNull(responseDTO);
        assertEquals("Updated text", responseDTO.getText());
        assertEquals("Updated title", responseDTO.getTitle());
        assertEquals(Status.OPEN, responseDTO.getStatus());
        assertEquals("user12", responseDTO.getUsername());
        assertEquals(0, responseDTO.getCommentsNumber());
    }

}
