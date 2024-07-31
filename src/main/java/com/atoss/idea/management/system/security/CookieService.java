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

    public ResponseCookie createRefreshTokenCookie(String token) {
        return ResponseCookie.from("refreshToken", token)
                .httpOnly(true)
                .secure(false)
                .path("/api/v1/auth")
                .maxAge(maxRefreshTokenCookieAgeSeconds)
                .build();
    }

    public ResponseCookie createAccessTokenCookie(String token) {
        return ResponseCookie.from("accessToken", token)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(maxAccessTokenCookieAgeSeconds)
                .build();
    }

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