package com.atoss.idea.management.system.service;

import com.atoss.idea.management.system.repository.dto.ImageDTO;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;

public interface ImageService {

    ImageDTO addImage(MultipartFile imageFile) throws IOException;

    List<ImageDTO> getAllImage();

}
