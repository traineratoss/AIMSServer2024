package com.atoss.idea.management.system.security;


import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

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
     * Recovers a token from cookies in the request
     * @param request  The HTTP request containing the cookies
     * @param type      Type of token recovered (accessToken or refreshToken)
     * @return          Return the value of cookie if it's found, otherwise will return null
     */
    public String getTokenFromCookies(HttpServletRequest request, String type) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (cookie.getName().equals(type)) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

}