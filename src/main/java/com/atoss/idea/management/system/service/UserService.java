package com.atoss.idea.management.system.service;

import com.atoss.idea.management.system.repository.dto.ChangePasswordDTO;
import com.atoss.idea.management.system.repository.dto.UserResponseDTO;
import com.atoss.idea.management.system.repository.dto.UserRequestDTO;
import com.atoss.idea.management.system.repository.dto.UserUpdateDTO;
import com.atoss.idea.management.system.repository.dto.UserRegisterDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface UserService {
    UserResponseDTO addUser(String username, String email);

    UserRequestDTO updateUserPassword(String username, String password);

    UserResponseDTO updateUserByUsername(String username, UserUpdateDTO userUpdateDTO);

    UserResponseDTO getUserByUsername(String username);

    Page<UserResponseDTO> getAllUsers(Pageable pageable);

    UserRegisterDTO getUserByEmail(String email);

    Page<UserResponseDTO> getAllUsersByUsername(String username);

    Page<UserResponseDTO> getAllPendingUsers(boolean isActive, Pageable pageable);

    boolean changePassword(ChangePasswordDTO changePasswordDTO);

    void sendEmail(String username);

    ResponseEntity<UserResponseDTO> login(String usernameOrEmail, String password);

    ResponseEntity<UserResponseDTO> sendForgotPassword(String usernameOrEmail);

}
