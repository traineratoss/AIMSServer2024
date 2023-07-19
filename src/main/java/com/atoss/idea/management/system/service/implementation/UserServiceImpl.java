package com.atoss.idea.management.system.service.implementation;

import com.atoss.idea.management.system.exception.UserAlreadyExistException;
import com.atoss.idea.management.system.repository.UserRepository;
import com.atoss.idea.management.system.repository.dto.AvatarDTO;
import com.atoss.idea.management.system.repository.dto.UserRequestDTO;
import com.atoss.idea.management.system.repository.dto.UserResponseDTO;
import com.atoss.idea.management.system.repository.entity.Avatar;
import com.atoss.idea.management.system.repository.entity.Role;
import com.atoss.idea.management.system.repository.entity.User;
import com.atoss.idea.management.system.service.UserService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


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
            throw new UserAlreadyExistException("User already exist!");
        }
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setIsActive(false);
        userRepository.save(user);
        return modelMapper.map(user, UserResponseDTO.class);
    }

    @Override
    public UserResponseDTO updateUserStatus(String username, Boolean isActive) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User dose not exist!"));
        user.setIsActive(isActive);
        userRepository.save(user);
        return modelMapper.map(user, UserResponseDTO.class);
    }

    @Override
    public UserResponseDTO updateUserRole(String username, Role role) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User dose not exist!"));
        user.setRole(role);
        userRepository.save(user);
        return modelMapper.map(user, UserResponseDTO.class);
    }

    @Override
    public UserRequestDTO updateUserPassword(String username, String password) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User dose not exist!"));
        user.setPassword(password);
        userRepository.save(user);
        return modelMapper.map(user, UserRequestDTO.class);
    }

    @Override
    public UserResponseDTO updateUserProfile(String fullName, String username, String newUsername, String email, AvatarDTO avatar) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User dose not exist!"));
        user.setUsername(newUsername);
        user.setFullName(fullName);
        user.setEmail(email);
        user.setAvatar(modelMapper.map(avatar, Avatar.class));
        userRepository.save(user);
        return modelMapper.map(user, UserResponseDTO.class);
    }

    @Override
    public UserResponseDTO getUserByUsername(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("Already exist!"));
        return modelMapper.map(user, UserResponseDTO.class);
    }

    @Override
    public Page<UserResponseDTO> getAllUsers(Pageable pageable) {
        return modelMapper.map(userRepository.findAll(pageable), new TypeToken<Page<UserResponseDTO>>() {}.getType());
    }
}
