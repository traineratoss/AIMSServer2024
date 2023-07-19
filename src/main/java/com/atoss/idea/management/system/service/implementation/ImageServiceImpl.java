package com.atoss.idea.management.system.service.implementation;

import com.atoss.idea.management.system.repository.ImageRepository;
import com.atoss.idea.management.system.repository.entity.Image;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.stream.Stream;

@Service
public class ImageServiceImpl {
    private final ImageRepository imageRepository;

    public  ImageServiceImpl(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;

    }

    public Image addImage(MultipartFile file) throws IOException {
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        Image image = new Image(fileName, file.getContentType(), file.getBytes());
        return imageRepository.save(image);
    }

    public Image getImage(String id) {
        return imageRepository.findById(Long.valueOf(id)).orElse(null);
    }

    public Stream<Image> getAllImage() {
        return imageRepository.findAll().stream();
    }

}
