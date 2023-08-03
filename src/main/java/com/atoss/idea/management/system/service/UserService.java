package com.atoss.idea.management.system.service;

import com.atoss.idea.management.system.exception.*;
import com.atoss.idea.management.system.repository.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

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
     * This method retrieves the user from the repository based on the provided username and updates the user's information according to the fields specified in the UserUpdateDTO object.
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

    ResponseEntity<Object> sendApproveEmail(String username);

    ResponseEntity<Object> sendDeclineEmail(String user);

    UserSecurityDTO login(String usernameOrEmail, String password);

    ResponseEntity<UserResponseDTO> sendForgotPassword(String usernameOrEmail);

    Boolean deleteUser(String username);

    ResponseEntity<Object> sendDeactivateEmail(String user);

    ResponseEntity<Object> sendActivateEmail(String user);


}
