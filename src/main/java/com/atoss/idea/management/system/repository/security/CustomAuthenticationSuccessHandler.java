package com.atoss.idea.management.system.repository.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import jakarta.servlet.http.Cookie;

public class CustomAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    /**
     * Called when a user successfully authenticates during the login process.
     *
     * @param request        The HttpServletRequest object representing the incoming request.
     * @param response       The HttpServletResponse object representing the response to be sent to the client.
     * @param authentication The Authentication object representing the user's authentication details.
     *
     * @throws IOException   If an input or output exception occurs during the processing of the request or response.
     * @throws ServletException If a general servlet exception occurs during the processing of the request or response.
     *
     * @see HttpServletRequest
     * @see HttpServletResponse
     * @see Authentication
     * @see Cookie
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        //        String username = authentication.getName();
        //        String role = authentication.getAuthorities().iterator().next().getAuthority();
        //        String email = authentication.getAuthorities().iterator().next().getAuthority();
        //        Cookie userCookie = new Cookie("user_info", username + ":" + role + ":" + email);
        //        userCookie.setMaxAge(3600); // Set cookie expiry time (in seconds). Adjust as needed.
        //        response.addCookie(userCookie);
        super.onAuthenticationSuccess(request, response, authentication);
    }
}