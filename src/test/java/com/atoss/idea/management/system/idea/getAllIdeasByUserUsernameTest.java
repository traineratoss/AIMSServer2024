package com.atoss.idea.management.system.idea;

import com.atoss.idea.management.system.controller.IdeaController;
import com.atoss.idea.management.system.exception.FieldValidationException;
import com.atoss.idea.management.system.exception.UserNotFoundException;
import com.atoss.idea.management.system.repository.IdeaRepository;
import com.atoss.idea.management.system.repository.UserRepository;
import com.atoss.idea.management.system.repository.dto.IdeaResponseDTO;
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
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

public class getAllIdeasByUserUsernameTest {

    @Mock
    private IdeaRepository ideaRepository;
    @InjectMocks
    private IdeaServiceImpl ideaServiceImpl;

    @Mock
    private CommentServiceImpl commentServiceImpl;

    private IdeaController ideaController;

    @Mock
    private UserRepository userRepository;

    @Spy
    public ModelMapper modelMapper;


    @BeforeEach
    public void setup(){
        MockitoAnnotations.openMocks(this);
        ideaController = new IdeaController(ideaServiceImpl);
    }

    @Test
    public void getAllIdeasByUserUsername_UserNotFoundException() {
        when(userRepository.findByUsername(any(String.class))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> ideaServiceImpl.getAllIdeasByUserUsername("user12", Pageable.unpaged()))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("User doesn't exist.");

        verify(userRepository, times(1)).findByUsername(any(String.class));
    }

    @Test
    public void getAllIdeasByUserUsernameException() {
        User user = new User();
        user.setIdeas(Collections.emptyList());

        when(userRepository.findByUsername(any(String.class))).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> ideaServiceImpl.getAllIdeasByUserUsername("user12", Pageable.unpaged()))
                .isInstanceOf(FieldValidationException.class)
                .hasMessage("No ideas found.");

        verify(userRepository, times(1)).findByUsername(any(String.class));
        verify(ideaRepository, never()).findAllByUserUsername(any(String.class), any(Pageable.class));
    }

    @Test
    public void getAllIdeasByUserUsername() {
        User user = new User();
        user.setUsername("user12");

        Idea idea = new Idea();
        idea.setCreationDate(new Date());
        idea.setCommentList(Collections.emptyList());

        user.setIdeas(List.of(idea));

        IdeaResponseDTO responseDTO = new IdeaResponseDTO();
        responseDTO.setUsername("user12");
        responseDTO.setElapsedTime("5 minutes ago");
        responseDTO.setCommentsNumber(0);

        when(userRepository.findByUsername(any(String.class))).thenReturn(Optional.of(user));
        when(ideaRepository.findAllByUserUsername(any(String.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(idea)));
        when(commentServiceImpl.getElapsedTime(any(Date.class))).thenReturn("5 minutes ago");

        Page<IdeaResponseDTO> result = ideaServiceImpl.getAllIdeasByUserUsername("user12", Pageable.unpaged());

        assertNotNull(result);
        assertThat(result.getContent().get(0).getUsername()).isEqualTo("user12");
        assertThat(result.getContent().get(0).getElapsedTime()).isEqualTo("5 minutes ago");
        assertThat(result.getContent().get(0).getCommentsNumber()).isEqualTo(0);

        verify(userRepository, times(1)).findByUsername(any(String.class));
        verify(ideaRepository, times(1)).findAllByUserUsername(any(String.class), any(Pageable.class));
        verify(modelMapper, times(1)).map(any(Idea.class), eq(IdeaResponseDTO.class));
        verify(commentServiceImpl, times(1)).getElapsedTime(any(Date.class));
    }
}
