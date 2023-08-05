package com.atoss.idea.management.system.service.implementation;

import com.atoss.idea.management.system.exception.ImageNotFoundException;
import com.atoss.idea.management.system.repository.ImageRepository;
import com.atoss.idea.management.system.repository.dto.ImageDTO;
import com.atoss.idea.management.system.repository.entity.Image;
import com.atoss.idea.management.system.service.ImageService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Service
public class ImageServiceImpl implements ImageService {
    private final ImageRepository imageRepository;
    private final ModelMapper modelMapper;

    /**
     * Constructor
     *
     * @param imageRepository accessing CRUD Repository for Image Entity
     * @param modelMapper mapping Entity-DTO relationship
     */
    public  ImageServiceImpl(ImageRepository imageRepository, ModelMapper modelMapper) {
        this.imageRepository = imageRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public ImageDTO addImage(MultipartFile file) throws IOException {

        String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        Image image = new Image(fileName, file.getContentType(), file.getBytes());
        return modelMapper.map(imageRepository.save(image), ImageDTO.class);
    }

    @Transactional
    @Override
    public ImageDTO getImage(Long id) throws ImageNotFoundException {
        if (imageRepository.findById(id).isPresent()) {
            return modelMapper.map(imageRepository.findById(id).get(), ImageDTO.class);
        } else {
            throw new ImageNotFoundException();
        }
    }

    @Override
    public List<ImageDTO> getAllImage() {
        return Arrays.asList(modelMapper.map(imageRepository.findAll(), ImageDTO[].class));
    }

    @Override
    public ImageDTO getImageByIdeaId(Long id) {
        return modelMapper.map(imageRepository.findByIdeaId(id), ImageDTO.class);
    }

}
