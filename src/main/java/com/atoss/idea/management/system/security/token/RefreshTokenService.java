package com.atoss.idea.management.system.security.token;

import com.atoss.idea.management.system.exception.InvalidRefreshTokenException;
import com.atoss.idea.management.system.exception.RefreshTokenExpiredException;
import com.atoss.idea.management.system.exception.UserNotFoundException;
import com.atoss.idea.management.system.repository.RefreshTokenRepository;
import com.atoss.idea.management.system.repository.UserRepository;
import com.atoss.idea.management.system.repository.dto.UserSecurityDTO;
import com.atoss.idea.management.system.repository.entity.RefreshToken;
import com.atoss.idea.management.system.repository.entity.User;
import com.atoss.idea.management.system.security.SessionService;
import com.atoss.idea.management.system.security.response.AuthResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@Log4j2
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    private final UserRepository userRepository;

    private final JwtService jwtService;
    private final SessionService sessionService;

    @Getter
    private final RefreshTokenConfig tokenConfig;

    /**
     * Creates a new refresh token for the specified username
     *
     * @param username The username for which the refresh token is to be created.
     * @return the newly created {@link RefreshToken} instance.
     * @throws UserNotFoundException If no user is found with the given username.
     */

    @Transactional
    public RefreshToken createRefreshToken(String username) {

        RefreshToken refreshToken = RefreshToken.builder()
                .user(userRepository
                        .findByUsername(username)
                        .orElseThrow(() -> {
                            if (log.isErrorEnabled()) {
                                log.error("User not found during refresh token creation: {}", username);
                            }
                            return new UserNotFoundException("User not found");
                        }))
                .token(UUID.randomUUID().toString())
                .expiryDate(Instant.now().plusMillis(tokenConfig.getExpiryMs()))
                .build();

        User user = refreshToken.getUser();
        user.getRefreshTokens().add(refreshToken);

        RefreshToken savedToken = refreshTokenRepository.save(refreshToken);
        userRepository.save(user);

        if (log.isInfoEnabled()) {
            log.info("Created and saved new refresh token: {}", savedToken.getToken());
        }

        return savedToken;
    }

    /**
     * Finds a refresh token by its token value.
     *
     * @param token The token value of the refresh token to be found
     * @return Return a {@link Optional} containing the found {@link RefreshToken} if present, otherwise {@link Optional#empty()}
     */
    public Optional<RefreshToken> findByToken(String token) {
        if (log.isInfoEnabled()) {
            log.info("Searching for refresh token");
        }
        return refreshTokenRepository.findByToken(token);
    }

    /**
     * Verifies the expiration of the provided refresh token.
     *
     * @param token The {@link RefreshToken} to be verified
     * @return The {@link RefreshToken} if it has not expired
     * @throws RefreshTokenExpiredException if the token has expired
     */
    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            if (log.isWarnEnabled()) {
                log.warn("Refresh token has expired, expiry date: {}", token.getExpiryDate());
            }
            refreshTokenRepository.delete(token);
            throw new RefreshTokenExpiredException(token, "Expired refresh token");
        }
        if (log.isInfoEnabled()) {
            log.info("Refresh token is still valid.");
        }
        return token;
    }

    /**
     * Refreshes the authentication token by issuing a new access token and refresh token
     *
     * @param request  Request the {@link HttpServletRequest} containing the current refresh token in cookies
     * @param response The {@link HttpServletResponse} to which new cookies will be added
     * @return Return an {@link AuthResponse} containing the new access token expiration date
     * @throws InvalidRefreshTokenException if the refresh token is invalid
     * @throws RefreshTokenExpiredException if the refresh token has expired
     */
    public AuthResponse refreshAuthToken(HttpServletRequest request, HttpServletResponse response) {

        Optional<String> token = sessionService.extractToken(request, tokenConfig);

        if (token.isEmpty()) {
            if (log.isErrorEnabled()) {
                log.error("Token is empty or not found");
            }
            throw new InvalidRefreshTokenException("Invalid refresh token");
        }

        RefreshToken refreshToken = refreshTokenRepository.findByToken(token.get())
                .orElseThrow(() -> {
                    if (log.isErrorEnabled()) {
                        log.error("Token not found in repository {}", token.get());
                    }
                    return new InvalidRefreshTokenException("Invalid refresh token");
                });

        if (log.isInfoEnabled()) {
            log.info("Found refresh token: {}", refreshToken.getToken());
        }
        verifyExpiration(refreshToken);

        Optional<User> userOptional = userRepository.findUserByRefreshToken(refreshToken);

        User user;
        if (userOptional.isPresent()) {
            user = userOptional.get();
            if (log.isInfoEnabled()) {
                log.info("User found: {}", user.getUsername());
            }
        } else {
            if (log.isErrorEnabled()) {
                log.error("User not found for refresh token: {}", refreshToken.getToken());
            }
            throw new UserNotFoundException("User not found " + refreshToken.getUser().getUsername());
        }

        String username = user.getUsername();

        refreshTokenRepository.delete(refreshToken);

        if (log.isDebugEnabled()) {
            log.debug("Deleted old refresh token: {}", refreshToken.getToken());
        }

        String newAccessToken = jwtService.generateToken(username);
        RefreshToken newRefreshToken = createRefreshToken(username);

        response.addHeader(HttpHeaders.SET_COOKIE, sessionService
                .createTokenCookie(
                        sessionService.extractSessionHeader(request),
                        newAccessToken,
                        jwtService.getTokenConfig())
                .toString());

        response.addHeader(HttpHeaders.SET_COOKIE, sessionService
                .createTokenCookie(
                        sessionService.extractSessionHeader(request),
                        newRefreshToken.getToken(),
                        tokenConfig)
                .toString());

        AuthResponse authResponse = new AuthResponse(
                jwtService.extractExpiration(newAccessToken),
                Date.from(newRefreshToken.getExpiryDate()),
                new UserSecurityDTO());

        if (log.isInfoEnabled()) {
            log.info("Generated new access token and refresh token");
        }
        return authResponse;

    }

    /**
     * Invalidates a refresh token by deleting it from the repository.
     *
     * @param token The refresh token to be deleted.
     */
    public void invalidateToken(String token) {

        if (log.isInfoEnabled()) {
            log.info("Trying to invalidate refresh token {}...", token);
        }

        refreshTokenRepository.findByToken(token).ifPresent(refreshTokenRepository::delete);
        if (log.isInfoEnabled()) {
            log.info("Refresh token {} invalidated successfully", token);
        }
    }

}
