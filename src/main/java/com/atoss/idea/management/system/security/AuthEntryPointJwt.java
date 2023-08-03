package com.atoss.idea.management.system.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
@Log4j2
public class AuthEntryPointJwt implements AuthenticationEntryPoint {

    /**
     * Called when authentication fails, and the user is not authenticated.
     *
     * @param request        The HttpServletRequest object representing the incoming request.
     * @param response       The HttpServletResponse object representing the response to be sent to the client.
     * @param authException  The AuthenticationException representing the cause of the authentication failure.
     *
     * @throws IOException   If an input or output exception occurs during the processing of the request or response.
     * @throws ServletException If a general servlet exception occurs during the processing of the request or response.
     *
     * @see HttpServletRequest
     * @see HttpServletResponse
     * @see AuthenticationException
     * @see HttpServletResponse#SC_UNAUTHORIZED
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException, ServletException {
        log.error("Unauthorized error: {}", authException.getMessage());

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        final Map<String, Object> body = new HashMap<>();
        body.put("status", HttpServletResponse.SC_UNAUTHORIZED);
        body.put("error", "Unauthorized");
        body.put("message", authException.getMessage());
        body.put("path", request.getServletPath());
        final ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(response.getOutputStream(), body);
    }
}
