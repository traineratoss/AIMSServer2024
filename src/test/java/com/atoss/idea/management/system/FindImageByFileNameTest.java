package com.atoss.idea.management.system;

import com.atoss.idea.management.system.controller.IdeaController;
import com.atoss.idea.management.system.repository.ImageRepository;
import com.atoss.idea.management.system.repository.entity.Idea;
import com.atoss.idea.management.system.repository.entity.Image;
import com.atoss.idea.management.system.service.implementation.IdeaServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class FindImageByFileNameTest {
    @InjectMocks
    private IdeaServiceImpl ideaServiceImpl;
    private IdeaController ideaController;
    @Mock
    private ImageRepository imageRepository;

    @BeforeEach
    public void setup(){
        MockitoAnnotations.openMocks(this);
        ideaController = new IdeaController(ideaServiceImpl);
    }

    @Test
    public void testFindImageByFileName() {
        Idea idea = new Idea();
        Image image = new Image();
        image.setFileName("test.jpg");

        idea.setImage(image);

        when(imageRepository.findImageByFileName(any(String.class))).thenReturn(image);
        imageRepository.findImageByFileName(image.getFileName());
        verify(imageRepository, times(1)).findImageByFileName("test.jpg");
    }
}
