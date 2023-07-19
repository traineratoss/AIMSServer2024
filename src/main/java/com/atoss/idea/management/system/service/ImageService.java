package com.atoss.idea.management.system.service;

import com.atoss.idea.management.system.repository.entity.Image;
import org.springframework.web.multipart.MultipartFile;

public interface ImageService {

    Image addImage(MultipartFile imageFile) throws Exception;
}
