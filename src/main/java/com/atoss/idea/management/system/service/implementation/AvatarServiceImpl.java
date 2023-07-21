package com.atoss.idea.management.system.service.implementation;

import com.atoss.idea.management.system.repository.AvatarRepository;
import com.atoss.idea.management.system.repository.UserRepository;
import com.atoss.idea.management.system.repository.dto.AvatarDTO;
import com.atoss.idea.management.system.service.AvatarService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import java.util.Arrays;
import java.util.List;

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

    @Override
    public List<AvatarDTO> getAllAvatars() {
        return Arrays.asList(modelMapper.map(avatarRepository.findAll(), AvatarDTO[].class));
    }

}
