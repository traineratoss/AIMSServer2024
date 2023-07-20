package com.atoss.idea.management.system.service.implementation;

import com.atoss.idea.management.system.exception.AvatarNotFoundException;
import com.atoss.idea.management.system.exception.UserNotFoundException;
import com.atoss.idea.management.system.repository.AvatarRepository;
import com.atoss.idea.management.system.repository.UserRepository;
import com.atoss.idea.management.system.repository.dto.AvatarDTO;
import com.atoss.idea.management.system.repository.entity.Avatar;
import com.atoss.idea.management.system.repository.entity.User;
import com.atoss.idea.management.system.service.AvatarService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.Objects;

@Service
public class AvatarServiceImpl implements AvatarService {

    private final UserRepository userRepository;
    private final AvatarRepository avatarRepository;
    private final ModelMapper modelMapper;

    public AvatarServiceImpl(UserRepository userRepository, AvatarRepository avatarRepository, ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.avatarRepository = avatarRepository;
        this.modelMapper = modelMapper;
    }

    @Transactional
    @Override
    public AvatarDTO addAvatar(String username, MultipartFile file) throws IOException {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException("User not found!"));
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        Avatar avatar = new Avatar(fileName, file.getContentType(), file.getBytes());
        avatar.setUser(user);
        user.setAvatar(avatar);
        avatarRepository.save(avatar);
        return modelMapper.map(avatar, AvatarDTO.class);
    }

    @Transactional
    @Override
    public AvatarDTO getAvatar(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException("User not found!"));
        if (Objects.nonNull(user.getAvatar())) {
            return modelMapper.map(user.getAvatar(), AvatarDTO.class);
        }
        throw new AvatarNotFoundException("Avatar not found!");
    }

    @Transactional
    @Override
    public AvatarDTO updateAvatar(String username, MultipartFile file) throws IOException {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException("User not found!"));
        Avatar avatar = user.getAvatar();
        if (Objects.nonNull(avatar)) {
            String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
            Avatar newAvatar = new Avatar(fileName, file.getContentType(), file.getBytes());
            user.setAvatar(newAvatar);
            avatarRepository.delete(avatar);
            return modelMapper.map(avatarRepository.save(newAvatar), AvatarDTO.class);
        }
        return addAvatar(username, file);
    }

    @Transactional
    @Override
    public void deleteAvatar(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException("User not found!"));
        if (Objects.nonNull(user.getAvatar())) {
            avatarRepository.delete(user.getAvatar());
        }
        user.setAvatar(null);
        userRepository.save(user);
    }
}
