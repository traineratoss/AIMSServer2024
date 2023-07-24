package com.atoss.idea.management.system.service.implementation;

import com.atoss.idea.management.system.exception.EmailAlreadyExistException;
import com.atoss.idea.management.system.exception.UserAlreadyExistException;
import com.atoss.idea.management.system.exception.UserNotFoundException;
import com.atoss.idea.management.system.exception.UsernameAlreadyExistException;
import com.atoss.idea.management.system.repository.AvatarRepository;
import com.atoss.idea.management.system.repository.UserRepository;
import com.atoss.idea.management.system.repository.dto.ChangePasswordDTO;
import com.atoss.idea.management.system.repository.dto.UserResponseDTO;
import com.atoss.idea.management.system.repository.dto.UserRequestDTO;
import com.atoss.idea.management.system.repository.dto.UserUpdateDTO;
import com.atoss.idea.management.system.repository.entity.Avatar;
import com.atoss.idea.management.system.repository.entity.User;
import com.atoss.idea.management.system.service.SendEmailService;
import com.atoss.idea.management.system.service.UserService;
import com.google.common.hash.Hashing;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.nio.charset.StandardCharsets;


@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final ModelMapper modelMapper;

    private final SendEmailService sendEmailService;

    private final AvatarRepository avatarRepository;


    public UserServiceImpl(UserRepository userRepository,
                           ModelMapper modelMapper,
                           SendEmailService sendEmailService,
                           AvatarRepository avatarRepository) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.sendEmailService = sendEmailService;
        this.avatarRepository = avatarRepository;
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
        sendEmailService.sendEmailToAdmin(username);
        return modelMapper.map(user, UserResponseDTO.class);
    }

    @Override
    public UserRequestDTO updateUserPassword(String username, String password) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UserAlreadyExistException("User already exist!"));
        user.setPassword(password);
        userRepository.save(user);
        return modelMapper.map(user, UserRequestDTO.class);
    }

    @Override
    public UserResponseDTO updateUserByUsername(String username, UserUpdateDTO userUpdateDTO) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException("User not found"));
        if (userUpdateDTO.getUsername() != null) {
            if (userRepository.findByUsername(userUpdateDTO.getUsername()).isPresent()) {
                throw new UsernameAlreadyExistException("This username is already in use!");
            }
            user.setUsername(userUpdateDTO.getUsername());
        }
        if (userUpdateDTO.getEmail() != null) {
            if (userRepository.findByEmail(userUpdateDTO.getEmail()).isPresent()) {
                throw new EmailAlreadyExistException("This email is already in use!");
            }
            user.setEmail(userUpdateDTO.getEmail());
        }
        if (userUpdateDTO.getAvatarId() != null) {
            user.setAvatar(modelMapper.map(avatarRepository.findAvatarById(userUpdateDTO.getAvatarId()), Avatar.class));
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
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException("User not found"));
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
    public UserResponseDTO getUserByEmail(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException("User not found!"));
        return modelMapper.map(user, UserResponseDTO.class);
    }

    @Override
    public Page<UserResponseDTO> getAllUsersByUsername(String username) {
        return new PageImpl<UserResponseDTO>(
                userRepository.findByUsernameStartsWith(username)
                        .stream()
                        .map(user -> modelMapper.map(user, UserResponseDTO.class))
                        .toList()
        );
    }

    @Override
    public Page<UserResponseDTO> getAllPendingUsers(boolean isActive, Pageable pageable) {
        return new PageImpl<UserResponseDTO>(
          userRepository.findAll(pageable)
                  .stream()
                  .filter(user -> user.getIsActive() != null && user.getIsActive().equals(isActive))
                  .map(user -> modelMapper.map(user, UserResponseDTO.class))
                  .toList()
        );
    }

    @Override
    public boolean changePassword(ChangePasswordDTO changePasswordDTO) {
        String username = changePasswordDTO.getUsername();
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UserAlreadyExistException("User already exist!"));
        String databasePassword = user.getPassword();
        String hashFrontendOldPassword = Hashing.sha256()
                .hashString(changePasswordDTO.getOldPassword(), StandardCharsets.UTF_8)
                .toString();
        String hashFrontendNewPassword = Hashing.sha256()
                .hashString(changePasswordDTO.getNewPassword(), StandardCharsets.UTF_8)
                .toString();
        if (!hashFrontendOldPassword.equals(databasePassword)) {
            return false;
        }
        user.setPassword(hashFrontendNewPassword);
        userRepository.save(user);
        return true;
    }

    @Override
    public void sendEmail(String username) {
        sendEmailService.sendEmailToUser(username);
    }
}
