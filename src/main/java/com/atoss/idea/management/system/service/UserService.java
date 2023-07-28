package com.atoss.idea.management.system.service;

import com.atoss.idea.management.system.repository.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface UserService {
    UserResponseDTO addUser(String username, String email);

    UserResponseDTO updateUserByUsername(String username, UserUpdateDTO userUpdateDTO);

    UserResponseDTO getUserByUsername(String username);

    Page<UserAdminDashboardResponseDTO> getAllUsersForAdmin(Pageable pageable);

    UserPageDTO getAllUsers(Pageable pageable);

    Page<UserResponseDTO> getAllUsersByUsername(String username);

    Page<UserResponseDTO> getAllPendingUsers(boolean isActive, Pageable pageable);

    boolean changePassword(ChangePasswordDTO changePasswordDTO);

    void sendApproveEmail(String username);

    ResponseEntity<Object> sendDeclineEmail(String user);

    ResponseEntity<UserResponseDTO> login(String usernameOrEmail, String password);

    ResponseEntity<UserResponseDTO> sendForgotPassword(String usernameOrEmail);

    Boolean deleteUser(String username);

    ResponseEntity<Object> sendDeactivateEmail(String user);

    ResponseEntity<Object> sendActivateEmail(String user);


}
