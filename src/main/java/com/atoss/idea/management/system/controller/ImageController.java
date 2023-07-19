package com.atoss.idea.management.system.controller;

import com.atoss.idea.management.system.repository.entity.Image;
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
import java.util.stream.Collectors;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/")
public class ImageController {
    private final ImageServiceImpl imageServiceImpl;

    @Autowired
     public ImageController(ImageServiceImpl imageServiceImpl) {
        this.imageServiceImpl = imageServiceImpl;

    }

    @PostMapping("/images")
    public  String addImage(@RequestParam("file") MultipartFile file) {
        try {
            imageServiceImpl.addImage(file);
            return "Upload" + file.getOriginalFilename();
        } catch (IOException e) {

            return "ceva e gresit";
        }

    }


    @GetMapping("/images")
    public List<Image> getAllImages() {
        return imageServiceImpl.getAllImage().collect(Collectors.toList());
    }


    @GetMapping("/images/{id}")
    public byte[] getImage(@PathVariable String id) {
        Image image = imageServiceImpl.getImage(id);
        return image != null ? image.getImage() : null;
    }

}
