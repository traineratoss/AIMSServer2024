package com.atoss.idea.management.system.repository;

import com.atoss.idea.management.system.repository.entity.Session;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SessionRepository extends JpaRepository<Session, String> {

    /**
     * Retrieves a Session entity from the database based on the username of the user associated with the session.
     * If a match is found, it returns the Session wrapped in an Optional. Otherwise, it returns an empty Optional.
     *
     * @param username The username of the user associated with the token.
     * @return An Optional containing the Session entity if a match is found,
     *         or an empty Optional if no match is found.
     */
    Optional<Session> findByUser_Username(String username);
}
