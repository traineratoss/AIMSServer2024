package com.atoss.idea.management.system.service;

import com.atoss.idea.management.system.repository.dto.AvatarDTO;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

public interface AvatarService {
    AvatarDTO addAvatar(String username, MultipartFile file) throws IOException;

    AvatarDTO getAvatar(String username);

    AvatarDTO updateAvatar(String username, MultipartFile file) throws IOException;

    void deleteAvatar(String username);
}
