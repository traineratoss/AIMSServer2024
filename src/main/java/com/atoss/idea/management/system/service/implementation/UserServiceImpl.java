package com.atoss.idea.management.system.service.implementation;

import com.atoss.idea.management.system.exception.*;
import com.atoss.idea.management.system.repository.AvatarRepository;
import com.atoss.idea.management.system.repository.UserRepository;
import com.atoss.idea.management.system.repository.dto.ChangePasswordDTO;
import com.atoss.idea.management.system.repository.dto.UserResponseDTO;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public UserResponseDTO updateUserByUsername(String username, UserUpdateDTO userUpdateDTO) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException("User not found"));
        if (userUpdateDTO.getUsername() != null) {
            user.setUsername(userUpdateDTO.getUsername());
        }
        if (userUpdateDTO.getEmail() != null) {
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
        return new PageImpl<>(
                userRepository.findAll(pageable)
                                        .stream()
                                        .map(user -> modelMapper.map(user, UserResponseDTO.class))
                                        .toList()
        );
    }

    @Override
    public Page<UserResponseDTO> getAllUsersByUsername(String username) {
        return new PageImpl<>(
                userRepository.findByUsernameStartsWith(username)
                        .stream()
                        .map(user -> modelMapper.map(user, UserResponseDTO.class))
                        .toList()
        );
    }

    @Override
    public Page<UserResponseDTO> getAllPendingUsers(boolean isActive, Pageable pageable) {
        return new PageImpl<>(
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
    public void sendApproveEmail(String username) {
        sendEmailService.sendApproveEmailToUser(username);
    }

    @Override
    public ResponseEntity<Object> sendDeclineEmail(String username) {
        if (userRepository.findByUsername(username).isPresent()) {
            sendEmailService.sendDeclineEmailToUser(username);
            if (deleteUser(username)) {
                return new ResponseEntity<>("User deleted successfully", HttpStatus.OK);
            }
            throw new UserStatusIsActiveException("User status is active");
        }
        throw new UserNotFoundException("User not found!");
    }

    @Override
    public ResponseEntity<UserResponseDTO> login(String usernameOrEmail, String password) {
        String hashFrontendPassword = Hashing.sha256()
                .hashString(password, StandardCharsets.UTF_8)
                .toString();
        User user = userRepository
                .findByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
                .orElseThrow(
                        () -> new UserNotFoundException("User not found!")
                );
        if (!user.getPassword().equals(hashFrontendPassword)) {
            throw new IncorrectPasswordException("Bad credentials!");
        }
        return new ResponseEntity<>(modelMapper.map(user, UserResponseDTO.class), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<UserResponseDTO> sendForgotPassword(String usernameOrEmail) {
        User user = userRepository
                .findByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
                .orElseThrow(() -> new UserNotFoundException("User or email not found"));
        sendEmailService.sendEmailForgotPassword(user.getUsername());
        return new ResponseEntity<>(modelMapper.map(user, UserResponseDTO.class), HttpStatus.OK);
    }

    @Override
    public Boolean deleteUser(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException("User not found!"));
        if (user.getIsActive()) {
            return false;
        }
        userRepository.delete(user);
        return true;
    }

    @Override
    public ResponseEntity<Object> sendDeactivateEmail(String username) {
        if (sendEmailService.sendDeactivateEmailToUser(username)) {
            return new ResponseEntity<>("Email send", HttpStatus.OK);
        }
        throw new EmailFailedException("Sending email failed");
    }

    @Override
    public ResponseEntity<Object> sendActivateEmail(String username) {
        if (sendEmailService.sendActivateEmailToUser(username)) {
            return new ResponseEntity<>("Email send", HttpStatus.OK);
        }
        throw new EmailFailedException("Sending email failed");
    }
}
