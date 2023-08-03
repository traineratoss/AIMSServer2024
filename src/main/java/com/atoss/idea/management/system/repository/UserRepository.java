package com.atoss.idea.management.system.repository;

import com.atoss.idea.management.system.repository.entity.Role;
import com.atoss.idea.management.system.repository.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    /**
     * Retrieves a User entity from the database based on either the username or email.
     *
     * This method searches for a User entity in the database that matches either the provided username or email.
     * If a match is found, it returns the User wrapped in an Optional. Otherwise, it returns an empty Optional.
     *
     * @param username The username to search for.
     * @param email    The email address to search for.
     * @return An Optional containing the User entity if a match is found, or an empty Optional if no match is found.
     */
    Optional<User> findByUsernameOrEmail(String username, String email);

    /**
     * Retrieves a User entity from the database based on the username.
     *
     * This method searches for a User entity in the database that matches the provided username.
     * If a match is found, it returns the User wrapped in an Optional. Otherwise, it returns an empty Optional.
     *
     * @param username The username to search for.
     * @return An Optional containing the User entity if a match is found, or an empty Optional if no match is found.
     */
    Optional<User> findByUsername(String username);

    /**
     * Retrieves a list of User entities from the database based on the role.
     *
     * This method searches for User entities in the database that have the specified role.
     * It returns a list of all User entities that match the given role.
     *
     * @param role The role to search for.
     * @return A list of User entities with the specified role.
     */
    List<User> findUserByRole(Role role);

    /**
     * Retrieves a User entity from the database based on the email address.
     *
     * This method searches for a User entity in the database that matches the provided email address.
     * If a match is found, it returns the User wrapped in an Optional. Otherwise, it returns an empty Optional.
     *
     * @param email The email address to search for.
     * @return An Optional containing the User entity if a match is found, or an empty Optional if no match is found.
     */
    Optional<User> findByEmail(String email);

    /**
     * Retrieves a paginated list of User entities from the database whose username starts with the provided string.
     *
     * This method searches for User entities in the database that have a username starting with the specified string.
     * It returns a Page containing a subset of User entities that match the given username prefix, based on the provided Pageable.
     *
     * @param username The prefix of the username to search for.
     * @param pageable The Pageable object used for pagination and sorting.
     * @return A Page containing a subset of User entities with usernames that start with the specified string.
     */
    Page<User> findByUsernameStartsWith(String username, Pageable pageable);
}
