package com.atoss.idea.management.system.idea;

import com.atoss.idea.management.system.controller.IdeaController;
import com.atoss.idea.management.system.exception.IdeaNotFoundException;
import com.atoss.idea.management.system.repository.IdeaRepository;
import com.atoss.idea.management.system.repository.UserRepository;
import com.atoss.idea.management.system.repository.entity.Idea;
import com.atoss.idea.management.system.service.implementation.IdeaServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class DeleteIdeaTest {

    @Mock
    public IdeaRepository ideaRepository;

    @InjectMocks
    public IdeaServiceImpl ideaServiceImpl;
    public IdeaController ideaController;

    @Mock
    public UserRepository userRepository;

    @Spy
    public Idea idea;

    @Spy
    public ModelMapper modelMapper;

    @BeforeEach
    public void setup(){
        MockitoAnnotations.openMocks(this);
        ideaController = new IdeaController(ideaServiceImpl);
    }

    @Test
    public void deleteIdeaById_success() {
        Long ideaId = 1L;

        when(ideaRepository.existsById(ideaId)).thenReturn(true);
        doNothing().when(ideaRepository).deleteById(ideaId);

        ResponseEntity<String> response = ideaController.deleteIdeaById(ideaId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Idea successfully deleted", response.getBody());
        verify(ideaRepository, times(1)).deleteById(ideaId);
    }


    @Test
    public void deleteIdeaById_notFound() {
        Long ideaId = 1L;

        when(ideaRepository.existsById(ideaId)).thenReturn(false);

        Exception exception = assertThrows(IdeaNotFoundException.class, () -> {
            ideaServiceImpl.deleteIdeaById(ideaId);
        });

        assertEquals("Idea doesn't exist.", exception.getMessage());
        verify(ideaRepository, never()).deleteById(anyLong());
    }
}
