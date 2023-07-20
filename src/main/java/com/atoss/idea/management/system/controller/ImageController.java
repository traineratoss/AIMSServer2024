package com.atoss.idea.management.system.controller;

import com.atoss.idea.management.system.repository.dto.ImageDTO;
import com.atoss.idea.management.system.service.implementation.ImageServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.GetMapping;
import java.io.IOException;
import java.util.List;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/images")
public class ImageController {
    private final ImageServiceImpl imageServiceImpl;

    @Autowired
     public ImageController(ImageServiceImpl imageServiceImpl) {
        this.imageServiceImpl = imageServiceImpl;

    }

    @PostMapping("/addImage")
    public ImageDTO addImage(@RequestParam("file") MultipartFile file) throws IOException {
        return imageServiceImpl.addImage(file);

    }


    @GetMapping
    public List<ImageDTO> getAllImages() {
        return imageServiceImpl.getAllImage();
    }


    @GetMapping("/{id}")
    public ImageDTO getImage(@PathVariable("id") Long id) throws Exception {
        return imageServiceImpl.getImage(id);
    }

}
