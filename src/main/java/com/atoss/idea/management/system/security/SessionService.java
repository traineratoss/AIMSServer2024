package com.atoss.idea.management.system.security;


import com.atoss.idea.management.system.security.token.TokenConfig;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Log4j2
@RequiredArgsConstructor
public class SessionService {

    @Value("${aims.app.sessionIdHeader}")
    private String sessionIDHeader;

    /**
     * Creates a cookie containing the provided token with the identifier given as a parameter.
     * The cookie's properties depend on the token configuration.
     * @param sessionId     The session id for a user
     * @param token         The token to be stored in the cookie
     * @param tokenConfig   Sets the cookie's properties based on the token's type
     * @return              The ResponseCookie containing the token
     */
    public ResponseCookie createTokenCookie(String sessionId, String token, TokenConfig tokenConfig) {
        return ResponseCookie.from(getIdentifier(tokenConfig.getType(), sessionId), token)
                .httpOnly(true)
                .secure(false)
                .path(tokenConfig.getPath())
                .maxAge(tokenConfig.getExpirySeconds())
                .build();
    }

    /**
     * Recovers a token from a request based on its type if any match is found.
     *
     * @param request       A Cookie[] in which to perform the search for the token.
     * @param tokenConfig   Type of token recovered (eg. accessToken or refreshToken)
     * @return              An Optional containing the token if it's found, otherwise an empty Optional
     */
    public Optional<String> extractToken(HttpServletRequest request, TokenConfig tokenConfig) {
        String tokenIdentifier = getIdentifier(tokenConfig.getType(), extractSessionHeader(request));

        Optional<String> token = getTokenFromCookies(request.getCookies(), tokenIdentifier);

        if (token.isPresent()) {
            if (log.isInfoEnabled()) {
                log.info("Token successfully extracted, token identifier: {}", tokenIdentifier);
            }
        } else {
            if (log.isWarnEnabled()) {
                log.warn("Token not found, token identifier: {}", tokenIdentifier);
            }
        }

        return token;
    }

    private Optional<String> getTokenFromCookies(Cookie[] cookies, String identifier) {

        if (cookies == null) {
            if (log.isWarnEnabled()) {
                log.warn("No cookies found for identifier: {}", identifier);
            }
            return Optional.empty();
        }

        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(identifier)) {
                if (log.isInfoEnabled()) {
                    log.info("Token found in cookie with identifier: {}", identifier);
                }
                return Optional.of(cookie.getValue());
            }
        }

        if (log.isWarnEnabled()) {
            log.warn("Token not found in cookies for identifier: {}", identifier);
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
        String identifier;
        if (sessionId != null) {
            identifier = type + sessionId;
            if (log.isDebugEnabled()) {
                log.debug("Generated identifier with sessionId: {} -> {}", sessionId, identifier);
            }
        } else {
            identifier = type;
            if (log.isDebugEnabled()) {
                log.debug("Generated identifier without sessionId -> {}", identifier);
            }
        }
        return identifier;
    }


    /**
     * Extracts the session id from a HttpServletRequest
     * @param request The request containing the session header
     * @return The session id from the header
     */
    public String extractSessionHeader(HttpServletRequest request) {

        String sessionHeaderValue = request.getHeader(sessionIDHeader);

        if (log.isDebugEnabled()) {
            log.debug("Extracted session header value: {} for header: {}", sessionHeaderValue, sessionIDHeader);
        }

        return sessionHeaderValue;
    }

}