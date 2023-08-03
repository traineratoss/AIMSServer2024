package com.atoss.idea.management.system.controller;

import com.atoss.idea.management.system.exception.ImageNotFoundException;
import com.atoss.idea.management.system.repository.dto.ImageDTO;
import com.atoss.idea.management.system.service.implementation.ImageServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/aims/api/v1/images")

public class ImageController {
    private final ImageServiceImpl imageService;

    @Autowired
     public ImageController(ImageServiceImpl imageService) {
        this.imageService = imageService;
    }

    @PostMapping("/addImage")
    public ImageDTO addImage(@RequestBody MultipartFile file) throws IOException {
        return imageService.addImage(file);

    }

    @GetMapping
    public List<ImageDTO> getAllImages() {
        return imageService.getAllImage();
    }

    @GetMapping("/{id}")
    public ImageDTO getImage(@PathVariable("id") Long id) throws ImageNotFoundException {
        return imageService.getImage(id);
    }

}
