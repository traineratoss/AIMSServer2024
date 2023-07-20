package com.atoss.idea.management.system.service;

import com.atoss.idea.management.system.repository.dto.UserRequestDTO;
import com.atoss.idea.management.system.repository.dto.UserResponseDTO;
import com.atoss.idea.management.system.repository.dto.UserUpdateDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public interface UserService {
    UserResponseDTO addUser(String username, String email);

    UserRequestDTO updateUserPassword(String username, String password);

    UserResponseDTO updateUserByUsername(String username, UserUpdateDTO userUpdateDTO);

    UserResponseDTO getUserByUsername(String username);

    Page<UserResponseDTO> getAllUsers(Pageable pageable);

}
