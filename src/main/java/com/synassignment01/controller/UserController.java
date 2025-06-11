package com.synassignment01.controller;

import com.synassignment01.dto.LoginRequest;
import com.synassignment01.dto.LoginResponse;
import com.synassignment01.dto.RegisterRequest;
import com.synassignment01.dto.RegisterResponse;
import com.synassignment01.exceptions.PasswordMismatchException;
import com.synassignment01.exceptions.UserAlreadyExistsException;
import com.synassignment01.service.UserService;
import com.synassignment01.security.JWTUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/synassignment/users")
@RequiredArgsConstructor
public class UserController {

    private final AuthenticationManager authManager;
    private final JWTUtil jwtUtil;
    private final UserService userService;

    @PostMapping(value = "/login",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        try {
            authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
            );
            String token = jwtUtil.generateJWTToken(loginRequest.getUsername());
            log.info("Login Successful for user {}", loginRequest.getUsername());
            return ResponseEntity.ok(new LoginResponse(token));
        } catch (BadCredentialsException e) {
            log.error("Login Failed for user {}. Please check credentials. {}", loginRequest.getUsername(),  e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            log.error("Login Failed for user {} with the following exception. {} ", loginRequest.getUsername(), e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping(value = "/register",
    consumes = MediaType.APPLICATION_JSON_VALUE,
    produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RegisterResponse> register(@RequestBody RegisterRequest registerRequest) {
        try
        {
            RegisterResponse registerResponse = userService.register(registerRequest);
            log.info("Registration Successful for user {}", registerRequest.getUsername());
            return ResponseEntity.ok(registerResponse);
        } catch (UserAlreadyExistsException | PasswordMismatchException e) {
            log.error("Registration Failed for user {} with the following exception ", registerRequest.getUsername(), e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (Exception e) {
            log.error("Registration Failed for user {} with the following exception. {} ", registerRequest.getUsername(), e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }

}