package com.atoss.idea.management.system.service;

import com.atoss.idea.management.system.exception.ImageNotFoundException;
import com.atoss.idea.management.system.repository.dto.ImageDTO;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;

public interface ImageService {

    /**
     * This method will save the image that is received as a parameter in the database.
     *
     * @param imageFile it is for the image we receive from the client.
     * @return it returns an ImageDTO that represents the added image data.
     * @throws IOException it throws when the I/O operation fails, or it was interrupted.
     */
    ImageDTO addImage(MultipartFile imageFile) throws IOException;

    /**
     * Gets the image that is requested by the id of the image.
     *
     * @param id it is for getting the image by id.
     * @return it returns an ImageDto.
     * @throws ImageNotFoundException if the image we want to get doesn't exist into the database
     *                                it will throw an exception.
     */
     ImageDTO getImage(Long id) throws ImageNotFoundException;

    /**
     * Gets all the images from the database.
     *
     * @return it returns a list of images.
     */
    List<ImageDTO> getAllImage();

}
