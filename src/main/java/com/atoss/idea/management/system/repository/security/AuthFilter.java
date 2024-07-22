package com.atoss.idea.management.system.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

@Log4j2
public class AuthFilter extends OncePerRequestFilter {

    /**
     * Performs filtering on the incoming HTTP request and response to set user information as a cookie.
     *
     * @param request      The HttpServletRequest object representing the incoming request.
     * @param response     The HttpServletResponse object representing the response to be sent to the client.
     * @param filterChain  The FilterChain to continue the filter chain with the next filter or servlet in the chain.
     *
     * @throws ServletException If a general servlet exception occurs during the processing of the request or response.
     * @throws IOException      If an input or output exception occurs during the processing of the request or response.
     *
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
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        //        if (authentication != null && authentication.isAuthenticated()) {
        //            String username = authentication.getName();
        //            String role = authentication.getAuthorities().iterator().next().getAuthority();
        //            String email = authentication.getAuthorities().iterator().next().getAuthority();
        //            String fullName = authentication.getAuthorities().iterator().next().getAuthority();
        //            String avatarId = authentication.getAuthorities().iterator().next().getAuthority();
        //            Cookie userCookie = new Cookie("user_info", username + ":" + role + ":" + email + ":" + fullName + ":" + avatarId);
        //            userCookie.setMaxAge(3600);
        //            response.addCookie(userCookie);
        //        }
        filterChain.doFilter(request, response);
    }

}
