package com.atoss.idea.management.system.service.implementation;

import com.atoss.idea.management.system.exception.ValidationException;
import com.atoss.idea.management.system.repository.entity.Image;
import com.atoss.idea.management.system.service.ImageService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ImageServiceImpl implements ImageService {
    @Override
    public Image addImage(Image image) throws ValidationException {
        return null;
    }

    @Override
    public Image getImageById(Long id) throws ValidationException {
        return null;
    }

    @Override
    public Image updateImageById(Image image) throws ValidationException {
        return null;
    }

    @Override
    public void deleteImageById(Long id) throws ValidationException {

    }

    @Override
    public List<Image> getAllImages() throws ValidationException {
        return null;
    }
}
