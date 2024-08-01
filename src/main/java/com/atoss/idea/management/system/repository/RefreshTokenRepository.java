package com.atoss.idea.management.system.repository;

import com.atoss.idea.management.system.repository.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    /**
     * Retrieves a RefreshToken entity from the database based on the token value.
     * If a match is found, it returns the RefreshToken wrapped in an Optional. Otherwise, it returns an empty Optional.
     *
     * @param token The token to search for.
     * @return An Optional containing the RrefreshToken entity if a match is found,
     *         or an empty Optional if no match is found.
     */
    Optional<RefreshToken> findByToken(String token);

    /**
     * Retrieves a RefreshToken entity from the database based on the username of the user associated with the token.
     * If a match is found, it returns the RefreshToken wrapped in an Optional. Otherwise, it returns an empty Optional.
     *
     * @param username The username of the user associated with the token.
     * @return An Optional containing the RrefreshToken entity if a match is found,
     *         or an empty Optional if no match is found.
     */
    Optional<RefreshToken> findByUser_Username(String username);
}
