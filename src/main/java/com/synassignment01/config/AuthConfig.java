package com.synassignment01.config;

import com.synassignment01.exceptions.CustomEntrypointAuthentication;
import com.synassignment01.security.JWTAuthFilter;
import com.synassignment01.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Security configuration class for setting up custom authentication and authorization
 * using JWT, stateless sessions, and secured API endpoints.
 */
@RequiredArgsConstructor
@EnableWebSecurity
@Configuration
public class AuthConfig {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    /**
     * Configures a custom AuthenticationManager with UserDetailsService and PasswordEncoder.
     */
    @Bean
    public AuthenticationManager authManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder builder =
                http.getSharedObject(AuthenticationManagerBuilder.class);
        builder.userDetailsService(userService).passwordEncoder(passwordEncoder);
        return builder.build();
    }

    /**
     * Defines the security filter chain:
     * - Disables CSRF for stateless APIs
     * - Sets up public and protected endpoints
     * - Adds JWT filter before authentication filter
     * - Configures stateless session management
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JWTAuthFilter jwtFilter,
                                                   CustomEntrypointAuthentication entryPoint) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .exceptionHandling(ex -> ex.authenticationEntryPoint(entryPoint))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/synassignment/users/login", "/synassignment/users/register")
                        .permitAll()
                        .requestMatchers("/synassignment/images/**").authenticated()
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                );
        return http.build();
    }


}
