package com.atoss.idea.management.system.idea;

import com.atoss.idea.management.system.controller.IdeaController;
import com.atoss.idea.management.system.exception.FieldValidationException;
import com.atoss.idea.management.system.repository.CategoryRepository;
import com.atoss.idea.management.system.repository.IdeaRepository;
import com.atoss.idea.management.system.repository.ImageRepository;
import com.atoss.idea.management.system.repository.UserRepository;
import com.atoss.idea.management.system.repository.dto.CategoryDTO;
import com.atoss.idea.management.system.repository.dto.IdeaRequestDTO;
import com.atoss.idea.management.system.repository.dto.IdeaResponseDTO;
import com.atoss.idea.management.system.repository.dto.ImageDTO;
import com.atoss.idea.management.system.repository.entity.*;
import com.atoss.idea.management.system.service.implementation.IdeaServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;

import java.io.UnsupportedEncodingException;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class CreateIdeaTest {
    @Mock
    private IdeaRepository ideaRepository;
    @InjectMocks
    private IdeaServiceImpl ideaServiceImpl;

    @Mock
    private CategoryRepository categoryRepository;
    private IdeaController ideaController;
    private IdeaRequestDTO ideaRequestDTO;
    @Mock
    private UserRepository userRepository;

    @Spy
    private ModelMapper modelMapper;

    @Mock
    private ImageRepository imageRepository;


    @BeforeEach
    public void setup(){
        MockitoAnnotations.openMocks(this);
        ideaController = new IdeaController(ideaServiceImpl);
    }

    @Test
    public void createIdea() throws UnsupportedEncodingException {
        CategoryDTO category = new CategoryDTO();
        category.setText("Nature");
        List<CategoryDTO> categoryList = Arrays.asList(category);

        ideaRequestDTO = new IdeaRequestDTO();
        ideaRequestDTO.setTitle("Idea First");
        ideaRequestDTO.setStatus(Status.OPEN);
        ideaRequestDTO.setText("asasfajshf");
        ideaRequestDTO.setCategoryList(categoryList);

        User user = new User();
        Idea idea = new Idea();
        user.setUsername("user12");

        Image image = new Image();
        image.setFileName("file.png");
        idea.setImage(image);

        when(ideaRepository.save(any(Idea.class))).thenReturn(idea);
        when(userRepository.findByUsername(any(String.class))).thenReturn(Optional.of(user));

        ResponseEntity<IdeaResponseDTO> idea2 = ideaController.addIdea(ideaRequestDTO, "user12");
        assertEquals(ideaRequestDTO.getTitle(), idea2.getBody().getTitle());
    }

    @Test
    public void FieldValidationExceptionTest() throws UnsupportedEncodingException {
        ideaRequestDTO = new IdeaRequestDTO();
        Idea idea = new Idea();

        when(ideaRepository.save(any(Idea.class))).thenReturn(idea);

        assertThatThrownBy(() -> ideaController.addIdea(ideaRequestDTO, "user12"))
                .isInstanceOf(FieldValidationException.class)
                .hasMessage("Please enter a valid title for the idea.");

        ideaRequestDTO.setTitle("Idea1");

        assertThatThrownBy(() -> ideaController.addIdea(ideaRequestDTO, "user12"))
                .isInstanceOf(FieldValidationException.class)
                .hasMessage("Please enter a valid status for the idea.");

        ideaRequestDTO.setStatus(Status.OPEN);

        assertThatThrownBy(() -> ideaController.addIdea(ideaRequestDTO, "user12"))
                .isInstanceOf(FieldValidationException.class)
                .hasMessage("Please enter a valid category for the idea.");

        CategoryDTO category = new CategoryDTO();
        category.setText("Nature");
        List<CategoryDTO> categoryList = Arrays.asList(category);

        ideaRequestDTO.setCategoryList(categoryList);

        assertThatThrownBy(() -> ideaController.addIdea(ideaRequestDTO, "user12"))
                .isInstanceOf(FieldValidationException.class)
                .hasMessage("Please enter a valid text for the idea.");
    }


    @Test
    public void testAddIdeaWithImage() throws UnsupportedEncodingException {
        CategoryDTO category = new CategoryDTO();
        category.setText("Nature");
        List<CategoryDTO> categoryList = Arrays.asList(category);

        ideaRequestDTO = new IdeaRequestDTO();
        ideaRequestDTO.setTitle("Idea First");
        ideaRequestDTO.setStatus(Status.OPEN);
        ideaRequestDTO.setText("asasfajshf");
        ideaRequestDTO.setCategoryList(categoryList);

        ImageDTO imageDTO = new ImageDTO();
        imageDTO.setFileName("file.png");
        ideaRequestDTO.setImage(imageDTO);

        User user = new User();
        user.setUsername("user12");

        Idea idea = new Idea();
        idea.setTitle(ideaRequestDTO.getTitle());
        idea.setStatus(ideaRequestDTO.getStatus());
        idea.setText(ideaRequestDTO.getText());
        idea.setCategoryList(new ArrayList<>());
        idea.setCreationDate(new Date());

        when(userRepository.findByUsername(any(String.class))).thenReturn(Optional.of(user));
        when(imageRepository.findImageByFileName(any(String.class))).thenReturn(null);
        when(ideaRepository.save(any(Idea.class))).thenReturn(idea);

        ResponseEntity<IdeaResponseDTO> response = ideaController.addIdea(ideaRequestDTO, "user12");

        verify(imageRepository, times(1)).findImageByFileName(imageDTO.getFileName());
        verify(ideaRepository, times(1)).save(any(Idea.class));
        assertEquals(ideaRequestDTO.getTitle(), response.getBody().getTitle());

        Image existingImage = new Image();
        existingImage.setFileName("file.png");
        when(imageRepository.findImageByFileName(any(String.class))).thenReturn(existingImage);

        response = ideaController.addIdea(ideaRequestDTO, "user12");

        verify(imageRepository, times(2)).findImageByFileName(imageDTO.getFileName());
        verify(ideaRepository, times(2)).save(any(Idea.class));
        assertEquals(ideaRequestDTO.getTitle(), response.getBody().getTitle());
        assertEquals(existingImage.getFileName(), response.getBody().getImage().getFileName());
    }






}
