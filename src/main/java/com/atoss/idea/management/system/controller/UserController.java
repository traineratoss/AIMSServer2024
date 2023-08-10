package com.atoss.idea.management.system.controller;

import com.atoss.idea.management.system.exception.*;
import com.atoss.idea.management.system.repository.dto.*;
import com.atoss.idea.management.system.service.UserService;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;
import com.atoss.idea.management.system.exception.UserAlreadyExistException;
import com.atoss.idea.management.system.exception.UserNotFoundException;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    /**
     * Constructs a new instance of the UserController with the provided UserService.
     *
     * @param userService The UserService instance to be used by the UserController.
     *
     * @see UserService
     */
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Adds a new user to the database.
     *
     * @param username The unique parameter representing the username of the new user.
     * @param email    The unique parameter representing the email address of the new user.
     * @return A ResponseEntity with the UserResponseDTO containing information about the newly added user.
     * @throws UserAlreadyExistException If the provided username or email is already used by another user in the database.
     *
     * @see UserResponseDTO
     * @see UserAlreadyExistException
     * @see UserService#addUser(String, String)
     */
    @Transactional
    @PostMapping
    public ResponseEntity<UserResponseDTO> addUser(@RequestParam(name = "username") String username,
                                                   @RequestParam(name = "email") String email) {
        return new ResponseEntity<>(userService.addUser(username, email), HttpStatus.CREATED);
    }

    /**
     * Updates the user's profile based on the provided username and UserUpdateDTO.
     *
     * @param username      The unique username of the user whose profile needs to be updated.
     * @param userUpdateDTO The UserUpdateDTO object containing the updated user information.
     * @return A UserResponseDTO object representing the updated user's profile information.
     *
     * @see UserUpdateDTO
     * @see UserResponseDTO
     * @see UserService#updateUserByUsername(String, UserUpdateDTO)
     */
    @Transactional
    @PatchMapping("/update-profile")
    public UserResponseDTO updateUserByUsername(@RequestParam(value = "username") String username, @RequestBody UserUpdateDTO userUpdateDTO) {
        return userService.updateUserByUsername(username, userUpdateDTO);
    }

    /**
     * Updates the role of a user based on the provided username.
     *
     * @param username The username of the user whose role needs to be updated.
     * @return A UserResponseDTO object representing the updated user's profile information after the role update.
     * @throws UserNotFoundException If the user with the given username is not found in the system.
     *
     * @see UserResponseDTO
     * @see UserNotFoundException
     * @see UserService#updateUserRole(String)
     */
    @Transactional
    @PatchMapping("/update-role")
    public UserResponseDTO updateUserRole(@RequestParam(value = "username") String username) {
        return userService.updateUserRole(username);
    }

    /**
     * Retrieves the user's profile information based on the provided username.
     *
     * @param username The username of the user whose profile information needs to be retrieved.
     * @return A UserResponseDTO object representing the user's profile information.
     * @throws UserNotFoundException If the user with the given username is not found in the system.
     *
     * @see UserResponseDTO
     * @see UserNotFoundException
     * @see UserService#getUserByUsername(String)
     */
    @Transactional
    @GetMapping
    public UserResponseDTO getUserByUsername(@RequestParam(name = "username") String username) {
        return userService.getUserByUsername(username);
    }

    /**
     * Retrieves a paginated list of all users for the admin panel.
     *
     * @param pageSize     The number of users to be included in each page of the result.
     * @param pageNumber   The page number of the result set to retrieve.
     * @param sortCategory The field by which the users should be sorted. It can be any valid property of the User entity.
     * @return A ResponseEntity containing the UserPageDTO object representing a paginated list of users.
     * @throws IllegalArgumentException If pageSize or pageNumber is less than 1.
     *
     * @see UserPageDTO
     * @see PageRequest
     * @see Sort
     * @see UserService#getAllUsersForAdmin(Pageable)
     */
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

    /**
     * Retrieves a paginated list of all users.
     *
     * @param pageSize     The number of users to be included in each page of the result.
     * @param pageNumber   The page number of the result set to retrieve.
     * @param sortCategory The field by which the users should be sorted. It can be any valid property of the User entity.
     * @return A ResponseEntity containing a Page of UserResponseDTO objects representing a paginated list of users.
     * @throws IllegalArgumentException If pageSize or pageNumber is less than 1.
     *
     * @see UserResponseDTO
     * @see PageRequest
     * @see Sort
     * @see UserService#getAllUsers(Pageable)
     */
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

    /**
     * Retrieves a paginated list of users based on the provided username and additional parameters.
     *
     * @param pageSize        The number of users to be included in each page of the result.
     * @param pageNumber      The page number of the result set to retrieve.
     * @param sortCategory    The field by which the users should be sorted. It can be any valid property of the User entity.
     * @param username        The username used as a filter to retrieve users with a specific username.
     * @param currentUsername The username of the current user performing the request.
     * @return A ResponseEntity containing the UserPageDTO object representing a paginated list of users with the specified username filter.
     * @throws IllegalArgumentException        If pageSize or pageNumber is less than 1.
     * @throws UserNotFoundException             If the user with the given username is not found in the system.
     *
     * @see UserPageDTO
     * @see PageRequest
     * @see Sort
     * @see UserService#getAllUsersByUsernamePageable(Pageable, String)
     */
    @Transactional
    @GetMapping("/allByUsername")
    public ResponseEntity<UserPageDTO> getAllUserByUsername(@RequestParam(required = true) int pageSize,
                                                            @RequestParam(required = true) int pageNumber,
                                                            @RequestParam(required = true) String sortCategory,
                                                            @RequestParam(name = "username") String username,
                                                            @RequestParam(name = "currentUsername") String currentUsername) {
        return new ResponseEntity<>(
                userService.getAllUsersByUsernamePageable(
                    PageRequest.of(
                            pageNumber,
                            pageSize,
                            Sort.by(Sort.Direction.ASC, sortCategory)
                    ),
                    username
                ),
                HttpStatus.OK
        );
    }

    /**
     * Retrieves a paginated list of users based on their active status.
     *
     * @param isActive      The boolean flag indicating whether to retrieve active (true) or inactive (false) users.
     * @param pageSize      The number of users to be included in each page of the result.
     * @param pageNumber    The page number of the result set to retrieve.
     * @param sortCategory  The field by which the users should be sorted. It can be any valid property of the User entity.
     * @return A ResponseEntity containing a Page of UserResponseDTO objects representing a paginated list of users with the specified active status.
     * @throws IllegalArgumentException        If pageSize or pageNumber is less than 1.
     *
     * @see UserResponseDTO
     * @see PageRequest
     * @see Sort
     * @see UserService#getAllPendingUsers(boolean, Pageable)
     */
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

    /**
     * Changes the password for the user.
     *
     * @param changePasswordDTO The ChangePasswordDTO object containing the necessary information for password change.
     * @return A ResponseEntity representing the result of the password change operation.
     *         If the password is successfully changed, the response entity has an HTTP status of 200 (HttpStatus.OK).
     *         If the password change operation fails, the response entity has an HTTP status of 400 (HttpStatus.BAD_REQUEST).
     *
     * @see ChangePasswordDTO
     * @see ResponseEntity
     * @see UserService#changePassword(ChangePasswordDTO)
     */
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

    /**
     * Sends an account activation approval email to the user
     * @param username the username of the user to which the approve email will be sent
     * @return ResponseEntity with the message "User approve successfully" and OK HTTP status
     * @throws UserNotFoundException when the user was not found in the database
     * @throws ApproveAlreadyGrantedException when the user's request was already approved
     */
    @Transactional
    @PostMapping("/send-approve-email")
    public ResponseEntity<Object> sendApproveEmail(@RequestBody String username) {
        return userService.sendApproveEmail(username);
    }

    /**
     * Sends a decline email to the user
     * @param username the username of the user to which the decline email will be sent
     * @return ResponseEntity with the message "User deleted successfully" and OK HTTP status
     * @throws UserNotFoundException when the user was not found in the database
     * @throws ApproveAlreadyGrantedException when the user's request was already approved
     * @throws UserAlreadyActivatedException when the user has already been activated
     */
    @Transactional
    @PostMapping("/send-decline-email")
    public ResponseEntity<Object> sendDeclineEmail(@RequestBody String username) {
        return userService.sendDeclineEmail(username);
    }

    /**
     * Method used to check if the user's credentials are in the database
     * @param username the user's username
     * @param hashPassword the encrypted password obtained by using bcrypt algorithm
     * @return UserSecurityDTO with the user's credentials
     * @throws UserAlreadyDeactivatedException if the user account is deactivated
     * @throws BadCredentialsException if the credentials entered are not valid
     */
    @PostMapping("/login")
    public UserSecurityDTO login(@RequestParam(name = "username") String username,
                                                         @RequestBody String hashPassword) {
        return userService.login(username, hashPassword);
    }

    /**
     * Sends an email with a newly generated password to the user
     * @param usernameOrEmail the username or email of the user which requested a new password
     * @return ResponseEntity with the UserResponseDTO and HTTP Status OK
     * @throws UserNotFoundException if the user specified was not found in the database
     */
    @Transactional
    @PostMapping("/send-forgot-password")
    public ResponseEntity<Object> sendForgotPassword(@RequestBody String usernameOrEmail) {
        return userService.sendForgotPassword(usernameOrEmail);
    }

    /**
     * Deletes the user
     * @param username the username of the user which will be deleted
     * @return ResponseEntity with delete result and OK HTTP status
     * @throws UserNotFoundException if the user specified by username is not found
     */
    @Transactional
    @DeleteMapping
    public ResponseEntity<Boolean> deleteUser(@RequestBody String username) {
        return new ResponseEntity<>(userService.deleteUser(username), HttpStatus.OK);
    }

    /**
     * Sends an email to the user whose account has been deactivated
     * @param username the username of the user to which the email will be sent
     * @return ResponseEntity with the "Email send" message and OK HTTP Status
     * @throws UserNotFoundException if the user is not found in the database
     * @throws EmailFailedException if the email is not sent
     * @throws UserAlreadyDeactivatedException if the user has already been deactivated
     */
    @Transactional
    @PostMapping("/send-deactivate-message")
    public ResponseEntity<Object> sendDeactivateMessage(@RequestBody String username) {
        return userService.sendDeactivateEmail(username);
    }

    /**
     * Sends an email to the user whose account has been activated
     * @param username the username of the user to which the email will be sent
     * @return ResponseEntity with the "Email send" message and OK HTTP Status
     * @throws UserNotFoundException if the user is not found in the database
     * @throws EmailFailedException if the email is not sent
     * @throws UserAlreadyActivatedException if the user has already been activated
     */
    @Transactional
    @PostMapping("/send-activate-message")
    public ResponseEntity<Object> sendActivateMessage(@RequestBody String username) {
        return userService.sendActivateEmail(username);
    }

    /**
     * Checks whether the user identified by the provided username or email has logged in for the first time.
     *
     * @param usernameOrEmail The username or email of the user to be checked.
     *
     * @return ResponseEntity containing a Boolean value indicating whether it's the user's first login.
     *         Returns HttpStatus.OK if the request is successful.
     *
     * @see UserService#isFirstLogin(String)
     */
    @Transactional
    @GetMapping("/is-first-login")
    public ResponseEntity<Boolean> isFirstLogin(@RequestParam(name = "usernameOrEmail") String usernameOrEmail) {
        return new ResponseEntity<>(userService.isFirstLogin(usernameOrEmail), HttpStatus.OK);
    }
}
