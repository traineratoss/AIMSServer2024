package com.atoss.idea.management.system.security;

import com.atoss.idea.management.system.exception.InvalidRefreshTokenException;
import com.atoss.idea.management.system.exception.RefreshTokenExpiredException;
import com.atoss.idea.management.system.exception.UserNotFoundException;
import com.atoss.idea.management.system.repository.RefreshTokenRepository;
import com.atoss.idea.management.system.repository.UserRepository;
import com.atoss.idea.management.system.repository.dto.UserSecurityDTO;
import com.atoss.idea.management.system.repository.entity.RefreshToken;
import com.atoss.idea.management.system.repository.entity.User;
import com.atoss.idea.management.system.security.response.AuthResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    @Value("${aims.app.jwt.refreshTokenExpirationMs}")
    private Long expirationMs;

    private final RefreshTokenRepository refreshTokenRepository;

    private final UserRepository userRepository;

    private final JwtService jwtService;
    private final CookieService cookieService;

    /**
     * Creates a new refresh token for the specified username
     *
     * @param username The username for which the refresh token is to be created.
     * @return the newly created {@link RefreshToken} instance.
     * @throws UserNotFoundException If no user is found with the given username.
     */

    public RefreshToken createRefreshToken(String username) {
        refreshTokenRepository.findByUser_Username(username).ifPresent(refreshTokenRepository::delete);
        refreshTokenRepository.flush();

        RefreshToken refreshToken = RefreshToken.builder()
                .user(userRepository
                        .findByUsername(username)
                        .orElseThrow(() -> new UserNotFoundException("User not found")))
                .token(UUID.randomUUID().toString())
                .expiryDate(Instant.now().plusMillis(expirationMs))
                .build();

        return refreshTokenRepository.save(refreshToken);
    }

    /**
     * Finds a refresh token by its token value.
     *
     * @param token The token value of the refresh token to be found
     * @return Return a {@link Optional} containing the found {@link RefreshToken} if present, otherwise {@link Optional#empty()}
     */
    public Optional<RefreshToken> findByToken(String token) {
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
            refreshTokenRepository.delete(token);
            throw new RefreshTokenExpiredException(token, "Expired refresh token");
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

        Optional<String> token = cookieService.getTokenFromCookies(request.getCookies(), "refreshToken");

        if (token.isEmpty()) {
            throw new InvalidRefreshTokenException("Invalid refresh token");
        }

        RefreshToken refreshToken = refreshTokenRepository.findByToken(token.get())
                .orElseThrow(() -> new InvalidRefreshTokenException("Invalid refresh token"));

        verifyExpiration(refreshToken);

        Optional<User> userOptional = userRepository.findUserByRefreshToken(refreshToken);

        User user;
        if (userOptional.isPresent()) {
            user = userOptional.get();
        } else {
            throw new UserNotFoundException("User not found " + refreshToken.getUser().getUsername());
        }

        String username = user.getUsername();

        refreshTokenRepository.delete(refreshToken);

        String newAccessToken = jwtService.generateToken(username);
        RefreshToken newRefreshToken = createRefreshToken(username);

        response.addHeader(HttpHeaders.SET_COOKIE, cookieService
                .createAccessTokenCookie((newAccessToken)).toString());

        response.addHeader(HttpHeaders.SET_COOKIE, cookieService
                .createRefreshTokenCookie(newRefreshToken.getToken()).toString());


        return new AuthResponse(
                jwtService.extractExpiration(newAccessToken),
                Date.from(newRefreshToken.getExpiryDate()),
                new UserSecurityDTO());

    }

    /**
     * Invalidates a refresh token by deleting it from the repository.
     *
     * @param token The refresh token to be deleted.
     */
    public void invalidateToken(String token) {
        refreshTokenRepository.findByToken(token).ifPresent(refreshTokenRepository::delete);
    }

}
