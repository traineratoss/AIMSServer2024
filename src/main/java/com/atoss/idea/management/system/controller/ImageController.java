package com.atoss.idea.management.system.controller;

import com.atoss.idea.management.system.exception.ImageNotFoundException;
import com.atoss.idea.management.system.repository.dto.ImageDTO;
import com.atoss.idea.management.system.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/aims/api/v1/images")

public class ImageController {
    private final ImageService imageService;

    /**
     * Constructor
     *
     * @param imageService Dependency injection through constructor
     */
    @Autowired
     public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    /**
     * Uploads an image to the database in the form of an ImageDTO object.
     *
     * @param file the multipart file representing the image to be uploaded.
     * @return it returns an ImageDTO that represents the added image.
     * @throws IOException it throws when the I/O operation fails, or it was interrupted.
     */
    @PostMapping("/addImage")
    public ResponseEntity<ImageDTO> addImage(@RequestBody MultipartFile file) throws IOException {
        return new ResponseEntity<>(imageService.addImage(file), HttpStatus.OK);
    }

    /**
     * Gets all the images from the database.
     *
     * @return it returns a response entity with all the images from the database.
     */
    @GetMapping
    public ResponseEntity<List<ImageDTO>> getAllImages() {
        return new ResponseEntity<>(imageService.getAllImage(), HttpStatus.OK);
    }

    /**
     * Gets a image from the database by the provided image id
     *
     * @param id it is for getting the image by id.
     * @return it returns a response entity with the selected image by the id of the image.
     * @throws ImageNotFoundException if the image we want to get doesn't exist into the database
     *                                it will throw an exception
     */
    @GetMapping("/get")
    public ResponseEntity<ImageDTO> getImage(@RequestParam Long id) throws ImageNotFoundException {
        return new ResponseEntity<>(imageService.getImage(id), HttpStatus.OK);
    }

    /**
     *  Gets an image by an idea id
     *
     * @param id the id of the idea
     * @return it returns a response entity with the image.
     */
    @GetMapping("/getByIdea")
    public ResponseEntity<ImageDTO> getImageByIdeaId(@RequestParam Long id) {
        return new ResponseEntity<>(imageService.getImageByIdeaId(id), HttpStatus.OK);
    }

}
