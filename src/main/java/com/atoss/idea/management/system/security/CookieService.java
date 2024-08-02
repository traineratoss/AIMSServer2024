package com.atoss.idea.management.system.security;


import com.atoss.idea.management.system.security.token.TokenConfig;
import jakarta.servlet.http.Cookie;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CookieService {

    /**
     * Creates a cookie containing the provided token with the identifier given as a parameter.
     * The cookie's properties depend on the token configuration.
     * @param identifier    The cookie's identifier
     * @param token         The token to be stored in the cookie
     * @param tokenConfig   Sets the cookie's properties based on the token's type
     * @return              The ResponseCookie containing the token
     */
    public ResponseCookie createTokenCookie(String identifier, String token, TokenConfig tokenConfig) {
        return ResponseCookie.from(identifier, token)
                .httpOnly(true)
                .secure(false)
                .path(tokenConfig.getPath())
                .maxAge(tokenConfig.getExpirySeconds())
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

    /**
     * Provides the identifier for a cookie based on the token's type and session's id.
     * @param type The type of the token (e.g. accessToken, refreshToken)
     * @param sessionId The id of the current user's session
     * @return The cookie's identifier
     */
    public String getIdentifier(String type, String sessionId) {
        if (sessionId != null) {
            return type + sessionId;
        }
        return type;
    }

}