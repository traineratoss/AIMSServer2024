package com.atoss.idea.management.system.security;


import jakarta.servlet.http.Cookie;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CookieService {

    @Value("${aims.app.cookie.maxAgeAccessTokenCookieSeconds}")
    private long maxAccessTokenCookieAgeSeconds;

    @Value("${aims.app.cookie.maxAgeRefreshTokenCookieSeconds}")
    private long maxRefreshTokenCookieAgeSeconds;

    /**
     * Creates a refresh token cookie.
     * @param token  The refresh token to be set in the cookie
     * @return  A refresh token cookie
     */
    public ResponseCookie createRefreshTokenCookie(String token) {
        return ResponseCookie.from("refreshToken", token)
                .httpOnly(true)
                .secure(false)
                .path("/api/v1/auth")
                .maxAge(maxRefreshTokenCookieAgeSeconds)
                .build();
    }

    /**
     * Creates an access token cookie
     * @param token The access token to be set in the cookie
     * @return    An access token cookie
     */

    public ResponseCookie createAccessTokenCookie(String token) {
        return ResponseCookie.from("accessToken", token)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(maxAccessTokenCookieAgeSeconds)
                .build();
    }

    /**
     * Recovers a token from a cookie array based on its name if any match is found.
     *
     * @param cookies       A Cookie[] in which to perform the search for the token.
     * @param identifier    Type of token recovered (eg. accessToken or refreshToken)
     * @return              An Optional containing the token if it's found, otherwise an empty Optional
     */
    public Optional<String> getTokenFromCookies(Cookie[] cookies, String identifier) {

        if (cookies == null) {
            return Optional.empty();
        }

        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(identifier)) {
                return Optional.of(cookie.getValue());
            }
        }

        return Optional.empty();
    }

}