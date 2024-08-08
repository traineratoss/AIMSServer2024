package com.atoss.idea.management.system.controller;

import com.atoss.idea.management.system.exception.*;
import com.atoss.idea.management.system.repository.dto.*;
import com.atoss.idea.management.system.service.SendEmailService;
import com.atoss.idea.management.system.service.UserService;
import jakarta.transaction.Transactional;
import lombok.extern.log4j.Log4j2;
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

@RestController
@Log4j2
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private final SendEmailService sendEmailService;

    //add a new comment
    /**
     * Constructs a new instance of the UserController with the provided UserService.
     *
     * @param userService      The UserService instance to be used by the UserController.
     * @param sendEmailService  The SendEmailService instance to be used by the UserController.
     * @see UserService
     */
    public UserController(UserService userService, SendEmailService sendEmailService) {
        this.userService = userService;
        this.sendEmailService = sendEmailService;
    }


    /**
     * Send email to all admins.
     * @param username The unique parameter representing the username of the new user.
     * @return A ResponseEntity with a String that confirm the email was sent.
     */
    @Transactional
    @PostMapping("/send-email-to-admin")
    public ResponseEntity<String> sendEmailToAdmins(@RequestParam(name = "username") String username) {
        if (log.isInfoEnabled()) {
            log.info("Received request to send email to all admins");
        }
        sendEmailService.sendEmailToAdmins(username);
        if (log.isInfoEnabled()) {
            log.info("Send email to all admins successfully");
        }
        return new ResponseEntity<>("Email send to all admins!", HttpStatus.OK);
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
        if (log.isInfoEnabled()) {
            log.info("Received request to updated user by username");
        }
        UserResponseDTO userResponseDTO = userService.updateUserByUsername(username, userUpdateDTO);
        if (log.isInfoEnabled()) {
            log.info("Successfully updated user by username");
        }
        return userResponseDTO;
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
        if (log.isInfoEnabled()) {
            log.info("Received request to updated user role");
        }
        UserResponseDTO userResponseDTO = userService.updateUserRole(username);
        if (log.isInfoEnabled()) {
            log.info("Successfully updated user role");
        }
        return userResponseDTO;
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
    public ResponseEntity<UserPageDTO> getAllUsersForAdmin(@RequestParam(required = true) int pageSize,
                                                           @RequestParam(required = true) int pageNumber,
                                                           @RequestParam(required = true) String sortCategory) {
        if (log.isInfoEnabled()) {
            log.info("Received request to retrieved all users for admin");
        }
        UserPageDTO userPageDTO = userService.getAllUsersForAdmin(
                PageRequest.of(
                        pageNumber,
                        pageSize,
                        Sort.by(Sort.Direction.ASC, sortCategory)
                )
        );
        if (log.isInfoEnabled()) {
            log.info("Retrieved all users for admin successfully");
        }

        return new ResponseEntity<>(userPageDTO, HttpStatus.OK);
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
    public ResponseEntity<Page<UserResponseDTO>> getAllUsers(@RequestParam(required = true) int pageSize,
                                                             @RequestParam(required = true) int pageNumber,
                                                             @RequestParam(required = true) String sortCategory) {

        if (log.isInfoEnabled()) {
            log.info("Received request to retrieved all users");
        }

        Page<UserResponseDTO> userResponseDTOPage = userService.getAllUsers(
                PageRequest.of(
                        pageNumber,
                        //10
                        pageSize,
                        Sort.by(Sort.Direction.ASC, sortCategory)
                )
        );
        if (log.isInfoEnabled()) {
            log.info("Retrieved all users successfully");
        }
        return new ResponseEntity<>(userResponseDTOPage, HttpStatus.OK);
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

        if (log.isInfoEnabled()) {
            log.info("Received request to retrieved all users by username");
        }
        UserPageDTO userPageDTO = userService.getAllUsersByUsernamePageable(
                PageRequest.of(
                        pageNumber,
                        pageSize,
                        Sort.by(Sort.Direction.ASC, sortCategory)
                ),
                username
        );
        if (log.isInfoEnabled()) {
            log.info("Retrieved all users by username successfully");
        }

        return new ResponseEntity<>(userPageDTO, HttpStatus.OK);
    }

    /**
     * Changes the password for the user.
     *
     * @param changePasswordDTO The ChangePasswordDTO object containing the necessary information for password change.
     * @return A ResponseEntity representing the result of the password change operation.
     *         If the password is successfully changed, the response entity has an HTTP status of 200 (HttpStatus.OK).
     *         If the password change operation fails, the response entity has an HTTP status of 400 (HttpStatus.BAD_REQUEST).
     *
     * @see ChangePasswordDTO - Must contain the username of the user who aborts the password recovery.
     * @see ResponseEntity
     * @see UserService#changePassword(ChangePasswordDTO)
     */
    @Transactional
    @PostMapping("/change-password")
    public ResponseEntity<Object> changePassword(@RequestBody ChangePasswordDTO changePasswordDTO) {
        if (log.isInfoEnabled()) {
            log.info("Received request to changed password");
        }

        userService.changePassword(changePasswordDTO);

        if (log.isInfoEnabled()) {
            log.info("Password successfully changed");
        }
        return new ResponseEntity<>(HttpStatus.OK);
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
        if (log.isInfoEnabled()) {
            log.info("Received request to send approve email");
        }
        ResponseEntity<Object> responseEntity = userService.sendApproveEmail(username);
        if (log.isInfoEnabled()) {
            log.info("Approval email successfully sent");
        }
        return responseEntity;
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
        if (log.isInfoEnabled()) {
            log.info("Received request to send decline email");
        }
        ResponseEntity<Object> responseEntity = userService.sendDeclineEmail(username);
        if (log.isInfoEnabled()) {
            log.info("Decline email successfully sent");
        }
        return responseEntity;
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
        if (log.isInfoEnabled()) {
            log.info("Received request to send forgot password email");
        }
        ResponseEntity<Object> responseEntity = userService.sendForgotPassword(usernameOrEmail);
        if (log.isInfoEnabled()) {
            log.info("Forgot password email successfully sent");
        }
        return responseEntity;
    }

    /**
     * Sends an email to the user whose account has been deactivated
     * @param username the username of the user to which the email will be sent
     * @return ResponseEntity with the "Email send" message and OK HTTP Status
     * @throws UserNotFoundException           if the user is not found in the database
     * @throws EmailFailedException            if the email is not sent
     * @throws UserAlreadyDeactivatedException if the user has already been deactivated
     */
    @Transactional
    @PostMapping("/send-deactivate-message")
    public ResponseEntity<Object> sendDeactivateMessage(@RequestBody String username) {
        if (log.isInfoEnabled()) {
            log.info("Received request to send deactivate message");
        }
        ResponseEntity<Object> responseEntity = userService.sendDeactivateEmail(username);
        if (log.isInfoEnabled()) {
            log.info("Deactivate email successfully sent");
        }
        return responseEntity;
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
        if (log.isInfoEnabled()) {
            log.info("Received request to send activate message");
        }
        ResponseEntity<Object> responseEntity = userService.sendActivateEmail(username);
        if (log.isInfoEnabled()) {
            log.info("Activate email successfully sent");
        }
        return responseEntity;
    }


    /**
     * Checks whether the provided OTP is valid for the specified user.
     *
     * @param verifyOtpDTO The username or email of the user to be checked and the one-time password.
     *
     * @return Returns HttpStatus.OK if the request is successful.
     *
     * @throws UserNotFoundException - if the user is not found in the database
     * @throws BadCredentialsException - if no OTP password has been generated for the specified user, the OTP password is invalid, or has expired.
     *
     * @see UserService#verifyOTP(VerifyOtpDTO)
     */
    @Transactional
    @PostMapping("/verify-otp")
    public ResponseEntity<UserSecurityDTO> verifyOTP(@RequestBody VerifyOtpDTO verifyOtpDTO) {
        if (log.isInfoEnabled()) {
            log.info("Received request to verify one-time password");
        }
        UserSecurityDTO userSecurityDTO = userService.verifyOTP(verifyOtpDTO);
        if (log.isInfoEnabled()) {
            log.info("Verify one-time password");
        }
        return new ResponseEntity<>(userSecurityDTO, HttpStatus.OK);
    }

    /**
     * Resets the user's state if the change password process is aborted.
     *
     * @param changePasswordDTO the data transfer object containing the details for the password change
     * @return Returns HttpStatus.OK if the request is successful.
     * @throws UserNotFoundException if the user is not found in the database
     * @see UserService#abortChangePassword(ChangePasswordDTO)
     */
    @PostMapping("/change-password/abort")
    public ResponseEntity<Object> abortChangePassword(@RequestBody ChangePasswordDTO changePasswordDTO) {
        if (log.isInfoEnabled()) {
            log.info("Received request to abort password change");
        }
        userService.abortChangePassword(changePasswordDTO);
        if (log.isInfoEnabled()) {
            log.info("Password change successfully aborted");
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Retrieves the avatar of a user by their username.
     *
     * @param username the username of the user whose avatar is to be retrieved.
     * @return a ResponseEntity containing the ImageDTO of the user's avatar and an HTTP status of OK.
     */
    @GetMapping("/get-avatar-by-username")
    public ResponseEntity<ImageDTO> getAvatarByUsername(@RequestParam(name = "username") String username) {
        return new ResponseEntity<>(userService.getAvatarByUsername(username), HttpStatus.OK);
    }
}
