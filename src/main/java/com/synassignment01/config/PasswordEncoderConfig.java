package com.synassignment01.config;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Configuration class for defining the PasswordEncoder bean
 * used for hashing user passwords securely.
 */
@AllArgsConstructor
@Configuration
public class PasswordEncoderConfig {
    /**
     * Defines a PasswordEncoder bean using BCrypt hashing algorithm.
     * This is used throughout the application for encoding and verifying passwords.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
