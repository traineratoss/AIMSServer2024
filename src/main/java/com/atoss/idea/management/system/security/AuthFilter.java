package com.atoss.idea.management.system.security;

import com.atoss.idea.management.system.exception.RefreshTokenExpiredException;
import com.atoss.idea.management.system.security.token.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Log4j2
public class AuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    private final UserDetailsService userDetailsService;

    private final SessionService sessionService;

    /**
     * Performs filtering on the incoming HTTP request and response to set user information as a cookie.
     *
     * @param request     The HttpServletRequest object representing the incoming request.
     * @param response    The HttpServletResponse object representing the response to be sent to the client.
     * @param filterChain The FilterChain to continue the filter chain with the next filter or servlet in the chain.
     * @throws ServletException If a general servlet exception occurs during the processing of the request or response.
     * @throws IOException      If an input or output exception occurs during the processing of the request or response.
     * @see HttpServletRequest
     * @see HttpServletResponse
     * @see FilterChain
     * @see SecurityContextHolder
     * @see Authentication
     * @see Cookie
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        try {
            sessionService.extractToken(request, jwtService.getTokenConfig())
                    .ifPresent(
                            token -> {
                                if (request.getRequestURI().contains("/api/v1/auth/")) {
                                    return;
                                }

                                String username = jwtService.extractUsername(token);

                                if (username != null) {
                                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                                    if (jwtService.validateToken(token, userDetails)) {
                                        UsernamePasswordAuthenticationToken authenticationToken =
                                                new UsernamePasswordAuthenticationToken(
                                                        userDetails,
                                                        null,
                                                        userDetails.getAuthorities());
                                        authenticationToken.setDetails(new WebAuthenticationDetailsSource()
                                                .buildDetails(request));
                                        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                                    }
                                }
                            }
                );

            filterChain.doFilter(request, response);

        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            log.info("Tried authenticating with access token expired at {}", e.getClaims().getExpiration());
        } catch (RefreshTokenExpiredException e) {
            log.info("Tried authenticating with refresh token expired at {}", e.getRefreshToken().getExpiryDate());
        }
    }

}
