package com.atoss.idea.management.system.service;

import com.atoss.idea.management.system.repository.dto.AvatarDTO;
import com.atoss.idea.management.system.repository.dto.UserRequestDTO;
import com.atoss.idea.management.system.repository.dto.UserResponseDTO;
import com.atoss.idea.management.system.repository.entity.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public interface UserService {
    UserResponseDTO addUser(String username, String email);

    UserResponseDTO updateUserStatus(String username, Boolean isActive);

    UserResponseDTO updateUserRole(String username, Role role);

    UserRequestDTO updateUserPassword(String username, String password);

    UserResponseDTO updateUserProfile(String fullName, String username, String newUsername, String email, AvatarDTO avatar);

    UserResponseDTO getUserByUsername(String username);

    Page<UserResponseDTO> getAllUsers(Pageable pageable);

}
