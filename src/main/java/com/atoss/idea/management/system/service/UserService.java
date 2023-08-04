package com.atoss.idea.management.system.service;

import com.atoss.idea.management.system.exception.*;
import com.atoss.idea.management.system.repository.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.security.authentication.BadCredentialsException;

@Service
public interface UserService {
    /**
     * Adds a new user with the provided username and email
     *
     * This method creates a new user entity with the given username and email, setting the 'isActive' and 'hasPassword' flags to false initially.
     *
     * @param username the username for the new user
     * @param email the email for the new user
     * @return the dto representation of the newly created user entity
     * @throws UserAlreadyExistException if a user with the same username or email already exists in the system
     */
    UserResponseDTO addUser(String username, String email);

    /**
     * Updates the information of a user identified by the given username
     *
     * This method retrieves the user from the repository based on the provided username and updates the user's information
     * according to the fields specified in the UserUpdateDTO object.
     *
     * @param username the username of the user whose information is to be updated
     * @param userUpdateDTO the new information about user
     * @return the dto representation of the updated user entity
     * @throws UserNotFoundException if a user with the given username doesn't exist in the system
     * @throws UsernameAlreadyExistException if a user with the same username already exists in the system
     * @throws EmailAlreadyExistException if a user with the same email already exists in the system
     * @throws AvatarNotFoundException if the provided avatarId does not correspond to any avatar in the Avatar repository
     */
    UserResponseDTO updateUserByUsername(String username, UserUpdateDTO userUpdateDTO);

    /**
     * Updates the user's role
     *
     * This method retrieves the user from the repository based on the provided username and updates the user's role.
     * If the current role is ADMIN, it will be changed to STANDARD, and vice versa.
     *
     * @param username the username of the user whose role is to be updated
     * @return the dto representation of the updated user entity
     * @throws UserNotFoundException if a user with the given username doesn't exist in the system
     */
    UserResponseDTO updateUserRole(String username);

    /**
     * Retrieves a user's information based on the provided username.
     *
     * This method fetches the user from the repository using the given username and returns the DTO representation of the user's information.
     *
     * @param username the username of the user to be retrieved
     * @return the dto representation of the user entity
     * @throws UserNotFoundException if a user with the given username doesn't exist in the system
     */
    UserResponseDTO getUserByUsername(String username);

    /**
     * Retrieves a paginated list of all the users with summarized information for administrative purposes
     *
     * @param pageable for defining the pagination parameters such as pageSize, pageNumber and sortCategory
     * @return a UserPageDTO containing the total pages of users and a paginated list of UserAdminDashboardResponseDTO
     */

    UserPageDTO getAllUsersForAdmin(Pageable pageable);

    /**
     * Retrieves a paginated list of all the users from the system
     *
     * @param pageable for defining the pagination parameters such as pageSize, pageNumber and sortCategory
     * @return the page containing the paginated list of UserResponseDTO objects
     */
    Page<UserResponseDTO> getAllUsers(Pageable pageable);

    /**
     * Retrieves a paginated list of the users whose username starts with the provided text, excluding the current user
     *
     * @param pageable for defining the pagination parameters such as pageSize, pageNumber and sortCategory
     * @param username the value that the usernames of all the users should start with
     * @param currentUsername the value which should be excluded from the list
     * @return a UserPageDTO containing the total pages of users and a paginated list of UserAdminDashboardResponseDTO filtered by their username
     */
    UserPageDTO getAllUsersByUsernamePageable(Pageable pageable, String username, String currentUsername);

    //    Page<UserAdminDashboardResponseDTO> getAllUsersByUsername(String username);

    /**
     * Retrieves a paginated list of all the users based on their activation status (active or inactive)
     *
     * @param isActive a flag which indicates whether to include active or inactive users.
     * @param pageable for defining the pagination parameters such as pageSize, pageNumber and sortCategory
     * @return the Page object containing the paginated list of UserResponseDTO objects filtered by the 'isActive' flag
     */
    Page<UserResponseDTO> getAllPendingUsers(boolean isActive, Pageable pageable);

