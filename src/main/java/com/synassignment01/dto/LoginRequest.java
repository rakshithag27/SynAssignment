package com.synassignment01.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO representing a login request with username and password.
 */
@Getter
@Setter
@AllArgsConstructor
public class LoginRequest {

    String username;
    String password;
}
