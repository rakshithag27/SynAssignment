package com.synassignment01.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {
    String username;
    String password;
    String confirmPassword;
    int age;
    String email;
}
