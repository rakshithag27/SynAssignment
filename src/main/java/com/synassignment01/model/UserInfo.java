package com.synassignment01.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * Entity representing user information stored in the database.
 * Implements UserDetails to integrate with Spring Security.
 */
@Setter
@Getter
@Entity
public class UserInfo implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;
    String username;
    String password;
    String email;
    int age;

    /**
     * Used for logging/debugging. Currently calls the default implementation.
     */
    @Override
    public String toString() {
        return super.toString();
    }

    /**
     * Returns the authorities granted to the user.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }
}
