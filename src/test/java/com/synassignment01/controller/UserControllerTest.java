package com.synassignment01.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.synassignment01.dto.*;
import com.synassignment01.exceptions.PasswordMismatchException;
import com.synassignment01.exceptions.UserAlreadyExistsException;
import com.synassignment01.security.JWTUtil;
import com.synassignment01.service.UserService;
import org.springframework.security.core.Authentication;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@Import(UserControllerTest.MockConfig.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AuthenticationManager authManager;

    @Autowired
    private JWTUtil jwtUtil;

    @Autowired
    private UserService userService;

    @BeforeEach
    void setUp() {
        Mockito.reset(authManager, jwtUtil, userService);
    }

    @Test
    void loginSuccess() throws Exception {
        LoginRequest loginRequest = new LoginRequest("user", "pass");

        Authentication mockAuth = Mockito.mock(Authentication.class);
        Mockito.when(authManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(mockAuth);
        Mockito.when(jwtUtil.generateJWTToken("user")).thenReturn("mockToken");

        mockMvc.perform(post("/synassignment/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("mockToken"));
    }

    @Test
    void loginInvalidCredentials() throws Exception {
        LoginRequest loginRequest = new LoginRequest("user", "wrongpass");

        doThrow(new BadCredentialsException("Bad credentials"))
                .when(authManager).authenticate(any());

        mockMvc.perform(post("/synassignment/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void registerSuccess() throws Exception {
        RegisterRequest request = new RegisterRequest("user", "pass", "pass", 26, "email@example.com");
        RegisterResponse response = new RegisterResponse("user");

        Mockito.when(userService.register(any(RegisterRequest.class))).thenReturn(response);

        mockMvc.perform(post("/synassignment/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("user"));
    }

    @Test
    void registerUserAlreadyExists() throws Exception {
        RegisterRequest request = new RegisterRequest("user", "pass", "pass", 26, "email@example.com");

        Mockito.doThrow(new UserAlreadyExistsException("User exists")).when(userService).register(any());

        mockMvc.perform(post("/synassignment/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    void registerPasswordMismatch() throws Exception {
        RegisterRequest request = new RegisterRequest("user", "pass", "pass", 26, "email@example.com");

        Mockito.doThrow(new PasswordMismatchException()).when(userService).register(any());

        mockMvc.perform(post("/synassignment/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @TestConfiguration
    static class MockConfig {
        @Bean
        public AuthenticationManager authManager() {
            return Mockito.mock(AuthenticationManager.class);
        }

        @Bean
        public JWTUtil jwtUtil() {
            return Mockito.mock(JWTUtil.class);
        }

        @Bean
        public UserService userService() {
            return Mockito.mock(UserService.class);
        }
        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
            http
                    .csrf(csrf -> csrf.disable()) // Important for POST tests, since WebMvc does not do cors
                    .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
            return http.build();
        }
    }
}
