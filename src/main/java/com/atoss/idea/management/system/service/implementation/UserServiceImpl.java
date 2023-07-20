package com.atoss.idea.management.system.service.implementation;

import com.atoss.idea.management.system.exception.UserAlreadyExistException;
import com.atoss.idea.management.system.exception.UserNotFoundException;
import com.atoss.idea.management.system.repository.UserRepository;
import com.atoss.idea.management.system.repository.dto.UserRegisterDTO;
import com.atoss.idea.management.system.repository.dto.UserRequestDTO;
import com.atoss.idea.management.system.repository.dto.UserResponseDTO;
import com.atoss.idea.management.system.repository.dto.UserUpdateDTO;
import com.atoss.idea.management.system.repository.entity.Avatar;
import com.atoss.idea.management.system.repository.entity.User;
import com.atoss.idea.management.system.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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
    public UserRequestDTO updateUserPassword(String username, String password) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User dose not exist!"));
        user.setPassword(password);
        userRepository.save(user);
        return modelMapper.map(user, UserRequestDTO.class);
    }

    @Override
    public UserResponseDTO updateUserByUsername(String username, UserUpdateDTO userUpdateDTO) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));
        if (userUpdateDTO.getUsername() != null) {
            user.setUsername(userUpdateDTO.getUsername());
        }
        if (userUpdateDTO.getEmail() != null) {
            user.setEmail(userUpdateDTO.getEmail());
        }
        if (userUpdateDTO.getAvatar() != null) {
            user.setAvatar(modelMapper.map(user.getAvatar(), Avatar.class));
        }
        if (userUpdateDTO.getFullName() != null) {
            user.setFullName(userUpdateDTO.getFullName());
        }
        if (userUpdateDTO.getRole() != null) {
            user.setRole(userUpdateDTO.getRole());
        }
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
        return new PageImpl<UserResponseDTO>(
                userRepository.findAll(pageable)
                                        .stream()
                                        .map(user -> modelMapper.map(user, UserResponseDTO.class))
                                        .toList()
        );
    }

    @Override
    public UserRegisterDTO getUserByEmail(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException("User not found!"));
        return modelMapper.map(user, UserRegisterDTO.class);
    }
}
