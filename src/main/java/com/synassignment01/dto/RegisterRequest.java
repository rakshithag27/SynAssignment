package com.synassignment01.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

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
