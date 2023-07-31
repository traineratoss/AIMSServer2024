package com.atoss.idea.management.system.controller;

import com.atoss.idea.management.system.repository.dto.*;
import com.atoss.idea.management.system.service.UserService;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Transactional
    @PostMapping
    public ResponseEntity<UserResponseDTO> addUser(@RequestParam(name = "username") String username,
                                                   @RequestParam(name = "email") String email) {
        return new ResponseEntity<>(userService.addUser(username, email), HttpStatus.CREATED);
    }

    @Transactional
    @PatchMapping("/update-profile")
    public UserResponseDTO updateUserByUsername(@RequestParam(value = "username") String username, @RequestBody UserUpdateDTO userUpdateDTO) {
        return userService.updateUserByUsername(username, userUpdateDTO);
    }

    @Transactional
    @PatchMapping("/update-role")
    public UserResponseDTO updateUserRole(@RequestParam(value = "username") String username) {
        return userService.updateUserRole(username);
    }

    @Transactional
    @GetMapping
    public UserResponseDTO getUserByUsername(@RequestParam(name = "username") String username) {
        return userService.getUserByUsername(username);
    }

    @Transactional
    @GetMapping("/allUsers")
    public  ResponseEntity<UserPageDTO> getAllUsersForAdmin(@RequestParam(required = true) int pageSize,
                                                                                    @RequestParam(required = true) int pageNumber,
                                                                                    @RequestParam(required = true) String sortCategory) {
        return new ResponseEntity<>(
                userService.getAllUsersForAdmin(
                        PageRequest.of(
                                pageNumber,
                                pageSize,
                                Sort.by(Sort.Direction.ASC, sortCategory)
                        )
                ),
                HttpStatus.OK
        );
    }

    @Transactional
    @GetMapping("/all")
    public  ResponseEntity<Page<UserResponseDTO>> getAllUsers(@RequestParam(required = true) int pageSize,
                                                              @RequestParam(required = true) int pageNumber,
                                                              @RequestParam(required = true) String sortCategory) {
        return new ResponseEntity<>(
                userService.getAllUsers(
                        PageRequest.of(
                                pageNumber,
                                //10
                                pageSize,
                                Sort.by(Sort.Direction.ASC, sortCategory)
                        )
                ),
                HttpStatus.OK
        );
    }

    @Transactional
    @GetMapping("/allByUsername")
    public Page<UserResponseDTO> getAllUserByUsername(@RequestParam(name = "username") String username) {
        return userService.getAllUsersByUsername(username);
    }

    @Transactional
    @GetMapping("/allByIsActive")
    public ResponseEntity<Page<UserResponseDTO>> getAllUserByIsActive(@RequestParam(name = "isActive") boolean isActive,
                                                                      @RequestParam(required = true) int pageSize,
                                                                      @RequestParam(required = true) int pageNumber,
                                                                      @RequestParam(required = true) String sortCategory) {
        return new ResponseEntity<>(
                userService.getAllPendingUsers(
                        isActive,
                        PageRequest.of(
                            pageNumber,
                            pageSize,
                            Sort.by(Sort.Direction.ASC, sortCategory)
                        )
                ),
                HttpStatus.OK
        );
    }

    @Transactional
    @PostMapping("/change-password")
    public ResponseEntity<Object> changePassword(@RequestBody ChangePasswordDTO changePasswordDTO) {
        boolean passwordChanged = userService.changePassword(changePasswordDTO);
        if (passwordChanged) {
            return new ResponseEntity<>(HttpStatus.OK);
        }
        ResponseEntity<Object> entity = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        return entity;
    }

    @Transactional
    @PostMapping("/send-approve-email")
    public void sendApproveEmail(@RequestBody String username) {
        userService.sendApproveEmail(username);
    }

    @Transactional
    @PostMapping("/send-decline-email")
    public ResponseEntity<Object> sendDeclineEmail(@RequestBody String username) {
        return userService.sendDeclineEmail(username);
    }

    @PostMapping("/login")
    public UserSecurityDTO login(@RequestParam(name = "username") String username,
                                                         @RequestBody String password) {
        return userService.login(username, password);
    }

    @Transactional
    @PostMapping("/send-forgot-password")
    public ResponseEntity<UserResponseDTO> sendForgotPassword(@RequestBody String usernameOrEmail) {
        return userService.sendForgotPassword(usernameOrEmail);
    }

    @Transactional
    @DeleteMapping
    public ResponseEntity<Boolean> deleteUser(@RequestBody String username) {
        return new ResponseEntity<>(userService.deleteUser(username), HttpStatus.OK);
    }

    @Transactional
    @PostMapping("/send-deactivate-message")
    public ResponseEntity<Object> sendDeactivateMessage(@RequestBody String username) {
        return userService.sendDeactivateEmail(username);
    }

    @Transactional
    @PostMapping("/send-activate-message")
    public ResponseEntity<Object> sendActivateMessage(@RequestBody String username) {
        return userService.sendActivateEmail(username);
    }



}
