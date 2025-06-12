package com.synassignment01.service;

import com.synassignment01.dto.RegisterRequest;
import com.synassignment01.dto.RegisterResponse;
import com.synassignment01.exceptions.PasswordMismatchException;
import com.synassignment01.exceptions.UserAlreadyExistsException;
import com.synassignment01.model.UserInfo;
import com.synassignment01.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private UserService userService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        userService = new UserService(userRepository, passwordEncoder);
    }

    @Test
    void registerSuccess() {
        RegisterRequest request = new RegisterRequest("testuser", "pass", "pass", 25, "test@example.com");

        when(userRepository.findByUsername("testuser")).thenReturn(null);
        when(passwordEncoder.encode("pass")).thenReturn("encodedPass");

        RegisterResponse response = userService.register(request);

        assertEquals("Successfully Registered!", response.getMessage());
        verify(userRepository, times(1)).save(Mockito.any(UserInfo.class));
    }

    @Test
    void registerFailsWhenUserExists() {
        RegisterRequest request = new RegisterRequest("existinguser", "pass", "pass", 25, "email@example.com");

        when(userRepository.findByUsername("existinguser")).thenReturn(new UserInfo());

        assertThrows(UserAlreadyExistsException.class, () -> userService.register(request));
        verify(userRepository, never()).save(any());
    }

    @Test
    void registerFailsWhenPasswordsMismatch() {
        RegisterRequest request = new RegisterRequest("user", "pass1", "pass2", 22, "user@example.com");

        when(userRepository.findByUsername("user")).thenReturn(null);

        assertThrows(PasswordMismatchException.class, () -> userService.register(request));
        verify(userRepository, never()).save(any());
    }

    @Test
    void loadUserByUsernameSuccess() {
        UserInfo mockUser = new UserInfo();
        mockUser.setUsername("user");
        mockUser.setPassword("encodedPass");

        when(userRepository.findByUsername("user")).thenReturn(mockUser);

        var userDetails = userService.loadUserByUsername("user");

        assertEquals("user", userDetails.getUsername());
        assertEquals("encodedPass", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_USER")));
    }

    @Test
    void loadUserByUsernameNotFound() {
        when(userRepository.findByUsername("notfound")).thenReturn(null);

        assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername("notfound"));
    }
}
