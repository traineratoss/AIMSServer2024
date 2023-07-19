package com.atoss.idea.management.system.service;

import com.atoss.idea.management.system.exception.ValidationException;
import com.atoss.idea.management.system.repository.entity.Image;
import java.util.List;

public interface ImageService {

    Image addImage(Image image) throws ValidationException;

    Image getImageById(Long id) throws ValidationException;

    Image updateImageById(Image image) throws ValidationException;

    void deleteImageById(Long id) throws ValidationException;

    List<Image> getAllImages() throws ValidationException;
}
