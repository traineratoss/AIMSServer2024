package com.atoss.idea.management.system.controller;

import com.atoss.idea.management.system.repository.dto.AvatarDTO;
import com.atoss.idea.management.system.repository.dto.UserRequestDTO;
import com.atoss.idea.management.system.repository.dto.UserResponseDTO;
import com.atoss.idea.management.system.repository.entity.Role;
import com.atoss.idea.management.system.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public UserResponseDTO addUser(@RequestParam(name = "username") String username,
                                   @RequestParam(name = "email") String email) {
        return userService.addUser(username, email);
    }

    @PutMapping("/role")
    public UserResponseDTO updateUserRole(@RequestParam(name = "username") String username,
                                          @RequestParam(name = "role") Role role) {
        return userService.updateUserRole(username, role);
    }

    @PutMapping("/status")
    public UserResponseDTO updateUserStatus(@RequestParam(name = "username") String username,
                                            @RequestParam(name = "is_active") Boolean isActive) {
        return userService.updateUserStatus(username, isActive);
    }

    @PutMapping("/password")
    public UserRequestDTO updateUserPassword(@RequestParam(name = "username") String username,
                                             @RequestParam(name = "password") String password) {
        return userService.updateUserPassword(username, password);
    }

    @PutMapping("/profile")
    public UserResponseDTO updateUserProfile(@RequestParam(name = "full_name") String fullName,
                                             @RequestParam(name = "user_name") String username,
                                             @RequestParam(name = "new_username") String newUsername,
                                             @RequestParam(name = "email") String email,
                                             @RequestBody AvatarDTO avatar) {
        return userService.updateUserProfile(fullName, username, newUsername, email, avatar);
    }

    @GetMapping
    public UserResponseDTO getUserByUsername(@RequestParam(name = "username") String username) {
        return userService.getUserByUsername(username);
    }
}
