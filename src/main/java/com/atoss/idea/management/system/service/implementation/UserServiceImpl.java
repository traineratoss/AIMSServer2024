package com.atoss.idea.management.system.service.implementation;

import com.atoss.idea.management.system.repository.UserRepository;
import com.atoss.idea.management.system.repository.dto.UserRequestDTO;
import com.atoss.idea.management.system.repository.dto.UserResponseDTO;
import com.atoss.idea.management.system.repository.entity.Role;
import com.atoss.idea.management.system.repository.entity.User;
import com.atoss.idea.management.system.service.UserService;
import com.atoss.idea.management.system.utils.PasswordGenerator;
import com.google.common.hash.Hashing;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import java.nio.charset.StandardCharsets;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final ModelMapper modelMapper;


    public UserServiceImpl(UserRepository userRepository, ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public UserResponseDTO addUser(String username, String email) {
        if (userRepository.findByUsernameOrEmail(username, email).isPresent()) {
            throw new RuntimeException("Already exist!");
        }
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setIsActive(false);
        String password = PasswordGenerator.generatePassayPassword(15);
        String hashPassword = Hashing.sha256()
                .hashString(password, StandardCharsets.UTF_8)
                .toString();
        user.setPassword(hashPassword);
        userRepository.save(user);
        return modelMapper.map(user, UserResponseDTO.class);
    }

    @Override
    public UserResponseDTO updateUserStatus(String username, Boolean isActive) {
        return null;
    }

    @Override
    public UserResponseDTO updateUserRole(String username, Role role) {
        return null;
    }

    @Override
    public UserRequestDTO updateUserPassword(String username, String password) {
        return null;
    }

    @Override
    public UserResponseDTO updateUserProfile(String username, String fullName, String email) {
        return null;
    }

    @Override
    public UserResponseDTO getUserByUsername(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("Already exist!"));
        return modelMapper.map(user, UserResponseDTO.class);
    }
}
