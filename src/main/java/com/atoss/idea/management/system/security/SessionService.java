package com.atoss.idea.management.system.security;


import com.atoss.idea.management.system.repository.SessionRepository;
import com.atoss.idea.management.system.repository.entity.Session;
import com.atoss.idea.management.system.repository.entity.User;
import com.atoss.idea.management.system.security.token.TokenConfig;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SessionService {

    private final SessionRepository sessionRepository;

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
        return getTokenFromCookies(
                request.getCookies(),
                getIdentifier(tokenConfig.getType(), extractSessionHeader(request))
        );
    }

    private Optional<String> getTokenFromCookies(Cookie[] cookies, String identifier) {

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

    /**
     * Creates a session for a user by associating the session id to a user.
     * @param sessionId The session's id for the authenticated user
     * @param user The User entity of the user for whom the session is created
     */
    public void createSession(String sessionId, User user) {
        sessionRepository.findByUser_Username(user.getUsername()).ifPresent(sessionRepository::delete);
        sessionRepository.flush();

        Session session = new Session(
                sessionId,
                user
        );

        sessionRepository.save(session);
    }

    /**
     * Extracts the session id from a HttpServletRequest
     * @param request The request containing the session header
     * @return The session id from the header
     */
    public String extractSessionHeader(HttpServletRequest request) {
        return request.getHeader(sessionIDHeader);
    }

}