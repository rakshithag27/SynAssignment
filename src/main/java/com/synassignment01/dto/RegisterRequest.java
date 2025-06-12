package com.synassignment01.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO representing a Register request with username, password, confirm password, age and email.
 */
@Getter
@Setter
@AllArgsConstructor
public class RegisterRequest {
    String username;
    String password;
    String confirmPassword;
    int age;
    String email;
}
