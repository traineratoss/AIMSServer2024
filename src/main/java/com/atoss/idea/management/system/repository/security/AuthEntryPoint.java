package com.atoss.idea.management.system.repository.security;

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
public class AuthEntryPoint implements AuthenticationEntryPoint {

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
        // Log an error message with the details of the unauthorized error
        log.error("Unauthorized error: {}", authException.getMessage());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);  // Set the response content type to JSON
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);    // Set the response status to 401 Unauthorized
        final Map<String, Object> body = new HashMap<>();           // Create a map to hold the response body data
        body.put("status", HttpServletResponse.SC_UNAUTHORIZED);    // Add status information to the response body
        body.put("error", "Unauthorized");                          // Add error type information to the response body
        body.put("message", authException.getMessage());            // Add the error message to the response body
        body.put("path", request.getServletPath());                 // Add the request path to the response body
        // Create an ObjectMapper instance to serialize the response body map to JSON
        final ObjectMapper mapper = new ObjectMapper();
        // Write the serialized JSON response body to the output stream of the response
        mapper.writeValue(response.getOutputStream(), body);
    }
}
