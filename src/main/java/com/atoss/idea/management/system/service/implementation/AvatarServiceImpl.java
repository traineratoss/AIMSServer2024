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

    /**
     * Constructs a new instance of the AvatarServiceImpl.
     *
     * This constructor initializes the AvatarServiceImpl with the required dependencies, including the UserRepository
     * for accessing user data, the AvatarRepository for accessing avatar data, and the ModelMapper for mapping objects
     * between different types.
     *
     * @param userRepository  The UserRepository instance for accessing user data.
     * @param avatarRepository The AvatarRepository instance for accessing avatar data.
     * @param modelMapper      The ModelMapper instance for mapping objects between different types.
     */
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
