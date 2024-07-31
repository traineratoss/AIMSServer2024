package com.atoss.idea.management.system.security;

import com.atoss.idea.management.system.exception.InvalidRefreshTokenException;
import com.atoss.idea.management.system.exception.RefreshTokenExpiredException;
import com.atoss.idea.management.system.exception.UserNotFoundException;
import com.atoss.idea.management.system.repository.RefreshTokenRepository;
import com.atoss.idea.management.system.repository.UserRepository;
import com.atoss.idea.management.system.repository.dto.UserSecurityDTO;
import com.atoss.idea.management.system.repository.entity.RefreshToken;
import com.atoss.idea.management.system.security.response.AuthResponse;
import jakarta.servlet.http.Cookie;
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

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token);
            throw new RefreshTokenExpiredException("Expired refresh token");
        }
        return token;
    }

    public AuthResponse refreshAuthToken(HttpServletRequest request, HttpServletResponse response) {

        String token = null;

        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (cookie.getName().equals("refreshToken")) {
                    token = cookie.getValue();
                }
            }
        }

        if (token == null) {
            throw new InvalidRefreshTokenException("Invalid refresh token");
        }

        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new InvalidRefreshTokenException("Invalid refresh token"));

        verifyExpiration(refreshToken);
        refreshTokenRepository.delete(refreshToken);

        String username = refreshToken.getUser().getUsername();

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
}