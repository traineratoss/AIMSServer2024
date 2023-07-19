package com.atoss.idea.management.system.service.implementation;

import com.atoss.idea.management.system.repository.ImageRepository;
import com.atoss.idea.management.system.repository.entity.Image;
import com.atoss.idea.management.system.service.ImageService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


@Service
public class ImageServiceImpl implements ImageService {

    public final ImageRepository imageRepository;
    public final ModelMapper modelMapper;

    public ImageServiceImpl(ImageRepository imageRepository, ModelMapper modelMapper) {
        this.imageRepository = imageRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public Image addImage(MultipartFile imageFile) throws Exception {
        return null;
    }
}

