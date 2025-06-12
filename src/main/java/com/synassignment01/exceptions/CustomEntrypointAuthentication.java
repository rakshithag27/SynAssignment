package com.synassignment01.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Custom entry point for handling unauthorized access attempts.
 * Sends a 401 Unauthorized response with a JSON error message when authentication fails.
 */
@Component
public class CustomEntrypointAuthentication implements AuthenticationEntryPoint {

    /**
     * Triggered when an unauthenticated user tries to access a secured resource.
     * Responds with a JSON message and 401 status code.
     */
    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write("{\"error\": \"Unauthorized: Token is missing or invalid\"}");
    }
}
