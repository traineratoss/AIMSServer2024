package com.atoss.idea.management.system.controller;

import com.atoss.idea.management.system.exception.ValidationException;
import com.atoss.idea.management.system.repository.entity.Image;
import com.atoss.idea.management.system.service.ImageService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;
import java.util.List;

@RestController
public class ImageController {

    private final ImageService imageService;

    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    @PostMapping("/idea/")
    public Image addImage(@RequestBody Image image) throws ValidationException {
        return imageService.addImage(image);
    }

    @GetMapping("/idea/{id}")
    public Image getImageById(@PathVariable("id") Long id) throws ValidationException {
        return imageService.getImageById(id);
    }

    @PutMapping("/idea/")
    public Image updateImageById(@RequestBody Image image) throws ValidationException {
        return imageService.updateImageById(image);
    }

    @DeleteMapping("/idea/{id}")
    public void deleteImage(@PathVariable("id") Long id) throws ValidationException {
        imageService.deleteImageById(id);
    }

    @GetMapping("/idea/all")
    public List<Image> getAllIdeas() throws ValidationException {
        return imageService.getAllImages();
    }
}