    /**
     * Changes the password of a user
     *
     * @param changePasswordDTO the dto containing the username, the old password and the new password
     * @return true if the password change is successful (old password is verified and the new password is saved), false otherwise.
     * @throws UserNotFoundException if a user with the given username doesn't exist in the system
     */
    boolean changePassword(ChangePasswordDTO changePasswordDTO);

    /**
     * Sends an approval email to the user with the specified username.
     *
     * @param username The username of the user to whom the approval email will be sent.
     *
     * @return A ResponseEntity representing the result of sending the approval email.
     *
     * @throws UserNotFoundException when the user was not found in the database
     * @throws ApproveAlreadyGrantedException when the user's request was already approved
     *
     * @see ResponseEntity
     */
    ResponseEntity<Object> sendApproveEmail(String username);

    /**
     * Sends a decline email to the user with the specified information.
     *
     * @param user The information of the user for whom the decline email will be sent.
     *
     * @return A ResponseEntity representing the result of sending the decline email.
     *
     * @throws UserNotFoundException        If the user specified in the user information is not found in the system's database.
     * @throws ApproveAlreadyGrantedException If the user's request has already been approved, and the decline email cannot be sent.
     * @throws UserAlreadyActivatedException   If the user has already been activated, and the decline email cannot be sent.
     *
     * @see ResponseEntity
     * @see UserNotFoundException
     * @see ApproveAlreadyGrantedException
     * @see UserAlreadyActivatedException
     */
    ResponseEntity<Object> sendDeclineEmail(String user);

    /**
     * Authenticates a user's login credentials and returns security-related information.
     *
     * @param usernameOrEmail The username or email of the user trying to log in.
     * @param password        The password provided by the user for authentication.
     *
     * @return A UserSecurityDTO object containing security-related information after successful authentication.
     *         If the login credentials are valid, the UserSecurityDTO object will hold security-related data,
     *         such as access tokens, refresh tokens, or other security-related information.
     *         If the login credentials are invalid, the method may return null or throw an exception, depending on the implementation.
     *
     * @throws UserAlreadyDeactivatedException if the user account is deactivated
     * @throws BadCredentialsException if the credentials entered are not valid
     *
     * @see ResponseEntity
     */
    UserSecurityDTO login(String usernameOrEmail, String password);

    /**
     * Sends a forgot password email to the user with the specified username or email.
     *
     * @param usernameOrEmail The username or email of the user for whom the forgot password email will be sent.
     *
     * @return A ResponseEntity representing the result of sending the forgot password email.
     *
     * @throws UserNotFoundException if the user specified was not found in the database
     *
     * @see ResponseEntity
     */
    ResponseEntity<Object> sendForgotPassword(String usernameOrEmail);

    /**
     * Deletes a user from the system based on the provided username.
     *
     * @param username The username of the user to be deleted.
     *
     * @return true if the user is successfully deleted, false otherwise.
     *
     * @throws UserNotFoundException If the user specified in the username is not found in the system's database.
     */
    Boolean deleteUser(String username);

    /**
     * Sends a deactivate email to the user with the specified information.
     *
     * @param user The information of the user for whom the deactivate email will be sent.
     *
     * @return A ResponseEntity representing the result of sending the deactivate email.
     *
     * @throws UserNotFoundException if the user is not found in the database
     * @throws UserAlreadyDeactivatedException if the user has already been deactivated
     * @throws EmailFailedException if the email was not sent
     *
     * @see ResponseEntity
     */
    ResponseEntity<Object> sendDeactivateEmail(String user);

    /**
     * Sends an activate email to the user with the specified information.
     *
     * @param user The information of the user for whom the activate email will be sent.
     *
     * @return A ResponseEntity representing the result of sending the activate email.
     *
     * @throws UserNotFoundException if the user is not found in the database
     * @throws UserAlreadyDeactivatedException if the user has already been deactivated
     * @throws EmailFailedException if the email was not sent
     *
     * @see ResponseEntity
     */
    ResponseEntity<Object> sendActivateEmail(String user);


}
