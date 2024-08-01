package com.atoss.idea.management.system.repository;

import com.atoss.idea.management.system.repository.entity.BlacklistedAccessToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.Optional;

public interface BlacklistedAccessTokenRepository extends JpaRepository<BlacklistedAccessToken, Long> {

    /**
     * Deletes all blacklisted tokens older than a specified date.
     * @param date The date until all blacklisted tokens will be removed
     */
    void deleteAllByExpiryLessThan(Date date);

    /**
     * Retrieves a BlacklistedAccessToken entry from the database based on a token.
     *
     * @param token The token used to retrieve a BlacklistedAccessToken
     * @return      An Optional containing the BlacklistedAccessToken if found
     *              and an empty Optional if no entry is found.
     */
    Optional<BlacklistedAccessToken> findByToken(String token);
}
